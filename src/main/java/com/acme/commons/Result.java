package com.acme.commons;

/**
 * Result&lt;T&gt; monad for representing success or failure of business operations
 * without using exception-based flow control.
 *
 * <p>Per P0-2024-0847 post-incident mandate, business logic MUST NOT throw exceptions
 * for flow control. Use {@code Result.success(value)} or {@code Result.failure(message)}
 * to communicate outcomes.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * // Producing a result
 * Result<Vendor> result = Result.success(vendor);
 * Result<Vendor> error  = Result.failure("Vendor not found: V001");
 *
 * // Consuming a result
 * if (result.isSuccess()) {
 *     Vendor v = result.getValue();
 * } else {
 *     String msg = result.getError();
 * }
 * }</pre>
 * </p>
 *
 * @param <T> the type of the success value
 */
public final class Result<T> {

    private final T value;
    private final String error;
    private final boolean success;

    private Result(T value, String error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    /**
     * Creates a successful result wrapping the given value.
     *
     * @param value the success value (may be null for void-like operations)
     * @param <T>   the type of the value
     * @return a successful {@code Result}
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    /**
     * Creates a failure result with the given error message.
     *
     * @param error a human-readable description of the failure
     * @param <T>   the expected success type (unused on failure)
     * @return a failed {@code Result}
     */
    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error, false);
    }

    /** Returns {@code true} if this result represents a success. */
    public boolean isSuccess() {
        return success;
    }

    /** Returns {@code true} if this result represents a failure. */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Returns the success value.
     *
     * @throws IllegalStateException if called on a failure result
     */
    public T getValue() {
        if (!success) {
            throw new IllegalStateException("Cannot get value from a failure result. Error: " + error);
        }
        return value;
    }

    /**
     * Returns the error message.
     *
     * @throws IllegalStateException if called on a success result
     */
    public String getError() {
        if (success) {
            throw new IllegalStateException("Cannot get error from a success result.");
        }
        return error;
    }

    @Override
    public String toString() {
        return success ? "Result.success(" + value + ")" : "Result.failure(" + error + ")";
    }
}
