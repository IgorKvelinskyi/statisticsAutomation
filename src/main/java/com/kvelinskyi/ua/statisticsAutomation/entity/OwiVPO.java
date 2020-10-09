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
    private int totalDisplacedPersons;
    private int includingChildren;
    private int numberOfPeopleTurnedAdults;
    private int numberOfPeopleTurnedChildren;
    private int numberOfPeopleHospitalizedAdults;
    private int numberOfPeopleHospitalizedChildren;
    private int problematicIssuesTakenAccountAdults;
    private int problematicIssuesTakenAccountChildren;
    private int problematicIssuesChildrenBorn;
    private int problematicIssuesCarryingMedicalExam;
}
