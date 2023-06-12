package com.christ.job.services.transactions.common;

import com.christ.job.services.dbobjects.common.ErpCampusDBO;
import jakarta.persistence.Tuple;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommonApiTransaction {

    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    public List<Tuple> getERPProperties() {
        String str = "select sys_properties.property_name, sys_properties.property_value, sys_properties.is_common_property, " +
                " sys_properties_details.erp_campus_id,sys_properties_details.erp_location_id,sys_properties_details.property_detail_value from sys_properties " +
                " left join sys_properties_details on sys_properties.sys_properties_id = sys_properties_details.sys_properties_id and sys_properties_details.record_status = 'A' " +
                "  where sys_properties.record_status = 'A'";
        return sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class).getResultList()).await().indefinitely();
    }

//    public ErpCampusDBO getCampus1() {
//        return sessionFactory.getCurrentSession().createQuery("from ErpCampusDBO where recordStatus='A' and id=:id", ErpCampusDBO.class)
//                .setParameter("id", 1)
//                .getSingleResult();
//    }

//    public List<ErpCampusDBO> getCampuses() {
//        return sessionFactory.getCurrentSession().createQuery("from ErpCampusDBO where recordStatus='A'", ErpCampusDBO.class).getResultList();
//    }

//    public Tuple getCampus() {
//        return sessionFactory.getCurrentSession().createNativeQuery("select bo.campus_name from erp_campus bo where bo.record_status='A' and bo.erp_campus_id=1", Tuple.class).getSingleResult();
//    }

    public ErpCampusDBO getCampus1() {
        return sessionFactory.withSession(s->s.createQuery("from ErpCampusDBO where recordStatus='A' and id=1", ErpCampusDBO.class)
                .getSingleResult()).await().indefinitely();
    }

    public List<ErpCampusDBO> getCampuses() {
        return sessionFactory.withSession(s->s.createQuery("from ErpCampusDBO where recordStatus='A'", ErpCampusDBO.class).getResultList()).await().indefinitely();
    }

    public Tuple getCampus() {
        return sessionFactory.withSession(s->s.createNativeQuery("select bo.campus_name from erp_campus bo where bo.record_status='A' and bo.erp_campus_id=1", Tuple.class)
                .getSingleResult()).await().indefinitely();
    }
}