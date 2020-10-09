package com.kvelinskyi.ua.statisticsAutomation.repository;

import com.kvelinskyi.ua.statisticsAutomation.entity.ReportingWeekVPO;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public interface ReportingWeekVPORepository extends CrudRepository<ReportingWeekVPO, Long> {
    ReportingWeekVPO findByDateStartAndAndDateEnd(Date dateStart, Date dateEnd);
    List<ReportingWeekVPO> findByDateStartBefore(Date dateEnd);
    List<ReportingWeekVPO> findByOrderByDateStartAsc();
}
