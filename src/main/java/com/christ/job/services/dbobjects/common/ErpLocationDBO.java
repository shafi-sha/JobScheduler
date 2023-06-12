package com.christ.job.services.dbobjects.common;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="erp_location")
@Getter
@Setter
public class ErpLocationDBO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="erp_location_id")
    private Integer id;

    @Column(name="location_name")
    private String locationName;

    @Column(name="created_users_id",updatable=false)
    private Integer createdUsersId;

    @Column(name="modified_users_id")
    private Integer modifiedUsersId;

    @Column(name="record_status")
    private Character recordStatus;
}