package com.kvelinskyi.ua.statisticsAutomation.repository;

import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekATO;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public interface ReportingWeekATORepository extends CrudRepository<ReportingWeekATO, Long> {
    ReportingWeekATO findByDateStartAndAndDateEnd(Date dateStart, Date dateEnd);
    List<ReportingWeekATO> findByDateStartBefore(Date dateEnd);
    List<ReportingWeekATO> findByOrderByDateStartAsc();
}
