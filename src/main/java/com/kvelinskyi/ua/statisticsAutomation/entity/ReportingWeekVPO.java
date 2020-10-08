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
@Table(name = "rw_vpo")
public class ReportingWeekVPO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="rw_vpo_id")
    private long id;
    @OneToMany(mappedBy = "reportingWeekVpoId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OwiVPO> owiVPOList;
    private Date dateStart;
    private Date dateEnd;

    @Override
    public String toString() {
        return "ReportingWeekVPO{" +
                "id=" + id +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                '}';
    }
}
