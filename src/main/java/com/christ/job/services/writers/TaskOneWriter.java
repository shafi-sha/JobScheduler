package com.christ.job.services.writers;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class TaskOneWriter implements ItemWriter<List<ErpCampusDBO>> {

    @Override
    public void write(Chunk<? extends List<ErpCampusDBO>> chunk) throws Exception {
        System.out.println("--------------------------");
        System.out.println("TaskOneWriter start");
        System.out.println("size : " + chunk.size());
        System.out.println("TaskOneWriter end");
        System.out.println("--------------------------");
    }
}
