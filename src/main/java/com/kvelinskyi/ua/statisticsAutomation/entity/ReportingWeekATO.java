package com.kvelinskyi.ua.statisticsAutomation.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Entity
@Data
@Table(name = "rw_ato")
public class ReportingWeekATO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="rw_ato_id")
    private long id;
    @OneToMany(mappedBy = "reportingWeekId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OwiATO> owiATOList;
    private Date dateStart;
    private Date dateEnd;

    @Override
    public String toString() {
        return "ReportingWeekATO{" +
                "id=" + id +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                '}';
    }
}
