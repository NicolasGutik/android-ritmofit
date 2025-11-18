package com.ritmofit.app.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class FechaUtils {
    private FechaUtils(){}
    public static OffsetDateTime parseIsoOffset(String iso) {
        // "2025-11-20T15:00:00.000-03:00"
        return OffsetDateTime.parse(iso);
    }
    public static String formatDiaHoraCorta(OffsetDateTime odt) {
        return odt.format(DateTimeFormatter.ofPattern("EEE dd/MM HH:mm"));
    }
    // Validacion simple de franjas "HH:mm"
    public static boolean esHoraValida(String hhmm) {
        return hhmm != null && hhmm.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }
}
