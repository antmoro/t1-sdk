package it.outset.t1_core.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import it.outset.t1_core.Constants.*;
import it.outset.t1_core.models.cu.ControlUnitTpms;

/**
 * Created by antonio on 30/01/16.
 * <p>
 * bit 7: indica il lato dal punto di vista del guidatore (1 a sinistra, 0 a destra)
 * bit 3..6: contiene un numero da 0 a 15 e indica quale asse partendo dalla testa del vagone.
 * bit 1..2: contiene un numero da 0 a 3 e può rappresentare il numero di ruote (0=singola, 1=gemella,...). Si conta a partire dalla ruota esterna.
 * bit 0: indica la presenza (1) o l'assenza (0). Probabilmente questo campo è inutile. Dipende da te.
 * <p>
 * Nel layout sono definite le view con id = tpms_{side}_{axis}_{tire}
 * <p>
 * Nota che il numero che segue "TPMS:" identifica la scheda e ha la stessa funzione del numero che segue "SENS:".
 * ID=id del sensore
 * L=byte di localizzazione (255 = valore non impostato)
 * P=pressione espressa in psi
 * T=temperatura espressa in °C
 * V=tensione batteria, da dividere per 100 [V]
 */

public class TpmsConfiguration {

    private Map<String, ControlUnitTpms> tpmsSensorMap;
    private boolean hasWarning = false;
    private BitSet tpmsInstalled;
    private BitSet tpmsActive;
    private BitSet tpmsLost;

    public TpmsConfiguration() {
        this.tpmsSensorMap = new HashMap<>(CONTROL_UNIT_TPMS.MAX_TPMS_SENSORS);
        tpmsInstalled = new BitSet();
        tpmsActive = new BitSet();
        tpmsLost = new BitSet();
    }

    public Collection<ControlUnitTpms> getTpmsSensors() {
        return tpmsSensorMap.values();
    }

    public int size() {
        return tpmsSensorMap.size();
    }

    public boolean addSensor(ControlUnitTpms tpmsSensor) {
        boolean success;
        success = !tpmsSensorMap.containsKey(tpmsSensor.getSensorId());

        if (success) {
            tpmsSensorMap.put(tpmsSensor.getSensorId(), tpmsSensor);
        }
        return success;
    }

    public boolean addSensor(String id, int position) {
        boolean success;
        success = !tpmsSensorMap.containsKey(id);

        if (success) {
            ControlUnitTpms tpmsSensor = new ControlUnitTpms(id);
            tpmsSensor.setPosition(position);
            tpmsSensorMap.put(id, tpmsSensor);
        }
        return success;
    }

