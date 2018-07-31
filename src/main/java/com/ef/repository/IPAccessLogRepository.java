package com.ef.repository;

import com.ef.model.IPAccesLogEntity;
import com.ef.model.IPAccessLogViolation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IPAccessLogRepository extends JpaRepository<IPAccesLogEntity, String> {

    @Query("select new com.ef.model.IPAccessLogViolation(ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date)) from IPAccesLogEntity ipAccessLog " +
            "where ipAccessLog.date > :date " +
            "group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date), ipAccessLog.ipAddress " +
            "having count(ipAccessLog.ipAddress) > :threshold " +
            "order by HOUR(ipAccessLog.date) asc")
    List<IPAccessLogViolation> getHourlyViolatorsBasedOnStartTimeAndThreshold(@Param("date") Date date, @Param("threshold") Long threshold);

    @Query("select new com.ef.model.IPAccessLogViolation(ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date)) from IPAccesLogEntity ipAccessLog " +
            "where ipAccessLog.date > :date " +
            "group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), ipAccessLog.ipAddress " +
            "having count(ipAccessLog.ipAddress) > :threshold " +
            "order by DAY(ipAccessLog.date)")
    List<IPAccessLogViolation> getDailyViolatorsBasedOnStartTimeAndThreshold(@Param("date") Date date, @Param("threshold") Long threshold);

}
