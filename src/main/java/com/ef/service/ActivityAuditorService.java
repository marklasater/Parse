package com.ef.service;

import com.ef.model.IPAccessLogViolation;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ActivityAuditorService {
    List<IPAccessLogViolation> findViolators(Date date, String duration, Long threshold);
}
