package com.kvelinskyi.ua.statisticsAutomation.helper.creationTableATO;

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

    public static List<String> createTableHeaderATONumbering() {
        List<String> strings = Arrays.asList(
                "А",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13"
        );
        return strings;
    }
}
