package com.kvelinskiy.ua.statisticsAutomation.repository;

import com.kvelinskiy.ua.statisticsAutomation.entity.Message;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public interface MessageRepository extends CrudRepository<Message, Long> {
}
