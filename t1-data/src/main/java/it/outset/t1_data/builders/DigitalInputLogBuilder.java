package it.outset.t1_data.builders;

import it.outset.t1_data.models.DigitalInputLog;

public final class DigitalInputLogBuilder {
    private final long timestampStart;
    private long timestampEnd;
    private final int inputState;

    private DigitalInputLogBuilder(long timestampStart, int inputState) {
        this.timestampStart = timestampStart;
        this.inputState = inputState;
    }

    public static DigitalInputLogBuilder newBuilder(long timestampStart, int inputState) {
        return new DigitalInputLogBuilder(timestampStart, inputState);
    }

    public DigitalInputLogBuilder timestampEnd(long timestampEnd) {
        this.timestampEnd = timestampEnd;
        return this;
    }

    public DigitalInputLog build() {
        return new DigitalInputLog(timestampStart, timestampEnd, inputState);
    }
}
