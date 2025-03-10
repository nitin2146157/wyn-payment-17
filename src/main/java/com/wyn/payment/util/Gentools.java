package com.wyn.payment.util;

import java.util.Calendar;
import java.util.Date;

public class Gentools {

    private Gentools() {
    }

    /**
     * Date function
     * @param d1 smaller date
     * @param d2 larger date
     * @return date difference
     */
    public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * Date function
     * @param date
     * @return date (midnight time)
     */
    public static Date getDateFromDateTime(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);                           // set cal to date
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        return cal.getTime();
    }

    /**
     * isEmptyString checks for empty and null
     * @param value
     * @return
     */
    public static boolean isEmptyString(String value) {
        if(value==null)
            return true;
        if("".equalsIgnoreCase(value))
            return true;
        return false;
    }

    /**
     * parseInt parses String to primitive int, handles NFE returns 0 if caused
     * @param value
     * @return
     */
    public static int parseInt(String value) {
        int valueInt = 0 ;
        try {
            valueInt = Integer.parseInt(value);
        }catch(NumberFormatException e){
        }
        return valueInt;
    }
}
