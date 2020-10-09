package com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiVPO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekVPO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Data
public class ListOwiVPO {
    public List<OwiVPO> outEmptyTableOwiVPO(ReportingWeekVPO reportingWeekVPO) {
        List<String> stringsName = Arrays.asList(
                "КНП «Клінічна лікарня «ПСИХІАТРІЯ»"
        );
        List<String> stringsVendorCode = Arrays.asList(
                "1."
        );
        List<OwiVPO> owiVPOList = new ArrayList<>();
        int count = 0;
        for (String value : stringsName) {
            OwiVPO owiVPO = new OwiVPO();
            owiVPO.setNameOfRegions(value);
            owiVPO.setVendorCode(stringsVendorCode.get(count));
            count++;
            allIntByZero(owiVPO);
            owiVPO.setReportingWeekVpoId(reportingWeekVPO);
            owiVPOList.add(owiVPO);
        }
        return owiVPOList;
    }

    private void allIntByZero(OwiVPO owiVPO) {
        owiVPO.setTotalDisplacedPersons(0);
        owiVPO.setIncludingChildren(0);
        owiVPO.setNumberOfPeopleTurnedAdults(0);
        owiVPO.setNumberOfPeopleTurnedChildren(0);
        owiVPO.setNumberOfPeopleHospitalizedAdults(0);
        owiVPO.setNumberOfPeopleHospitalizedChildren(0);
        owiVPO.setProblematicIssuesTakenAccountAdults(0);
        owiVPO.setProblematicIssuesTakenAccountChildren(0);
        owiVPO.setProblematicIssuesChildrenBorn(0);
        owiVPO.setProblematicIssuesCarryingMedicalExam(0);
    }
}
