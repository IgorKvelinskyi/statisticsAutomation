package com.kvelinskyi.ua.statisticsAutomation.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String text;
}
