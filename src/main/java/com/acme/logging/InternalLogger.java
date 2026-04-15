package com.acme.logging;

/**
 * InternalLogger - Organization's standard logging framework.
 * Provides automatic distributed trace ID, team ownership tag,
 * environment/region tags, and structured JSON output.
 * 
 * This is a stub for the internal InternalLogger SDK.
 * In production, this will be provided by the com.acme.logging artifact.
 */
public class InternalLogger {

    private final String name;

    private InternalLogger(String name) {
        this.name = name;
    }

    public static InternalLogger getLogger(Class<?> clazz) {
        return new InternalLogger(clazz.getName());
    }

    public void info(String message, Object... args) {
        System.out.println("[INFO] [" + name + "] " + format(message, args));
    }

    public void debug(String message, Object... args) {
        System.out.println("[DEBUG] [" + name + "] " + format(message, args));
    }

    public void warn(String message, Object... args) {
        System.out.println("[WARN] [" + name + "] " + format(message, args));
    }

    public void error(String message, Object... args) {
        System.err.println("[ERROR] [" + name + "] " + format(message, args));
    }

    public void error(String message, Throwable t) {
        System.err.println("[ERROR] [" + name + "] " + message + " - " + t.getMessage());
    }

    private String format(String message, Object... args) {
        if (args == null || args.length == 0) return message;
        String result = message;
        for (Object arg : args) {
            int idx = result.indexOf("{}");
            if (idx >= 0) {
                result = result.substring(0, idx) + String.valueOf(arg) + result.substring(idx + 2);
            }
        }
        return result;
    }
}
