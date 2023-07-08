package com.christ.job.services.handler;

import com.christ.job.services.dbobjects.common.ErpEmailsDBO;
import com.christ.job.services.transactions.common.CommonApiTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonApiHandler {

    private static volatile CommonApiHandler commonApiHandler = null;

    public static CommonApiHandler getInstance() {
        if(commonApiHandler==null) {
            commonApiHandler = new CommonApiHandler();
        }
        return commonApiHandler;
    }

    @Autowired
    CommonApiTransaction commonApiTransaction;

    public void updateErpEmailsDBO(ErpEmailsDBO erpEmailsDBO){
        CommonApiTransaction.getInstance().updateErpEmailsDBO(erpEmailsDBO);
    }

}
