package com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import lombok.Data;

import java.util.*;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Data
public class ListOwiATO {
    public List<OwiATO> outEmptyTableOwiATO(ReportingWeekATO reportingWeekATO){
        List<String> stringsName= Arrays.asList(
                "Звернулось за медичною допомогою, з них:",
                "поранених в зоні АТО",
                "з захворюваннями, що виникли в зоні АТО",
                "Госпіталізовано з числа осіб, які звернулися за медичною допомогою, зних:",
                "прооперовано з числа госпіталізованих",
                "померло з числа госпіталізованих"
        );
        List<String> stringsVendorCode= Arrays.asList(
                "1",
                "-",
                "-",
                "2",
                "-",
                "-"
        );
        List<OwiATO> owiATOSet = new ArrayList<>();
        int count = 0;
        for (String value:stringsName) {
            OwiATO owiATO = new OwiATO();
            owiATO.setName(value);
            owiATO.setVendorCode(stringsVendorCode.get(count));
            count++;
            allIntByZero(owiATO);
            owiATO.setReportingWeekId(reportingWeekATO);
            owiATOSet.add(owiATO);
        }
        return owiATOSet;
    }

    private void allIntByZero(OwiATO owiATO) {
        owiATO.setWholePeriodTotal(0);
        owiATO.setWholePeriodCivilian(0);
        owiATO.setWholePeriodSoldiers(0);
        owiATO.setWholePeriodDemobilized(0);
        owiATO.setWholePeriodWomen(0);
        owiATO.setWholePeriodChildren(0);
        owiATO.setReportingWeekTotal(0);
        owiATO.setReportingWeekCivilian(0);
        owiATO.setReportingWeekSoldiers(0);
        owiATO.setReportingWeekDemobilized(0);
        owiATO.setReportingWeekWomen(0);
        owiATO.setReportingWeekChildren(0);

    }
}
