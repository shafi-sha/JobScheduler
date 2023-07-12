package com.christ.job.services.common;

import com.christ.job.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.caching.CacheUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisSysPropertiesData {

    @Autowired
    CommonApiTransaction commonApiTransaction;

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

    public void setSysProperties(String propertyName, String propertyValue, String lc, Integer lcID) {
        String val= "";
        if(!Utils.isNullOrEmpty(propertyName)) {
            if(Utils.isNullOrEmpty(lc)) {
                CacheUtils.instance.set("__PROPERTY_MAP_","_G_"+ propertyName, propertyValue);
            }
            else if(lc.equalsIgnoreCase("L")) {
                CacheUtils.instance.set("__PROPERTY_MAP_", lcID+"_L_"+propertyName, propertyValue);
            }
            else if(lc.equalsIgnoreCase("C")) {
                CacheUtils.instance.set("__PROPERTY_MAP_",lcID+"_C_"+ propertyName, propertyValue);
            }
        }
    }
}


