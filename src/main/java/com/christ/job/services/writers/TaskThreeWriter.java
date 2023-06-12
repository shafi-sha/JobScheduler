package com.christ.job.services.writers;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

@Service
public class TaskThreeWriter implements ItemWriter<ErpCampusDBO> {

    @Override
    public void write(Chunk<? extends ErpCampusDBO> chunk) throws Exception {
        System.out.println("TaskFourWriter size" + chunk.size());
    }
}
