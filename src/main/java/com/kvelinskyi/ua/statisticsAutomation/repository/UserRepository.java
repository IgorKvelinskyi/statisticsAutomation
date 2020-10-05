package com.kvelinskyi.ua.statisticsAutomation.repository;

import com.kvelinskyi.ua.statisticsAutomation.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

    Iterable<User> findAll(Sort id);
}
