package com.acme.commons;

/**
 * Result pattern for business logic flow control.
 * Replaces exception-based error handling per playbook mandate (P0-2024-0847).
 * 
 * This is a stub for the internal Result<T> SDK.
 * In production, this will be provided by the com.acme.commons artifact.
 */
public class Result<T> {

    private final T value;
    private final String error;
    private final boolean success;

    private Result(T value, String error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> fail(String error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getValue() {
        return value;
    }

    public String getError() {
        return error;
    }
}
