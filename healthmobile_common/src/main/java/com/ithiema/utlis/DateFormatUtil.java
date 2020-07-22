package com.ithiema.utlis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Nice
 * @Description:
 * @Date: Create in 10:36 2020/7/16
 */
public class DateFormatUtil {
    /**
     * 格式化日期
     * @param format
     * @param date
     * @return
     */
    public static String dateFormat(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
