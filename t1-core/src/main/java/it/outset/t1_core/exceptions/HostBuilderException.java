package it.outset.t1_core.exceptions;

public class HostBuilderException extends RuntimeException {
    public HostBuilderException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
