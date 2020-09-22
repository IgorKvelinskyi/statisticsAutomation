package com.kvelinskiy.ua.statisticsAutomation.helper;

import java.util.Arrays;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
public class HeaderTableATO {
    public static List<String> createTableHeaderATO() {
        List<String> strings = Arrays.asList(
                "Всього",
                "Цивільних",
                "Військовослужбовців",
                "Демобілізовано",
                "Жінок",
                "Дітей",
                "Всього",
                "Цивільних",
                "Військовослужбовців",
                "Демобілізовано",
                "Жінок",
                "Дітей"
        );
        return strings;
    }
}
