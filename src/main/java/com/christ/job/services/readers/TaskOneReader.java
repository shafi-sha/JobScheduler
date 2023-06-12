package com.christ.job.services.readers;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import jakarta.persistence.Tuple;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskOneReader implements ItemReader<List<ErpCampusDBO>> {

    @Autowired
    CommonApiTransaction transaction;

    @Override
    public List<ErpCampusDBO> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("TaskOneReader start");
        //Tuple tuple = transaction.getCampus();
        //List<ErpCampusDBO> campusDBOList = transaction.getCampuses();
        System.out.println("TaskOneReader end");
        return transaction.getCampuses();
    }
}
