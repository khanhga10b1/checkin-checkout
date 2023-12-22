package bv.utils;

public class ObjectUtils {
    private ObjectUtils() {
    }

    public static <V> V getIgnoreException(Callback<V> callback, V resultIfNull, String message) {
        try {
            return callback.run();
        } catch (Exception e) {
            if (message != null) {
                System.err.println(message);
            }
            return resultIfNull;
        }
    }

    public static <V> V getIgnoreException(Callback<V> callback) {
        return getIgnoreException(callback, null, null);
    }

    public static <V> V getIgnoreException(Callback<V> callback, V resultIfNull) {
        return getIgnoreException(callback, resultIfNull, null);
    }

    public static void callFunction(NoParamCallback callback, ErrorMessage message) {
        try {
            callback.run();
        } catch (Exception e) {
            if (message != null) {
                message.show(e.getMessage());
            }
        }
    }

    public static void callFunction(NoParamCallback callback) {
        callFunction(callback, null);
    }

    public static boolean nullOrBlank(String text) {
        return text == null || text.isBlank();
    }

    public static boolean hasText(String text) {
        return !nullOrBlank(text);
    }

}
