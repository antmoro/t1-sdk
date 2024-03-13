package it.outset.t1_core.models.hr;

import android.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import it.outset.t1_core.Constants.*;

public class HoldingRegister {

    public static final int HEADER_LENGTH = 7;
    public static final int HR_LENGTH_MAX = 40;

    private static final int HR_ROLE_OFFSET = 0; // 0x0000
    private static final int HR_AXES_OFFSET = 1; // 0x0000 0000
    private static final int HR_AXES_LENGTH = 2;
    private static final int HR_CALIBRATION_OFFSET = 3; // [0...31] = 0x0000 0000 0000 0000
    private static final int HR_CALIBRATION_LENGTH = 4;
    private static final int HR_SENSORS_TYPE_OFFSET = 35;
    private static final int HR_SENSORS_TYPE_LENGTH = 2;
    private static final int HR_ADJUST_WEIGHT_OFFSET = 39;
    private static final int HR_CALIBRATION_ADC_MIN = 0;
    private static final int HR_CALIBRATION_ADC_MAX = 1;
    private static final int HR_CALIBRATION_WEIGHT_EMPTY = 2;
    private static final int HR_CALIBRATION_WEIGHT_FULL = 3;

    private int offset;
    private int length;
    private int[] data;
    private int[] dataUndo;
    private int[] adcInstalled; // Bitmap degli ADC installati per ogni asse
    private int[] sensorsType;

    public HoldingRegister() {
        this.data = new int[HR_LENGTH_MAX];
        this.dataUndo = new int[HR_LENGTH_MAX];
        this.adcInstalled = new int[CONTROL_UNIT.AXES_MAX];
        this.sensorsType = new int[CONTROL_UNIT.ADC_MAX];
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public int[] getData() {
        return data;
    }

    public int getRole() {
        return data[HR_ROLE_OFFSET];
    }

    public void setRole(int role) {
        data[HR_ROLE_OFFSET] = role;
    }

    private int getCalibrationPosition(int position, int offset) {
        return data[HR_CALIBRATION_OFFSET + position * HR_CALIBRATION_LENGTH + offset];
    }

    public int[] getAdcInstalled() {
        return adcInstalled;
    }

    public int getAdcInstalledByAxis(int position) {
        if (position >= CONTROL_UNIT.AXES_MAX)
            return 0;

        return adcInstalled[position];
    }

    public void setAdcInstalledPerAxis(int position, int installed) {
        if (position < CONTROL_UNIT.AXES_MAX) {
            adcInstalled[position] = installed;
            // ADC for each Axis
            for (int i = 0; i < HR_AXES_LENGTH; i++) {
                int n = i * 2;
                data[HR_AXES_OFFSET + i] = (adcInstalled[n] & 0xFF) | (adcInstalled[n + 1] << 8 & 0xFF00);
            }
        }
    }

    public int getAdcMin(int position) {
        if (position >= CONTROL_UNIT.ADC_MAX)
            return 0;

        return getCalibrationPosition(position, HR_CALIBRATION_ADC_MIN);
    }

    public int getAdcMax(int position) {
        if (position >= CONTROL_UNIT.ADC_MAX)
            return 0;

        return getCalibrationPosition(position, HR_CALIBRATION_ADC_MAX);
    }

    public void setAdcMin(int position, int adc) {
        if (position < CONTROL_UNIT.ADC_MAX) {
            data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_ADC_MIN + HR_CALIBRATION_LENGTH * position] = adc;
        }
    }

