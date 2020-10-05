package com.kvelinskyi.ua.statisticsAutomation.repository;

import com.kvelinskyi.ua.statisticsAutomation.entity.Message;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public interface MessageRepository extends CrudRepository<Message, Long> {
}
