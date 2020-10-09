package com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO;

import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public class HeaderTableVPO {
    public static List<String> createTableMainHeaderVPO() {
        List<String> strings = Arrays.asList(
                "№ п/п",
                "Назва регіонів",
                "Всього переміщених осіб",
                "У тому числі дітей",
                "Кількість осіб",
                "Проблемні питання що виникають"
        );
        return strings;
    }

    public static List<String> createTableSecondHeaderVPO() {
        List<String> strings = Arrays.asList(
                "звернулось",
                "госпіталізовано",
                "Взято на облік",
                "Діти,  що народилися",
                "Проведення медичних оглядів, флюорографії то що"
        );
        return strings;
    }

    public static List<String> createTableThirdHeaderVPO() {
        List<String> strings = Arrays.asList(
                "дорослих",
                "дітей",
                "дорослих",
                "дітей",
                "дорослих",
                "дітей"
        );
        return strings;
    }
}
