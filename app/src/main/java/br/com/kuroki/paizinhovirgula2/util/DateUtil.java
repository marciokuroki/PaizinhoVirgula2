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
}