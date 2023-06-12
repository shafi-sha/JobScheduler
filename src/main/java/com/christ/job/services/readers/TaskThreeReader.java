package com.christ.job.services.readers;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@StepScope
public class TaskThreeReader implements ItemReader<ErpCampusDBO> {

    @Autowired
    CommonApiTransaction transaction;

    @Value("#{jobParameters['parameter1']}")
    private String parameter1;

    @Value("#{jobParameters['parameter2']}")
    private Double parameter2;


    @Override
    public ErpCampusDBO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("TaskFourReader start");

        System.out.println("TaskFourReader parameter1 : " + parameter1);
        System.out.println("TaskFourReader parameter2 : " + parameter2);
        //Tuple tuple = transaction.getCampus();
        //List<ErpCampusDBO> campusDBOList = transaction.getCampuses();
        ErpCampusDBO erpCampusDBO = transaction.getCampus1();
        System.out.println("campusName: " + erpCampusDBO.getCampusName());
        System.out.println("TaskFourReader end");
        return erpCampusDBO;
    }

}
