package com.kvelinskyi.ua.statisticsAutomation.helper.creationWordDocxATO;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiATO;
import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import com.kvelinskyi.ua.statisticsAutomation.repository.ReportingWeekATORepository;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Component
public class FormGeneratingTableData {
    public List<OwiATO> generatingTableData(List<OwiATO> owiATOSet, Date dateEnd, ReportingWeekATORepository reportingWeekATORepository) {
        List<ReportingWeekATO> owiATOList = reportingWeekATORepository.findByDateStartBefore(dateEnd);
        if (owiATOList == null || owiATOList.size() == 0) {
            return generatingTableDataTotal(owiATOSet);
        }
        //OwiATO owiATO = new OwiATO();
        owiATOSet = resetTotalOwiATO(owiATOSet);
        for (OwiATO owiATO : owiATOSet
        ) {
            for (ReportingWeekATO reportingWeekATO : owiATOList
            ) {
                List<OwiATO> currentOwiATOSet = reportingWeekATO.getOwiATOSet();
                for (OwiATO owiATO1 : currentOwiATOSet
                ) {
                    if (owiATO.getName().equals(owiATO1.getName())) {
                        owiATO.setWholePeriodCivilian(owiATO.getWholePeriodCivilian() + owiATO1.getReportingWeekCivilian());
                        owiATO.setWholePeriodSoldiers(owiATO.getWholePeriodSoldiers() + owiATO1.getReportingWeekSoldiers());
                        owiATO.setWholePeriodDemobilized(owiATO.getWholePeriodDemobilized() + owiATO1.getReportingWeekDemobilized());
                        owiATO.setWholePeriodWomen(owiATO.getWholePeriodWomen() + owiATO1.getReportingWeekWomen());
                        owiATO.setWholePeriodChildren(owiATO.getWholePeriodChildren() + owiATO1.getReportingWeekChildren());
                    }
                }
            }
        }
        return generatingTableDataTotal(owiATOSet);
    }

    private List<OwiATO> resetTotalOwiATO(List<OwiATO> owiATOSet) {
        for (OwiATO owiATO : owiATOSet
        ) {
            owiATO.setWholePeriodCivilian(0);
            owiATO.setWholePeriodSoldiers(0);
            owiATO.setWholePeriodDemobilized(0);
            owiATO.setWholePeriodWomen(0);
            owiATO.setWholePeriodChildren(0);
        }
        return owiATOSet;
    }

    private List<OwiATO> generatingTableDataTotal(List<OwiATO> owiATOSet) {
        for (OwiATO owiATO : owiATOSet
        ) {
            owiATO.setWholePeriodTotal(owiATO.getWholePeriodCivilian() + owiATO.getWholePeriodSoldiers()
                    + owiATO.getWholePeriodDemobilized() + owiATO.getWholePeriodWomen() + owiATO.getWholePeriodChildren());
            owiATO.setReportingWeekTotal(owiATO.getReportingWeekCivilian() + owiATO.getReportingWeekSoldiers()
                    + owiATO.getReportingWeekDemobilized() + owiATO.getReportingWeekWomen() + owiATO.getReportingWeekChildren());
        }
        return owiATOSet;
    }


}
