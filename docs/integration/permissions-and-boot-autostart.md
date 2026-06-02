# Integration Guide: Permissions & Auto-Start at Boot

How to correctly wire **runtime permissions** (location, Bluetooth, notifications, storage, background location) and **automatic start of a foreground service at device boot** into an Android app that consumes `t1-core`.

Read this when adding these capabilities to a new app or branch (e.g. porting from `Cloud_5.x` to `Cloud` 6.x). It documents what `t1-core` gives you for free, what your app must supply itself, and the non-obvious traps that have already bitten us.

**Worked reference implementation:** the `T1_RS` app on branch `Cloud_5.x` (see its `docs/superpowers/specs/2026-05-…`, `…/specs/2026-06-01-background-location-boot-tracking-design.md`, and `…/specs/2026-06-02-start-on-boot-preference-design.md`).

---

## 1. What `t1-core` provides (shared — you get it for free)

### `it.outset.t1_core.PermissionHandler`

Drives the runtime permission request flow. Construct it from a `ComponentActivity` or a `Fragment`, then call `requestAppPermissions(callback)`.

```java
mPermissionHandler = new PermissionHandler(this);              // in Activity.onCreate
mPermissionHandler.requestAppPermissions(mPermissionCallback);
```

What it requests, by API level (built in `requestAppPermissions`):
- `ACCESS_COARSE_LOCATION` **and** `ACCESS_FINE_LOCATION` (API 23+) — always together (see Pitfall 2).
- `BLUETOOTH_SCAN` + `BLUETOOTH_CONNECT` (API 31+), else legacy `BLUETOOTH` + `BLUETOOTH_ADMIN`.
- `MANAGE_EXTERNAL_STORAGE` via a settings intent (API 30+), else `READ`/`WRITE_EXTERNAL_STORAGE`.
- `POST_NOTIFICATIONS` (API 33+).

Key members:
- `requestAppPermissions(PermissionCallback)` — build list + launch the multi-permission dialog.
- `requestPermissions()` — re-run the request for whatever is still ungranted (used on return from Settings — see Pitfall 5).
- `checkBluetoothEnabled()` — prompts to enable Bluetooth; result via the callback.
- `boolean needsBackgroundLocation()` — true only when API 29+, a foreground location perm is granted, and `ACCESS_BACKGROUND_LOCATION` is **not** yet granted. Returns false under MDM pre-grant, so the background step silently no-ops.
- `void requestBackgroundLocation(BackgroundLocationCallback)` — launches the **separate** background-location request (see Pitfall 3).
- An internal `isRequestingPermissions` guard prevents a second `launch()` from corrupting an in-flight request (see Pitfall 1).

`PermissionCallback`:
- `onPermissionsResult(Map<String,Boolean> results)` — per-permission grant map.
- `onBluetoothEnabledResult(boolean enabled)`.

`BackgroundLocationCallback`:
- `onResult(boolean granted)`.

### `it.outset.t1_core.models.AppPreferences`

- `String KEY_START_ON_BOOT = "pref_start_on_boot"` — preference key for the boot auto-start toggle.
- `static boolean isStartOnBootEnabled(Context)` — reads the flag with an **explicit default of `true`** (see Pitfall 6). Call this from the boot receiver.

---

## 2. What your app must supply (per-app — not shareable)

These pieces depend on app resources, the manifest, and app-specific UI, so each consuming app implements them. Copy the reference app's versions.

### 2.1 Manifest

```xml
<!-- Location: strip the maxSdkVersion cap a BLE library (blessed-kotlin) merges in (Pitfall 4) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"
    tools:remove="android:maxSdkVersion" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"
    tools:remove="android:maxSdkVersion" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"
    tools:ignore="BackgroundLocationPolicy" />

<!-- Boot auto-start -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<!-- Foreground service + the types you actually use -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
```

(`xmlns:tools` must be declared on `<manifest>`.)

### 2.2 Boot receiver

```xml
<receiver
    android:name=".BootCompleted"
    android:enabled="true"
    android:exported="true">          <!-- required to receive the system BOOT_COMPLETED broadcast -->
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

```java
public void onReceive(Context context, Intent intent) {
    if (intent == null || intent.getAction() == null) return;     // null-action guard
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        if (!AppPreferences.isStartOnBootEnabled(context)) return; // honor the user toggle
        Intent svc = new Intent(context, BluetoothService.class);
        svc.setAction(/* your STARTFOREGROUND action */);          // set it on svc, NOT on the incoming intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(svc);
        else
            context.startService(svc);
    }
}
```

### 2.3 The location foreground service

Compute the `startForeground` type from permissions actually held, and wrap it with a `dataSync`-only fallback so a boot without location/Bluetooth perms degrades instead of crashing (Android 14+):

```java
int type = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;          // always allowed (normal perm)
if (hasLocation())  type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
if (hasBtConnect()) type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE;
try {
    startForeground(NOTIF_ID, notification, type);
} catch (SecurityException e) {
    startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
}
```

Request location **from inside the running foreground service** (`FusedLocationProviderClient`), not from a bare background context.

### 2.4 Activity orchestration (graceful + warn)

```
onCreate:  new PermissionHandler(this); requestAppPermissions(cb)
onStart:   if (awaitingPermissionSettings) { awaitingPermissionSettings=false; requestPermissions(); }  // Pitfall 5

