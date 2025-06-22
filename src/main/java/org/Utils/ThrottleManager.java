package org.Utils;

public class ThrottleManager {
    private static final long THROTTLE_INTERVAL_MS = 2000; // 2 seconds, adjust as needed
    private static long lastActionTime = 0;

    public static synchronized void waitIfNeeded() {
        long now = System.currentTimeMillis();
        long waitTime = lastActionTime + THROTTLE_INTERVAL_MS - now;
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastActionTime = System.currentTimeMillis();
    }
}

