package com.kvelinskyi.ua.statisticsAutomation.helper;

import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekVPO;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class FormatTheDate {
    public static String formDdMmYyyy(Date date){
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatEnd.format(date);
    }

    public static String timeIntervalATOConvert(ReportingWeekATO reportingWeekATO) {
        return "з " + formDdMmYyyy(reportingWeekATO.getDateStart()) +
                " по " + formDdMmYyyy(reportingWeekATO.getDateEnd());
    }
    public static String timeIntervalVPOConvert(ReportingWeekVPO reportingWeekVPO) {
        return "з " + formDdMmYyyy(reportingWeekVPO.getDateStart()) +
                " по " + formDdMmYyyy(reportingWeekVPO.getDateEnd());
    }
}
