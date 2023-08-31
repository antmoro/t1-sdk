package it.outset.t1_data.models;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmModule;

@RealmModule(library = true)
@RealmClass(embedded = true)
public class DigitalInputLog extends RealmObject {
    private long timestampStart;
    private long timestampEnd;
    private long timeInterval;
    private int inputState;

    public long getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(long timestampStart) {
        this.timestampStart = timestampStart;
    }

    public long getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(long timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getInputState() {
        return inputState;
    }

    public void setInputState(int inputState) {
        this.inputState = inputState;
    }

    public DigitalInputLog(long timestampStart, long timestampEnd, int inputState) {
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
        this.inputState = inputState;
    }
}
