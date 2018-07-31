package com.ef.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="ip_access_log")
public class IPAccesLogEntity extends BaseEntity {

    private Date date;
    private String ipAddress;
}
