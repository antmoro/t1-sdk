package it.outset.t1_core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class to request app permissions and check if Bluetooth is enabled.
 * Example usage:
 *     private void requestPermissions() {
 *         mPermissionsHelper.requestAppPermissions(new PermissionHandler.PermissionCallback() {
 *             @Override
 *             public void onPermissionsResult(Map<String, Boolean> results) {
 *                 List<String> deniedPermissions = new ArrayList<>();
 *                 for (Map.Entry<String, Boolean> entry : results.entrySet()) {
 *                     if (!entry.getValue()) {
 *                         deniedPermissions.add(entry.getKey());
 *                     }
 *                 }
 *
 *                 if (deniedPermissions.isEmpty()) {
 *                     mPermissionsHelper.checkBluetoothEnabled();
 *                 } else {
 *                     // Some permissions were denied
 *                     handleDeniedPermissions(deniedPermissions);
 *                 }
 *             }
 *
 *             @Override
 *             public void onBluetoothEnabledResult(boolean enabled) {
 *                 mainViewModel.setIsBluetoothEnabled(enabled);
 *                 if (!enabled) {
 *                     Toast.makeText(MainActivity.this, R.string.failed_to_enable_bluetooth, Toast.LENGTH_SHORT).show();
 *                 }
 *                 initializeApp();
 *             }
 *         });
 *     }
 *
 *     private void handleDeniedPermissions(List<String> deniedPermissions) {
 *         // Handle denied permissions
 *         for (String permission : deniedPermissions) {
 *             if ("MANAGE_EXTERNAL_STORAGE".equals(permission)) {
 *                 showManageExternalStorageExplanation();
 *             } else {
 *                 showExplanationForPermission(permission);
 *             }
 *         }
 *     }
 *
 *     private void showExplanationForPermission(String permission) {
 *         // Show a dialog or toast explaining why the permission is needed
 *         // You might want to provide an option to request the permission again
 *         Timber.tag(TAG).w("Permission denied: %s", permission);
 *     }
 *
 *     private void showManageExternalStorageExplanation() {
 *         // Explain why MANAGE_EXTERNAL_STORAGE is needed and guide the user to settings
 *         Timber.tag(TAG).w("MANAGE_EXTERNAL_STORAGE");
 *     }
 */
public class PermissionHandler {
    private final Context context;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Intent> manageStorageLauncher;
    private ActivityResultLauncher<Intent> enableBtLauncher;
    private PermissionCallback callback;
    private List<String> requiredPermissions;
    private boolean needsManageExternalStorage = false;

    public interface PermissionCallback {
        void onPermissionsResult(Map<String, Boolean> results);

        void onBluetoothEnabledResult(boolean enabled);
    }

    public PermissionHandler(ComponentActivity activity) {
        this.context = activity;
        initPermissionLauncher(activity);
        initManageStorageLauncher(activity);
        initEnableBtLauncher(activity);
    }

    public PermissionHandler(Fragment fragment) {
        this.context = fragment.getContext();
        initPermissionLauncher(fragment);
        initManageStorageLauncher(fragment);
        initEnableBtLauncher(fragment);
    }

    private void initPermissionLauncher(ComponentActivity activity) {
        permissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::handlePermissionResult
        );
    }

    private void initPermissionLauncher(Fragment fragment) {
        permissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::handlePermissionResult
        );
    }

    private void initManageStorageLauncher(ComponentActivity activity) {
        manageStorageLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkManageExternalStoragePermission()
        );
    }

    private void initManageStorageLauncher(Fragment fragment) {
        manageStorageLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkManageExternalStoragePermission()
        );
    }

    private void initEnableBtLauncher(ComponentActivity activity) {
        enableBtLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    callback.onBluetoothEnabledResult(result.getResultCode() == Activity.RESULT_OK);
                }
        );
    }

    private void initEnableBtLauncher(Fragment fragment) {
        enableBtLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> checkBluetoothEnabled()
        );
    }

    @SuppressLint("ObsoleteSdkInt")
    public void requestAppPermissions(PermissionCallback callback) {
        this.callback = callback;
        this.requiredPermissions = new ArrayList<>();

        // Add required permissions based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requiredPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(android.Manifest.permission.BLUETOOTH_SCAN);
            requiredPermissions.add(android.Manifest.permission.BLUETOOTH_CONNECT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requiredPermissions.add(android.Manifest.permission.BLUETOOTH);
            requiredPermissions.add(android.Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            needsManageExternalStorage = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requiredPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            requiredPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        requestPermissions();
    }

    public void checkBluetoothEnabled() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            // Bluetooth is not enabled
            requestBluetoothEnable();
        } else {
            callback.onBluetoothEnabledResult(true);
        }
    }

    public void requestPermissions() {
        // Filter out permissions that are already granted
        List<String> permissionsToRequest = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            permissionsToRequest = requiredPermissions.stream()
                    .filter(permission -> ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    .collect(Collectors.toList());
        } else {
            permissionsToRequest = new ArrayList<>();
            for (String permission : requiredPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
        }

        // Check if all permissions are already granted
        if (permissionsToRequest.isEmpty() && !needsManageExternalStorage) {
            // All permissions are already granted
            Map<String, Boolean> results = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                results = requiredPermissions.stream()
                        .collect(Collectors.toMap(permission -> permission, permission -> true));
            } else {
                results = new HashMap<>();
                for (String permission : requiredPermissions) {
                    results.put(permission, true);
                }
            }
            callback.onPermissionsResult(results);
        } else if (!permissionsToRequest.isEmpty()) {
            // Request standard permissions
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            // Only MANAGE_EXTERNAL_STORAGE is needed
            requestManageExternalStorage();
        }
    }

    private void handlePermissionResult(Map<String, Boolean> results) {
        if (needsManageExternalStorage) {
            requestManageExternalStorage();
        } else {
            callback.onPermissionsResult(results);
        }
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtLauncher.launch(enableBtIntent);
    }

    private void requestManageExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                manageStorageLauncher.launch(intent);
            } else {
                checkManageExternalStoragePermission();
            }
        }
    }

    private void checkManageExternalStoragePermission() {
        Map<String, Boolean> results = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            results = new HashMap<>(requiredPermissions.stream()
                    .collect(Collectors.toMap(permission -> permission,
                            permission -> ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)));
        } else {
            results = new HashMap<>();
            for (String permission : requiredPermissions) {
                results.put(permission, ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            results.put("MANAGE_EXTERNAL_STORAGE", Environment.isExternalStorageManager());
        }

        callback.onPermissionsResult(results);
    }
}