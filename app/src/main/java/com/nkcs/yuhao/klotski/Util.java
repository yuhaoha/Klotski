package com.nkcs.yuhao.klotski;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String getCurrentTime()
    {
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = dateFormat.format(date);
        return time;
    }
}
