package it.outset.t1_core.models.cu;

public class ControlUnitSensor {

    private int id;
    private int position;
    private int axisId;
    private int emptyAdc; // Peson in kg
    private int fullAdc;
    private int type;
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il numero dell'input ADC a cui è collegato il sensore.
     * @return Posizione ADC.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Imposta il numero dell'input ADC a cui è collegato il sensore.
     * @param position Posizione ADC.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    public int getEmptyAdc() {
        return emptyAdc;
    }

    public void setEmptyAdc(int emptyAdc) {
        this.emptyAdc = emptyAdc;
    }

    public int getFullAdc() {
        return fullAdc;
    }

    public void setFullAdc(int fullAdc) {
        this.fullAdc = fullAdc;
    }

    public int getAxisId() {
        return axisId;
    }

    public void setAxisId(int axisId) {
        this.axisId = axisId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ControlUnitSensor(){}

    public ControlUnitSensor(int id, int position, int emptyAdc, int fullAdc, int type) {
        this.id = id;
        this.position = position;
        this.emptyAdc = emptyAdc;
        this.fullAdc = fullAdc;
        this.type = type;
    }
}
