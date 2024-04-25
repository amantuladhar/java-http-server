package utils;

public class Assert {
    public static void notBlank(String type) {
        notNull(type, "Blank not expected");
    }

    public static void notBlank(String type, String message) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void notNull(T type) {
        notNull(type, "Not null expected");
    }

    public static <T> void notNull(T type, String message) {
        if (type == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
