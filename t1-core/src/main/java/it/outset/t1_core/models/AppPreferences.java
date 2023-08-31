package it.outset.t1_core.models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import it.outset.t1_core.Constants;
import it.outset.t1_core.R;
import it.outset.t1_core.builders.HostSettingsBuilder;

/**
 * <a href="https://yakivmospan.com/blog/best-practice-shared-preferences">Best Practice - SharedPreferences</a>
 */
public abstract class AppPreferences {

    protected SharedPreferences sharedPreferences;
    protected Context context;

    private final static String KEY_PREFERENCES_VERSION = "preferences_version";
    private final static int PREFERENCES_VERSION = 1;

    public static final String KEY_PERIPHERAL_NAME = "peripheral_name";
    public static final String KEY_PERIPHERAL_ADDRESS = "peripheral_address";
    public static final String KEY_CONNECTION_TIMER_DELAY = "connection_timer_delay";

    public static final String KEY_AUTOCONNECT_ENABLED = "pref_autoconnect_enabled";
    public static final String KEY_AUTOCONNECT_MAX_RETRIES = "pref_autoconnect_max_retries";

    public static final String KEY_ALERT_SOUND_ENABLED = "pref_alert_sound";
    public static final String KEY_ALERT_SOUND_VOLUME = "pref_alert_sound_volume";
    public static final String KEY_ALERT_VIBRATOR_ENABLED = "pref_alert_vibrator";

    public final static String KEY_EQUIPMENT_ENABLED = "pref_equipment_enabled";
    public final static String KEY_EQUIPMENT_SCAN_TIME = "pref_equipment_scan_time";
    public final static String KEY_DIGITAL_INPUT_ENABLED = "pref_digital_input_enabled";

    public static final String KEY_CLOUD_SERVICE_ENABLED = "pref_cloud_service_enabled";
    public static final String KEY_CLOUD_HOST_SETTINGS = "pref_cloud_host_settings";
    public static final String KEY_PRIVATE_HOST_SETTINGS = "pref_private_host_settings";
    public final static String KEY_PRIVATE_HOST_ENABLED = "pref_private_host_enabled";
    public static final String KEY_CLOUD_SETTINGS = "pref_cloud_settings"; // Cloud remote.

    // Dispositivo Bluetooth
    public String peripheralName;
    public String peripheralAddress;
    // Modalità di funzionamento
    public int connectionTimerDelay;
    // Impostazioni dispositivo
    public boolean locationEnabled;
    public boolean alertSoundEnabled;
    public int alertSoundVolume;
    public boolean alertVibratorEnabled;
    public boolean digitalInputEnabled;

    // Cloud
    public HostSettings cloudHostSettings;
    public HostSettings privateHostSettings;

