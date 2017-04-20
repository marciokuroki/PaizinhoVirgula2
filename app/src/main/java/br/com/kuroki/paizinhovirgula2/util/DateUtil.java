package br.com.kuroki.paizinhovirgula2.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by marciokuroki on 27/01/17.
 */

public class DateUtil {

    /**
     * "dd/MM/yyyy"
     *
     * */
    public static String converteLongToDate(Long time, String format) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    public static Date formataData(String data) throws Exception {
        if (data == null || data.equals(""))
            return null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            date = formatter.parse(data);
        } catch (ParseException e) {
            throw e;
        }
        return date;
    }

    public static Date formataHora(String data) throws Exception {
        if (data == null || data.equals(""))
            return null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            date = formatter.parse(data);
        } catch (ParseException e) {
            throw e;
        }
        return date;
    }

    public static Long converterStringToLong(String duration) {
        String horaSelecionada[] = duration.split(":");

        long hora = 0l;
        long minuto = 0l;
        long segundo  = 0l;

        if (horaSelecionada.length > 2) {
            hora = new Integer(horaSelecionada[0])  * 3600000;
            minuto = new Integer(horaSelecionada[1])  * 60000;
            segundo = new Integer(horaSelecionada[2])  * 1000;
        }else if (horaSelecionada.length == 2) {
            minuto = new Integer(horaSelecionada[0])  * 60000;
            segundo = new Integer(horaSelecionada[1])  * 1000;
        }else {
            segundo = new Integer(horaSelecionada[0])  * 1000;
        }

        return hora + minuto + segundo;
    }

    public static String converterLongToString(Long durationInMilis) {
        if (durationInMilis > 3600000) {
            long segundos = (durationInMilis / 1000) % 60;
            long minutos = (durationInMilis / 60000) % 60;
            long horas  = durationInMilis / 3600000;

            return String.format("%02d:%02d:%02d", horas, minutos, segundos);
        }else {
            long segundos = (durationInMilis / 1000) % 60;
            long minutos = (durationInMilis / 60000) % 60;
            return String.format("%02d:%02d:%02d", 0l, minutos, segundos);
        }
    }
}