package com.ef.service.impl;

import com.ef.model.IPAccesLogEntity;
import com.ef.model.IPAccessLogViolation;
import com.ef.repository.IPAccessLogRepository;
import com.ef.service.ActivityAuditorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityAuditorServiceImpl implements ActivityAuditorService {

    private static final String DAILY_VIOLATION = "daily";
    private static final String HOURLY_VIOLATION = "hourly";

    private IPAccessLogRepository ipAccessLogRepository;

    public ActivityAuditorServiceImpl(IPAccessLogRepository ipAccessLogRepository){
        this.ipAccessLogRepository = ipAccessLogRepository;
    }

    public  List<IPAccessLogViolation> findViolators(Date startDate, String duration, Long threshold) {

        if(DAILY_VIOLATION.equals(duration.toLowerCase())){
            return ipAccessLogRepository.getHourlyViolatorsBasedOnStartTimeAndThreshold(startDate, threshold);
        } else if(HOURLY_VIOLATION.equals(duration.toLowerCase())){
             return ipAccessLogRepository.getDailyViolatorsBasedOnStartTimeAndThreshold(startDate, threshold);
        }

        return new ArrayList<IPAccessLogViolation>();
    }
}