    public void setAdcMax(int position, int adc) {
        if (position < CONTROL_UNIT.ADC_MAX) {
            data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_ADC_MAX + HR_CALIBRATION_LENGTH * position] = adc;
        }
    }

    public int getWeightEmpty(int position) {
        if (position >= CONTROL_UNIT.ADC_MAX)
            return 0;

        return getCalibrationPosition(position, HR_CALIBRATION_WEIGHT_EMPTY);
    }

    public int getWeightFull(int position) {
        if (position >= CONTROL_UNIT.ADC_MAX)
            return 0;

        return getCalibrationPosition(position, HR_CALIBRATION_WEIGHT_FULL);
    }

    public void setAxisWeightEmpty(int axisPosition, int weight) {
        List<Integer> adcs = getAdcByInstalledList(adcInstalled[axisPosition]);
        if (adcs.size() > 0) {
            int weigthPart = weight / adcs.size();
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i) * HR_CALIBRATION_LENGTH;
                data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_WEIGHT_EMPTY + adcPosition] = weigthPart;
            }
        }
    }

    public void setAxisWeightFull(int axisPosition, int weight) {
        List<Integer> adcs = getAdcByInstalledList(adcInstalled[axisPosition]);
        if (adcs.size() > 0) {
            int weigthPart = weight / adcs.size();
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i) * HR_CALIBRATION_LENGTH;
                data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_WEIGHT_FULL + adcPosition] = weigthPart;
            }
        }
    }

    public int getAxisWeightEmpty(int axisPosition) {
        int weight = 0;
        List<Integer> adcs = getAdcByInstalledList(adcInstalled[axisPosition]);
        if (adcs.size() > 0) {
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i) * HR_CALIBRATION_LENGTH;
                weight += data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_WEIGHT_EMPTY + adcPosition];
            }
        }
        return weight;
    }

    public int getAxisWeightFull(int axisPosition) {
        int weight = 0;
        List<Integer> adcs = getAdcByInstalledList(adcInstalled[axisPosition]);
        if (adcs.size() > 0) {
            for (int i = 0; i < adcs.size(); i++) {
                int adcPosition = adcs.get(i) * HR_CALIBRATION_LENGTH;
                weight += data[HR_CALIBRATION_OFFSET + HR_CALIBRATION_WEIGHT_FULL + adcPosition];
            }
        }
        return weight;
    }

    public int getSensorType(int position) {
        if (position >= CONTROL_UNIT.ADC_MAX)
            return 0;

        int i = position / 2;
        int n = position % 2;
        return data[HR_SENSORS_TYPE_OFFSET + i] >> (n * 8) & 0xFF;
    }

    public void setSensorType(int position, int type) {
        if (position < CONTROL_UNIT.ADC_MAX) {
            sensorsType[position] = type;
            int i = position / 2;
            int n = i << 1;
            data[HR_SENSORS_TYPE_OFFSET + i] = (sensorsType[n] & 0xFF) | (sensorsType[n + 1] << 8 & 0xFF00);
        }
    }

    /**
     * Restituisce gli ADC installati
     *
     * @param installed
     * @return
     */
    public List<Integer> getAdcByInstalledList(int installed) {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < CONTROL_UNIT.ADC_MAX; i++) {
            int adc = installed & (1 << i);
            if (adc > 0) {
                items.add(i);
            }
        }
        return items;
    }

    /**
     * Parsing di tutti gli holding register in uso
     *
     * @param base64 Stringa da elaborare
     */
    public void parse(String base64) throws DecoderException {
        if (base64.length() < 7)
            return;

        // HRr0000 valori ASCII HEX
//        byte[] header = DataHelper.HexString2Bytes(base64.substring(3, 7));
        byte[] header = Hex.decodeHex(base64.substring(3, 7));
        offset = header[0];
        length = header[1];
        if (length == 0 || length - offset <= 0)
            return;

        byte[] payload = Base64.decode(base64.substring(HEADER_LENGTH), Base64.NO_WRAP);
        for (int i = 0; i < length; i++) {
            int r = i * 2;
            // Converte le coppie di byte in word Big endian
            data[i] = (payload[r] & 0x00FF) | ((payload[r + 1] << 8) & 0xFF00);
        }

        // Crea una copia di ripristino degli Holding Register
//        System.arraycopy(this.data, offset, this.dataUndo, offset, this.data.length);

        // ADC for each Axis
        if (offset <= HR_AXES_OFFSET && offset + length >= HR_AXES_OFFSET + HR_AXES_LENGTH) {
            for (int i = 0; i < CONTROL_UNIT.AXES_MAX; i++) {
                int r = 8 * (i % 2);
                adcInstalled[i] = (data[HR_AXES_OFFSET + i / 2] >> r) & 0xFF;
            }
        }

        // Sensor type for each ADC
        if (offset <= HR_SENSORS_TYPE_OFFSET && offset + length >= HR_SENSORS_TYPE_OFFSET + HR_SENSORS_TYPE_LENGTH) {
            for (int i = 0; i < CONTROL_UNIT.ADC_MAX; i++) {
                int r = 8 * (i % 2);
                sensorsType[i] = (data[HR_SENSORS_TYPE_OFFSET + i / 2] >> r) & 0xFF;
            }
        }
    }

    public String toBase64() {
        return toBase64(0, HR_LENGTH_MAX);
    }

    public String toBase64WithHeader() {
        return String.format("HRr%02X%02X%s", 0, HR_LENGTH_MAX, toBase64(0, HR_LENGTH_MAX));
    }

    public String toBase64(int offset, int count) {
        byte[] payload = new byte[count * 2];
        for (int i = 0; i < data.length && i < count; i++) {
            int n = i * 2;
            payload[n] = (byte) (data[i + offset] & 0x00FF);
            payload[n + 1] = (byte) ((data[i + offset] >> 8) & 0xFF);
        }
        return Base64.encodeToString(payload, 0, payload.length, Base64.NO_WRAP);
    }

    public void copy(HoldingRegister holdingRegister, int offset) {
        copy(holdingRegister, offset, holdingRegister.length - offset);
    }

    public void copy(HoldingRegister holdingRegister, int offset, int length) {
        if (length - offset < 0)
            return;

        System.arraycopy(holdingRegister.data, offset, this.data, offset, length - offset);
        System.arraycopy(holdingRegister.adcInstalled, 0, this.adcInstalled, 0, this.adcInstalled.length);
    }

    public boolean compare(HoldingRegister holdingRegister, int offset, int count) {
        if (offset + count > this.data.length)
            return false;

        for (int i = offset; i < offset + count; i++) {
            if (holdingRegister.data[i] != this.data[i])
                return false;
        }
        return true;
    }

    public boolean compare(int[] block, int offset) {
        if (offset + block.length > this.data.length)
            return false;

        for (int i = 0; i < block.length; i++) {
            if (block[i] != this.data[i + offset])
                return false;
        }
        return true;
    }

    public void clear() {
        this.data = new int[HR_LENGTH_MAX];
    }

    /**
     * Blocco di dati
     */

    public static int[] parseHeader(String header) throws DecoderException {
        int[] empty = new int[]{0, 0};
        if (header.length() < 7)
            return empty;

        // HRr0000 valori ASCII HEX
        byte[] payload = Hex.decodeHex(header.substring(3, 7));
        int offset = payload[0];
        int length = payload[1];
        if (offset == 0 && length == 0)
            return empty;

        return new int[]{offset, length};
    }

    /**
     * Parsing di un blocco degli holding register
     *
     * @param base64 Stringa con header e payload in Base64
     * @return
     */
    public static int[] parseBlock(String base64) throws DecoderException {
        if (base64.length() < 7)
            return new int[0];

        // HRr0000 valori ASCII HEX
        byte[] header = Hex.decodeHex(base64.substring(3, 7));
        int offset = header[0];
        int length = header[1];
        if (length == 0 || length - offset <= 0)
            return new int[0];

        byte[] payload = Base64.decode(base64.substring(HEADER_LENGTH), Base64.NO_WRAP);
        int[] block = new int[length];
        for (int i = 0; i < length; i++) {
            int r = i * 2;
            block[i] = (payload[r] << 8 & 0xFF00) | (payload[r + 1] & 0xFF);
        }
        return block;
    }
}
