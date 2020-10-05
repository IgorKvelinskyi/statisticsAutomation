package com.kvelinskyi.ua.statisticsAutomation.helper;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class FormatTheDate {
    public static String formDdMmYyyy(Date date){
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatEnd.format(date);
    }
}