onPermissionsResult(results):
    if (critical perm denied)        -> blocking dialog -> Settings (set awaitingPermissionSettings)
    else                             -> proceedAfterPermissions()

proceedAfterPermissions():
    if (needsBackgroundLocation())   -> rationale dialog -> requestBackgroundLocation(onBgResolved)
    else                             -> clear warning; checkBluetoothEnabled()

onBgResolved(granted):
    granted ? clear warning : show persistent warning (indefinite Snackbar + Settings action)
    ALWAYS checkBluetoothEnabled()   // never block startup on background location
```

Rules: never block startup on background location; show a persistent, non-blocking warning when it is missing so a misconfigured device is obvious.

### 2.5 The "start at boot" toggle (Settings)

A `SwitchPreference` with `app:key="pref_start_on_boot"`, `app:defaultValue="@bool/pref_start_on_boot_default"` where the bool is `true`. The XML default and `isStartOnBootEnabled`'s literal default must agree (both `true`).

---

## 3. Pitfalls (symptom → cause → fix)

1. **Permissions you granted come back denied (two denials logged the same millisecond, no dialog you tapped).**
   Cause: a second `ActivityResultLauncher.launch()` fired while the first was in flight (e.g. `onCreate` requests, then `onStart` requests again before you respond). Fix: the `isRequestingPermissions` guard in `PermissionHandler` (already present); don't call `requestPermissions()` unconditionally in `onStart` — gate it (Pitfall 5).

2. **You tap "Allow" for precise location but `ACCESS_FINE_LOCATION` stays denied on Android 12+.**
   Cause: on API 31+ you must request `FINE` **together with** `COARSE`; requesting `FINE` alone is ignored. Fix: `requestAppPermissions` adds both (already present).

3. **Background location never granted / boot tracking gets no location.**
   Cause: `ACCESS_BACKGROUND_LOCATION` cannot be bundled with the foreground request — the OS requires a separate request *after* foreground is granted, surfaced as "Allow all the time". A foreground service started **from the background** (boot) only gets location if the app holds background location. Fix: `needsBackgroundLocation()` + `requestBackgroundLocation()` as a follow-on step.

4. **Location permissions vanish entirely on Android 12+ (denied instantly, absent from `dumpsys package … | grep LOCATION`).**
   Cause: the `blessed-kotlin` BLE library declares `ACCESS_FINE/COARSE_LOCATION` with `android:maxSdkVersion="30"`; the manifest merger applies that cap, dropping the perms on API 31+. The library does this because BLE scanning on API 31+ uses `BLUETOOTH_SCAN`+`neverForLocation` instead of location — but apps that need location for *other* reasons (GPS telemetry) must override it. Fix: `tools:remove="android:maxSdkVersion"` on both location permissions in the app manifest.

5. **The app re-prompts for permissions every time it returns to the foreground.**
   Cause: calling `requestPermissions()` in every `onStart`; with `MANAGE_EXTERNAL_STORAGE` (API 30+) the "all granted" early-return is never taken, so it relaunches the storage settings flow. Fix: only re-check in `onStart` when returning from the Settings screen, gated by a flag set when you sent the user there.

6. **A freshly-updated device that never opened Settings does NOT auto-start at boot.**
   Cause: reading the boot preference with a default of `false`, or relying on the `SwitchPreference` default which isn't persisted until Settings is shown — and `BootCompleted` runs before that. Fix: read with explicit default `true` (`isStartOnBootEnabled`), and keep the XML bool default `true` to match.

7. **Boot receiver never fires.**
   Cause: missing `RECEIVE_BOOT_COMPLETED`, receiver `exported="false"`, or the app is in stopped-state (never launched since install). Fix: declare the permission, set `exported="true"`, and note the app must be launched once after install before it receives broadcasts.

---

## 4. Checklist for a new app

- [ ] Add the manifest permissions in §2.1, including `tools:remove="android:maxSdkVersion"` on FINE/COARSE and `RECEIVE_BOOT_COMPLETED`.
- [ ] Construct `PermissionHandler` in `onCreate`; implement `PermissionCallback`.
- [ ] Route both proceed paths through a single `proceedAfterPermissions()` that calls `needsBackgroundLocation()` → rationale → `requestBackgroundLocation()`.
- [ ] Never block startup on background location; show a persistent warning when it's missing.
- [ ] Gate the `onStart` permission re-check behind an "awaiting settings" flag.
- [ ] Add the `BootCompleted` receiver (`exported="true"`, null-action guard, `isStartOnBootEnabled` gate).
- [ ] Type the foreground service from held permissions with a `dataSync` fallback; request location from inside the FGS.
- [ ] Add the `pref_start_on_boot` `SwitchPreference` (default ON) and ensure both defaults are `true`.
- [ ] Verify on-device: fresh install auto-starts at boot; toggle OFF stops it; precise-location grant sticks on Android 12+; `dumpsys package <pkg> | grep ACCESS_BACKGROUND_LOCATION` shows `granted=true` after the rationale flow.