    public static void migrate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        checkPreferences(preferences);
    }

    private static void checkPreferences(SharedPreferences sharedPreferences) {
        final double oldVersion = sharedPreferences.getInt(KEY_PREFERENCES_VERSION, 1);

        if (oldVersion < PREFERENCES_VERSION) {
            final SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.putInt(KEY_PREFERENCES_VERSION, PREFERENCES_VERSION);
            edit.commit();
        }
    }

    public boolean getAutoconnectEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTOCONNECT_ENABLED, context.getResources().getBoolean(R.bool.pref_autoconnect_enabled_default));
    }

    public void setAutoconnectEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_AUTOCONNECT_ENABLED, enabled).apply();
    }

    public int getAutoconnectMaxRetries() {
        return sharedPreferences.getInt(KEY_AUTOCONNECT_MAX_RETRIES, context.getResources().getInteger(R.integer.pref_autoconnect_max_retries_value_default));
    }

    public void setKeyAutoconnectMaxRetries(int value) {
        sharedPreferences.edit().putInt(KEY_AUTOCONNECT_MAX_RETRIES, value).apply();
    }

    public void update(SharedPreferences sp, String key) {
        switch (key) {
            case KEY_PERIPHERAL_NAME:
                peripheralName = sp.getString(key, "No name");
                break;
            case KEY_PERIPHERAL_ADDRESS:
                peripheralAddress = sp.getString(key, Constants.BLUETOOTH.MAC_ADDRESS_EMPTY);
                break;
            case KEY_CONNECTION_TIMER_DELAY:
                try {
                    connectionTimerDelay = Integer.parseInt(sp.getString(key, "5"));
                } catch (NumberFormatException e) {
                    connectionTimerDelay = 5;
                }
                break;
            case KEY_ALERT_SOUND_ENABLED:
                alertSoundEnabled = sp.getBoolean(key, true);
                break;
            case KEY_ALERT_SOUND_VOLUME:
                alertSoundVolume = sp.getInt(key, 50);
                break;
            case KEY_ALERT_VIBRATOR_ENABLED:
                alertVibratorEnabled = sp.getBoolean(key, true);
                break;
            case KEY_CLOUD_HOST_SETTINGS: {
                String json = sp.getString(key, null);
                if (json != null) {
                    cloudHostSettings = new Gson().fromJson(json, HostSettings.class);
                }
                break;
            }
            case KEY_PRIVATE_HOST_SETTINGS: {
                String json = sp.getString(key, null);
                if (json != null) {
                    privateHostSettings = new Gson().fromJson(json, HostSettings.class);
                }
                break;
            }
            case KEY_DIGITAL_INPUT_ENABLED: {
                digitalInputEnabled = sp.getBoolean(key, false);
                break;
            }
            case KEY_CLOUD_SETTINGS: {
                String json = sp.getString(key, null);
                if (json != null) {
                    CloudSettings settings = new Gson().fromJson(json, CloudSettings.class);
                    privateHostSettings = HostSettingsBuilder.newBuilder(settings.hostUrl).build();
                }
            }
            default:
                break;
        }
    }

    /**
     * Salva la configurazione caricata dal file config.json nelle preferenze dell'app.
     *
     * @param sp            SharedPreferences.
     * @param configuration AppPrefences generate dal file config.json.
     */
    public void saveConfiguration(SharedPreferences sp, AppPreferences configuration) {
        SharedPreferences.Editor editor = sp.edit();

        if (configuration.connectionTimerDelay >= 0 && configuration.connectionTimerDelay <= 60000)
            editor.putInt(KEY_CONNECTION_TIMER_DELAY, configuration.connectionTimerDelay);

        editor.putBoolean(KEY_ALERT_SOUND_ENABLED, configuration.alertSoundEnabled);

        if (configuration.alertSoundVolume >= 0 && configuration.alertSoundVolume <= 100)
            editor.putInt(KEY_ALERT_SOUND_VOLUME, configuration.alertSoundVolume);

        editor.putBoolean(KEY_ALERT_VIBRATOR_ENABLED, configuration.alertVibratorEnabled);

        if (configuration.cloudHostSettings != null) {
            String json = new Gson().toJson(configuration.cloudHostSettings);
            editor.putString(KEY_CLOUD_HOST_SETTINGS, json);
        }

        if (configuration.privateHostSettings != null) {
            String json = new Gson().toJson(configuration.privateHostSettings);
            editor.putString(KEY_PRIVATE_HOST_SETTINGS, json);
        }

        editor.putBoolean(KEY_DIGITAL_INPUT_ENABLED, configuration.digitalInputEnabled);

        editor.apply();
    }

    public boolean isAutoConnectEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTOCONNECT_ENABLED, context.getResources().getBoolean(R.bool.pref_autoconnect_enabled_default));
    }

    public static Peripheral getPeripheral(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return new Peripheral(sp.getString(KEY_PERIPHERAL_NAME, "No name"), sp.getString(KEY_PERIPHERAL_ADDRESS, Constants.BLUETOOTH.MAC_ADDRESS_EMPTY));
    }

    public static void setPeripheral(Context context, Peripheral peripheral) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_PERIPHERAL_NAME, peripheral.name);
        editor.putString(KEY_PERIPHERAL_ADDRESS, peripheral.address);
        editor.apply();
    }

    public static int getAlertSoundVolume(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(KEY_ALERT_SOUND_VOLUME, 50);
    }

    public static int getInteger(SharedPreferences sp, String key, String defaultValue) {
        try {
            String s = sp.getString(key, null);
            if (s == null)
                return Integer.parseInt(defaultValue);
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Deprecated
    public static int getInteger(Context context, String key, String defaultValue) {
        try {
            String s = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
            if (s == null)
                return Integer.parseInt(defaultValue);
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
