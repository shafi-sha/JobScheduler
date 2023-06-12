package com.christ.job.services.processors;

import com.christ.job.services.common.Utils;
import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

public class TaskOneProcessor implements ItemProcessor<List<ErpCampusDBO>, List<ErpCampusDBO>> {


    @Override
    public List<ErpCampusDBO> process(List<ErpCampusDBO> item) throws Exception {
        System.out.println("--------------------------");
        System.out.println("TaskOneProcessor start");
        List<ErpCampusDBO> list = new ArrayList<>();
        if(!Utils.isNullOrEmpty(item))
            list.addAll(item);
        System.out.println("TaskOneProcessor end");
        return list;
    }
}
