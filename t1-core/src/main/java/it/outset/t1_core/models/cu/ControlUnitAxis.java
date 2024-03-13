package it.outset.t1_core.models.cu;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import it.outset.t1_core.Constants.*;

public class ControlUnitAxis {

    private int id;
    private int installed; // Bitmask ADC 8 bit
    private int emptyPressure; // Pressione in bar
    private int weightEmpty; // Peson in kg
    private int weightFull;
    private int weightInput;
    private int fullPressure;
    private List<ControlUnitSensor> sensors;

    public ControlUnitAxis() {
        sensors = new ArrayList<>();
    }

    /**
     * Crea un nuovo Asse con assegnazione dell'ID.
     *
     * @param id Indice dell'asse con inizio da 0.
     */
    public ControlUnitAxis(int id) {
        this();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAxisId() {
        return this.id + 1;
    }

    public int getInstalled() {
        return this.installed;
    }

    public void setInstalled(int installed) {
        this.installed = installed;
    }

    public int getEmptyPressure() {
        return emptyPressure;
    }

    public void setEmptyPressure(int emptyPressure) {
        this.emptyPressure = emptyPressure;
    }

    public int getWeightEmpty() {
        return weightEmpty;
    }

    /**
     * Imposta il peso a vuoto dell'asse suddiviso per gli ADC.
     *
     * @param weightEmpty
     */
    public void setWeightEmpty(int weightEmpty) {
        this.weightEmpty = weightEmpty;

    }

    public int getFullPressure() {
        return fullPressure;
    }

    public void setFullPressure(int fullPressure) {
        this.fullPressure = fullPressure;
    }

    public int getWeightFull() {
        return weightFull;
    }

    public void setWeightFull(int weightFull) {
        this.weightFull = weightFull;
    }

    @SuppressLint("DefaultLocale")
    public String getName() {
        return String.format("Asse %d", getAxisId());
    }

    public void update(ControlUnitAxis controlUnitAxis) {
    }

    public int getWeightInput() {
        return weightInput;
    }

    public void setWeightInput(int weightInput) {
        this.weightInput = weightInput;
    }

    /**
     * Assegna i valori di input dei sensori.
     * @param values
     */
    public void setValues(int[] values) {
        for (int i = 0; i < sensors.size(); i++) {
            ControlUnitSensor sensor = sensors.get(i);
            if (i < values.length) {
                sensor.setValue(values[i]);
            } else {
                sensor.setValue(0);
            }
        }
    }

    public List<ControlUnitSensor> getSensors() {
        return sensors;
    }

    public ControlUnitSensor getSensor(int id) {
        if (id < sensors.size())
            return sensors.get(id);

        return null;
    }

    public void addSensor(ControlUnitSensor sensor) {
        if (sensors.size() < CONTROL_UNIT.ADC_AXES_MAX) {
            sensor.setId(sensors.size());
            this.sensors.add(sensor);
        }
    }

    public void addSensor(int position, int emptyAdc, int fullAdc) {
        if (sensors.size() < CONTROL_UNIT.ADC_AXES_MAX) {
            ControlUnitSensor cuSensor = new ControlUnitSensor(sensors.size(), position, emptyAdc, fullAdc, 0);
            cuSensor.setAxisId(this.id);
            this.sensors.add(cuSensor);
        }
    }

    /**
     * Restituisce il numero di ADC/Sensor installati.
     * @return Conteggio totale.
     */
    public int getAdcCount() {
        int count = 0;
        for (int i = 0; i < CONTROL_UNIT.ADC_MAX; i++) {
            int bitmask = (byte) (1 << i);
            count += (installed & bitmask) > 0 ? 1 : 0;
        }
        return count;
    }

    public boolean hasAdc(int position) {
        int bitmask = (byte) (1 << position);
        return (this.installed & bitmask) > 0;
    }
}
