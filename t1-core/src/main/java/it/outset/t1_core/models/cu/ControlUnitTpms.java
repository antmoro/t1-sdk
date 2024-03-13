package it.outset.t1_core.models.cu;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import it.outset.t1_core.Constants.*;

/**
 * Created by antonio on 28/07/17.
 * <p>
 * Modello che descrive il sensore Tpms
 * <p>
 * bit 7: indica il lato dal punto di vista del guidatore (1 a sinistra, 0 a destra)
 * bit 3..6: contiene un numero da 0 a 15 e indica quale asse partendo dalla testa del vagone.
 * bit 1..2: contiene un numero da 0 a 3 e può rappresentare il numero di ruote (0=singola, 1=gemella,...).
 * Si conta a partire dalla ruota esterna.
 * bit 0: indica la presenza (1) o l'assenza (0). Probabilmente questo campo è inutile. Dipende da te.
 * <p>
 * Nel layout sono definite le view con sensorId = tpms_{side}_{axis}_{tire}
 * <p>
 * Nota che il numero che segue "TPMS:" identifica la scheda e ha la stessa funzione del numero che segue "SENS:".
 * ID=sensorId del sensore
 * L=byte di localizzazione (255 = valore non impostato)
 * P=pressione espressa in psi
 * T=temperatura espressa in °C
 * V=tensione batteria, da dividere per 100 [V]
 */

public class ControlUnitTpms {

    private static float BAR_DECIBAR = 0.1f;
    private static float PASCAL_BAR = 0.00001f;
    private static float BAR_PASCAL = 100000;
    private static float PSI_PASCAL = 6894.75729f;
    private static float PSI_BAR = 0.06894757279867757f;
    private static float BAR_PSI = 14.503773801f;
    private static float PRESSURE_MIN_PERCENTO = .25f;
    private static float PRESSURE_MAX_PERCENTO = .25f;
    private static float PRESSURE_PUNCTURED_PERCENTO = .60f;

    private String address;
    private String sensorId;
    private int position;
    private int index;
    private float pressureBarThreshold; // Soglia di pressione in [bar]
    private float pressureBarMin;
    private float pressureBarMax;
    private float pressureBarPunctured;
    private int temperatureThreshold; // Soglia di temperatura in °C
    private int temperatureMin;
    private int temperatureMax;
    private Date created;
    private Date lastUpdate;

    private float pressureBar;
    private float temperature;
    private boolean warning;

    private int stateLevel;


    public ControlUnitTpms() {
        stateLevel = CONTROL_UNIT_TPMS.LEVEL_NORMAL;

        this.pressureBarThreshold = 8.5F;
        this.setPressureBarThreshold(this.pressureBarThreshold);
        this.temperatureThreshold = 80;
        this.position = -1;
        this.temperature = 0;
        this.created = new Date();
        this.lastUpdate = created;
    }

    public ControlUnitTpms(String sensorId) {
        this();
        this.sensorId = sensorId;
    }

    public ControlUnitTpms(String sensorId, int position, int index, float pressureThreshold, int temperatureThreshold) {
        this(sensorId);
        this.position = position;
        this.index = index;
        this.pressureBarThreshold = pressureThreshold;
        this.setPressureBarThreshold(this.pressureBarThreshold);
        this.temperatureThreshold = temperatureThreshold;
    }

