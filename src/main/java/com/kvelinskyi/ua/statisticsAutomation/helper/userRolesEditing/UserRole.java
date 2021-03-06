package com.kvelinskyi.ua.statisticsAutomation.helper.userRolesEditing;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Data
@AllArgsConstructor
public class UserRole {
    private Enum name;
    private boolean status;
}
