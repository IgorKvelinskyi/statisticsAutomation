package com.kvelinskyi.ua.statisticsAutomation.helper;

import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class FormatTheDate {
    public static String formDdMmYyyy(Date date){
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatEnd.format(date);
    }

    public static String timeIntervalConvert(ReportingWeekATO reportingWeekATO) {
        return "з " + formDdMmYyyy(reportingWeekATO.getDateStart()) +
                " по " + formDdMmYyyy(reportingWeekATO.getDateEnd());
    }
}
