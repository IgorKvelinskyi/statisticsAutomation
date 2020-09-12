package com.kvelinskiy.ua.statisticsAutomation.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN, USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
