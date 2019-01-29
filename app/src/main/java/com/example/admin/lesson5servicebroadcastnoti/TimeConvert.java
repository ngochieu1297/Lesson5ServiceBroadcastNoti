package com.example.admin.lesson5servicebroadcastnoti;

import java.util.concurrent.TimeUnit;

public class TimeConvert {
    public static String convertMilisecondToFormatTime(long msec) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(msec) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(msec)),
                TimeUnit.MILLISECONDS.toSeconds(msec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(msec)));
    }
}
