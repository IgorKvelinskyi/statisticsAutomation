package com.kvelinskiy.ua.statisticsAutomation.helper.creationTableATO;

import com.kvelinskiy.ua.statisticsAutomation.entity.OwiATO;
import lombok.Data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Data
public class OwiATOCreationForm {
    private List<OwiATO> owiATOList;

    public List<OwiATO> listSort(List<OwiATO> owiATOList) {
        List<OwiATO> list = owiATOList;
        Collections.sort(list, new Comparator<OwiATO>() {
            public int compare(OwiATO o1, OwiATO o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return list;
    }

}
