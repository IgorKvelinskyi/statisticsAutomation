package com.kvelinskyi.ua.statisticsAutomation.helper.creationTableVPO;

import com.kvelinskyi.ua.statisticsAutomation.entity.OwiVPO;
import lombok.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Data
public class FormOwiVPO {
    private List<OwiVPO> owiVPOList;

    public List<OwiVPO> listSort(List<OwiVPO> owiVPOList) {
        List<OwiVPO> list = owiVPOList;
        Collections.sort(list, new Comparator<OwiVPO>() {
            public int compare(OwiVPO o1, OwiVPO o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return list;
    }
}
