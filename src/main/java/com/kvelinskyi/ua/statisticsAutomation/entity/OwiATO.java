package com.kvelinskyi.ua.statisticsAutomation.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Entity
@Data
@Table(name = "owi_ato")
public class OwiATO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long owi_ato_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rw_ato_id")
    private ReportingWeekATO reportingWeekId;
    private String vendorCode;
    private String name;
    private int wholePeriodTotal;
    private int wholePeriodCivilian;
    private int wholePeriodSoldiers;
    private int wholePeriodDemobilized;
    private int wholePeriodWomen;
    private int wholePeriodChildren;
    private int reportingWeekTotal;
    private int reportingWeekCivilian;
    private int reportingWeekSoldiers;
    private int reportingWeekDemobilized;
    private int reportingWeekWomen;
    private int reportingWeekChildren;
}
