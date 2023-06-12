package com.christ.job.services.common;

import com.christ.job.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.caching.CacheUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisSysPropertiesData {

    @Autowired
    CommonApiTransaction commonApiTransaction;

    @PostConstruct
    public void setERPPropertiesDataToRedis() {
        CacheUtils.instance.clearMap("__PROPERTY_MAP_");
        List<Tuple> erpProperties = commonApiTransaction.getERPProperties();
        if(!Utils.isNullOrEmpty(erpProperties)){
            System.out.println("data for redis");
            for(Tuple tuple : erpProperties){
                if(!Utils.isNullOrEmpty(tuple.get("is_common_property")) && tuple.get("is_common_property").toString().equals("1")) {
                    CacheUtils.instance.set("__PROPERTY_MAP_", "_G_"+ tuple.get("property_name").toString(), tuple.get("property_value").toString());
                } else {
                    if(!Utils.isNullOrEmpty(tuple.get("erp_location_id"))) {
                        CacheUtils.instance.set("__PROPERTY_MAP_",tuple.get("erp_location_id").toString()+"_L_"+ tuple.get("property_name").toString(),tuple.get("property_detail_value").toString());
                    }
                    else if(!Utils.isNullOrEmpty(tuple.get("erp_campus_id"))) {
                        CacheUtils.instance.set("__PROPERTY_MAP_", tuple.get("erp_campus_id").toString()+"_C_"+tuple.get("property_name").toString(),tuple.get("property_detail_value").toString());
                    }
                }
            }
        }
    }

    public String getSysProperties(String propertyName, String lc, Integer lcID) {
        String val= "";
        if(!Utils.isNullOrEmpty(propertyName)) {
            if(Utils.isNullOrEmpty(lc)) {
                   val =  CacheUtils.instance.get("__PROPERTY_MAP_","_G_"+ propertyName);
            }
            else if(lc.equalsIgnoreCase("L")) {
                val =  CacheUtils.instance.get("__PROPERTY_MAP_", lcID+"_L_"+propertyName);
            }
            else if(lc.equalsIgnoreCase("C")) {
                val =  CacheUtils.instance.get("__PROPERTY_MAP_",lcID+"_C_"+ propertyName);
            }
        }
       return val;
    }
}


