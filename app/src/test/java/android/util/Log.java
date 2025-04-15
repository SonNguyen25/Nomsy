package android.util;


 // Test‐only stub so that any call to Log.d/e/i/w/v will no‑op
 // instead of throwing "Method X in android.util.Log not mocked"

public class Log {
    public static int d(String tag, String msg)                   { return 0; }
    public static int d(String tag, String msg, Throwable tr)    { return 0; }
    public static int e(String tag, String msg)                   { return 0; }
    public static int e(String tag, String msg, Throwable tr)    { return 0; }
    public static int i(String tag, String msg)                   { return 0; }
    public static int w(String tag, String msg)                   { return 0; }
    public static int w(String tag, String msg, Throwable tr)    { return 0; }
    public static int v(String tag, String msg)                   { return 0; }
}
