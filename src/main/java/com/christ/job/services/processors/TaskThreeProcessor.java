package com.christ.job.services.processors;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
public class TaskThreeProcessor implements ItemProcessor<ErpCampusDBO, ErpCampusDBO> {

    @Override
    public ErpCampusDBO process(ErpCampusDBO erpCampusDBO) throws Exception {
        System.out.println("TaskFourProcessor  " + erpCampusDBO.getCampusName());
        erpCampusDBO.setCampusName("a");
        return erpCampusDBO;
    }
}
