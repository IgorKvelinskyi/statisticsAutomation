package com.kvelinskyi.ua.statisticsAutomation.helper.userRolesEditing;

import com.kvelinskyi.ua.statisticsAutomation.entity.Role;
import com.kvelinskyi.ua.statisticsAutomation.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FormUserRoles {
    public static List<UserRole> setUserRolesForUser(User user) {
        List<UserRole> userRoles = Arrays.asList(
                new UserRole(Role.ROLE_ADMIN, false),
                new UserRole(Role.ROLE_USER, false),
                new UserRole(Role.ROLE_INCOGNITO, false)
        );
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            switch (role) {
                case ROLE_USER -> statusActivationForUserRole(userRoles, Role.ROLE_USER);
                case ROLE_ADMIN -> statusActivationForUserRole(userRoles, Role.ROLE_ADMIN);
                default -> statusActivationForUserRole(userRoles, Role.ROLE_INCOGNITO);
            }
        }
        return userRoles;
    }

    public static List<UserRole> statusActivationForUserRole(List<UserRole> userRoles, Enum nameRole) {
        for (UserRole role : userRoles) {
            if (nameRole.equals(role.getName())) {
                role.setStatus(true);
            }
        }
        return userRoles;
    }
}
