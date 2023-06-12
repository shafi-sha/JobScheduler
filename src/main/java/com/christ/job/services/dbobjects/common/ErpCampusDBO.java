package com.christ.job.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


@Entity
@Table(name="erp_campus")
@Getter
@Setter
public class ErpCampusDBO {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_campus_id")
    private Integer id;

    @Column(name="campus_name")
    private String campusName;

    @Column(name="short_name")
    private String shortName;

    @Column(name="campus_color_code")
    private String campusColorCode;

//    @ManyToOne
//    @JoinColumn(name="erp_location_id")
    @Column(name="erp_location_id")
    private Integer erpLocationDBO;

    @Column(name = "created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name = "modified_users_id")
    private Integer modifiedUsersId;

    @Column(name = "record_status")
    private Character recordStatus;

}