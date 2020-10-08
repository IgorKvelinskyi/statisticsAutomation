package com.kvelinskyi.ua.statisticsAutomation.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Entity
@Data
@Table(name = "owi_vpo")
public class OwiVPO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long owi_vpo_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rw_vpo_id")
    private ReportingWeekVPO reportingWeekVpoId;
    private String vendorCode;
    private String nameOfRegions;
    private String totalDisplacedPersons;
    private String includingChildren;
    private String numberOfPeopleTurnedAdults;
    private String numberOfPeopleTurnedChildren;
    private String numberOfPeopleHospitalizedAdults;
    private String numberOfPeopleHospitalizedChildren;
    private String problematicIssuesTakenAccountAdults;
    private String problematicIssuesTakenAccountChildren;
    private String problematicIssuesChildrenBorn;
    private String problematicIssuesCarryingMedicalExam;
}
