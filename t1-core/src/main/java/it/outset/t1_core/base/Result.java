package it.outset.t1_core.base;

public class Result<T> {
    private T value;
    private Exception error;

    private Result(T value, Exception error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(Exception error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getValue() {
        if (!isSuccess()) {
            throw new IllegalStateException("Cannot get value from an error result");
        }
        return value;
    }

    public Exception getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a success result");
        }
        return error;
    }
}