    public ControlUnitTpms(String address, String sensorId, int position, int index, float pressureThreshold, int temperatureThreshold) {
        this(sensorId, position, index, pressureThreshold, temperatureThreshold);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setSensorId(int b1, int b2, int b3, int b4) {
        int sensorId = (b1 & 0xFF) << 24
                | (b2 & 0xFF) << 16
                | (b3 & 0xFF) << 8
                | b4 & 0xFF;
        this.sensorId = StringUtils.leftPad(Integer.toHexString(sensorId).toUpperCase(), 8, '0');
    }

    public String getSensorIdDecimal() {
        int[] ids = getSensorIdToIntArray();
        return String.format(Locale.getDefault(),
                "%03d %03d %03d %03d", ids[0], ids[1], ids[2], ids[3]);
    }

    public int getSensorIdInteger() {
        return sensorId != null ? (int) Long.parseLong(this.sensorId, 16) : 0;
    }

    public int[] getSensorIdToIntArray() {
        int[] out = new int[4];
        long id = this.sensorId != null ? Long.parseLong(this.sensorId, 16) : 0;
        out[0] = ((int) id >> 24) & 0xFF;
        out[1] = ((int) id >> 16) & 0xFF;
        out[2] = ((int) id >> 8) & 0xFF;
        out[3] = (int) id & 0xFF;
        return out;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPosition(Side side, int axis, int tire) {
        this.position = (side == Side.RIGHT ? 0 : 1) << 7
                | axis << 3
                | tire << 1
                | 1; // bit 0 sempre a 1 = installato
    }

    public float getPressureBarThreshold() {
        return pressureBarThreshold;
    }

    public void setPressureBarThreshold(float threshold) {
        this.pressureBarThreshold = threshold;
        this.pressureBarMin = threshold - threshold * PRESSURE_MIN_PERCENTO;
        this.pressureBarMax = threshold + threshold * PRESSURE_MAX_PERCENTO;
        this.pressureBarPunctured = threshold - threshold * PRESSURE_PUNCTURED_PERCENTO;
    }

    public int getPressureDecibarThreshold() {
        return Math.round(this.pressureBarThreshold / BAR_DECIBAR);
    }

    public void setPressureDecibarThreshold(int threshold) {
        setPressureBarThreshold(threshold * BAR_DECIBAR);
    }

    public float getPressureBarMin() {
        return pressureBarMin;
    }

    public float getPressureBarMax() {
        return pressureBarMax;
    }

    public int getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public void setTemperatureThreshold(int temperatureThreshold) {
        this.temperatureThreshold = temperatureThreshold;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTire() {
        return (this.position & 0x6) >> 1;
    }

    public int getAxis() {
        return (this.position & 0x78) >> 3;
    }

    public int getStateLevel() {
        return stateLevel;
    }

    public void setStateLevel(int stateLevel) {
        this.stateLevel = stateLevel;
    }

    public float getPressureBar() {
        return pressureBar;
    }

    public void setPressureBar(float pressureBar) {
        this.pressureBar = pressureBar;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void updateThresholds(ControlUnitTpms tm) {
        this.setPressureBarThreshold(tm.getPressureBarThreshold());
        this.setTemperatureThreshold(tm.getTemperatureThreshold());
    }

    public void updateValues(float pressureBar, float temperature) {
        this.pressureBar = pressureBar;
        this.temperature = temperature;
        this.lastUpdate = new Date();
    }

    public void mapValues(ControlUnitTpms tpms) {
        this.setSensorId(tpms.getSensorId());
        this.setPosition(tpms.getPosition());
        this.setTemperatureThreshold(tpms.getTemperatureThreshold());
        this.setPressureBarThreshold(tpms.getPressureBarThreshold());
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public boolean isWarning() {
        return (isWarningSignal() || isWarningPressure() || isWarningTemperature());
    }

    public boolean isWarningPressure() {
        if (pressureBar < pressureBarPunctured)
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_PUNCTURED;
        else if (pressureBar < pressureBarMin)
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_FLAT;
        else if (pressureBar > pressureBarMax)
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_OVERPRESSURE;
        else {
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_NORMAL;
            return false;
        }
        return true;
    }

    public boolean isWarningTemperature() {
        if (getTemperature() > temperatureThreshold) {
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_OVERTEMPERATURE;
            return true;
        }
        return false;
    }

    public boolean isWarningSignal() {
        long diff = System.currentTimeMillis() - lastUpdate.getTime();
        if (diff > TimeUnit.SECONDS.toMillis(CONTROL_UNIT_TPMS.SIGNAL_TIMOUT_SECONDS)) {
            stateLevel = CONTROL_UNIT_TPMS.LEVEL_SIGNAL;
            return true;
        }
        return false;
    }

    public String getDescription() {
        return String.format(Locale.getDefault(), "Axis %d at %s: %.1f bar %.1f °C",
                getAxis() + 1, getSide() == Side.LEFT ? "left" : "right", getPressureBar(), getTemperature());
    }

    public Side getSide() {
        int result = (this.position & 0x80) >> 7;
        if (result == 1) {
            return Side.LEFT;
        }
        return Side.RIGHT;
    }

    public enum Side {
        RIGHT,
        LEFT
    }
}