    public boolean existsSensor(int position) {
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            if (tpms.getPosition() == position)
                return true;
        }
        return false;
    }

    @Deprecated
    public ControlUnitTpms getSensor(int position) {
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            if (tpms.getPosition() == position)
                return tpms;
        }
        return null;
    }

    /**
     * Sostituisce un sensore con il nuovo
     *
     * @param id       Codice del sensore
     * @param position Posisizone del sensore
     * @return
     */
    public boolean addOrReplaceSensor(String id, int position) {
        boolean success;
        // Verifica se esiste un sensore nella posizione destinata
        ControlUnitTpms tpmsSensor = setTpmsSensorByPosition(position, id);
        success = (tpmsSensor != null);
        // Se il sensore non esiste viene aggiunto alla configurazione
        if (!success) {
            success = addSensor(id, position);
        }
        return success;
    }

    /**
     * Aggiunge un nuovo sensore o lo sostituisce se esiste.
     *
     * @param tpmsSensor
     */
    public ControlUnitTpms addOrReplaceSensor(ControlUnitTpms tpmsSensor) {
        // Aggiunge il sensore all'array
        ControlUnitTpms replaced = tpmsSensorMap.put(tpmsSensor.getSensorId(), tpmsSensor);
        return replaced;
    }

    public void removeSensor(String sensorId) {
        tpmsSensorMap.remove(sensorId);
    }

    @Deprecated
    public void updateSettings(ControlUnitTpms tpmsSensor) {
        ControlUnitTpms tpms = this.tpmsSensorMap.get(tpmsSensor.getSensorId());
        if (tpms != null) {
            tpms.updateThresholds(tpmsSensor);
        }
    }

    /**
     * $TPMS (dato di misura dai sensori TPMS)
     * Questo messaggio contiene l’informazione comunicata direttamente dai sensori TPMS. I campi sono i seguenti
     * {code},{loc},{pressure},{temperature},{battery},{status}
     *
     * @param data Es. 0016A4A08310,,,,24,12345678,131,25.5,-34,,0
     * @return
     */
    public ControlUnitTpms parseData(String data) {
        String tpmsSensorId = "";
        short position = 0;
        float pressure = 0;
        float temperature = 0;

        String[] params = data.split(",");

        tpmsSensorId = params[CONTROL_UNIT_TPMS.PARAM_SENSOR_ID];
        position = Short.parseShort(params[CONTROL_UNIT_TPMS.PARAM_POSITION]);

        ControlUnitTpms cuTpms = setTpmsSensorById(position, tpmsSensorId);
        if (cuTpms == null) {
            // TODO: sensore TPMS non configurato
        } else {
            if (params[CONTROL_UNIT_TPMS.PARAM_PRESSURE].length() > 0)
                pressure = Float.parseFloat(params[CONTROL_UNIT_TPMS.PARAM_PRESSURE]);
            else
                pressure = cuTpms.getPressureBar();

            if (params[CONTROL_UNIT_TPMS.PARAM_TEMPERATURE].length() > 0)
                temperature = Float.parseFloat(params[CONTROL_UNIT_TPMS.PARAM_TEMPERATURE]);
            else
                temperature = cuTpms.getTemperature();

            cuTpms.updateValues(pressure, temperature);
            boolean warning = cuTpms.isWarning();
            cuTpms.setWarning(warning);
            if (warning)
                this.hasWarning = true;
        }
        return cuTpms;
    }

    @Deprecated
    public boolean hasWarning() {
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            if (tpms.isWarning())
                return true;
        }
        return false;
    }

    public boolean hasWarningLost() {
        return !tpmsLost.isEmpty();
    }

    public ArrayList<ControlUnitTpms> getWarnings() {
        long ts = System.nanoTime();

        ArrayList<ControlUnitTpms> tpmsSensors = new ArrayList<>();
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            if (tpms.isWarning())
                tpmsSensors.add(tpms);
        }
        if (tpmsSensors.size() > 0) this.hasWarning = true;

        Log.i("TIMER", String.format("getNotWarnings: %d", System.nanoTime() - ts));

        return tpmsSensors;
    }

    public ArrayList<ControlUnitTpms> getNotWarnings() {
        long ts = System.nanoTime();

        ArrayList<ControlUnitTpms> tpmsSensors = new ArrayList<>();
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            if (!tpms.isWarning())
                tpmsSensors.add(tpms);
        }
        if (tpmsSensors.size() == tpmsSensorMap.size()) this.hasWarning = false;

        Log.i("TIMER", String.format("getNotWarnings: %d", System.nanoTime() - ts));

        return tpmsSensors;
    }

    /**
     * Cerca il sensore in una determinata posizione.
     * Se nella stessa posizione esiste un sensore allora viene sostituito.
     *
     * @param position
     * @param sensorId
     * @return Restituisce il sensore trovato nella posizione.
     */
    public ControlUnitTpms setTpmsSensorByPosition(int position, String sensorId) {
        for (ControlUnitTpms tpms :
                tpmsSensorMap.values()) {
            String originalId = tpms.getSensorId();
            int originalPosition = tpms.getPosition();
            if (originalPosition == position) {
                // Se il sensore si trova nella stessa posizione è cambiato allora aggiorna l'ID
                if (!originalId.equals(sensorId)) {
                    tpmsSensorMap.remove(originalId);
                    tpms.setSensorId(sensorId);
                    tpmsSensorMap.put(sensorId, tpms);
                }
                return tpms;
            }
        }
        return null;
    }

    /**
     * Cerca e aggiorna il sensore con ID specificato.
     *
     * @param sensorId
     * @return Restituisce il sensore.
     */
    public ControlUnitTpms setTpmsSensorById(int position, String sensorId) {
        // TODO: correggere
        ControlUnitTpms cuTpms = new ControlUnitTpms();
        if (cuTpms != null && position != cuTpms.getPosition()) {
            // Aggiorna la posizione se diversa.
            cuTpms.setPosition(position);
        }
        return cuTpms;
    }

    public static int getPositionByTag(String tag) {
        int position = 0;
        String[] fields = tag.split("_");
        if (fields[0].equals("tpms")) {
            int side = Integer.parseInt(fields[1]);
            int axis = Integer.parseInt(fields[2]);
            int tire = Integer.parseInt(fields[3]);
            position = ((side << 7) | (axis << 3) | (tire << 1) | 1);
        }
        return position;
    }

    public static String getTagByPosition(short position) {
        final String tag;

        int side = (position & 0x80) >> 7;
        int axis = (position & 0x78) >> 3;
        int tire = (position & 0x6) >> 1;
        tag = String.format(Locale.US, "tpms_%d_%d_%d", side, axis, tire);

        return tag;
    }
}
