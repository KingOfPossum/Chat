package main.Common;

import java.time.LocalDateTime;

public class TimeUtils {
    public static String currentTimestamp() {
        return LocalDateTime.now().toString();
    }
}
