--Query to find daily violations
select ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date)) from IPAccesLogEntity ipAccessLog
where ipAccessLog.date > (:date)
group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), ipAccessLog.ipAddress
having count(ipAccessLog.ipAddress) > (:threshold)


--Query to find hourly violations
select ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date) from IPAccesLogEntity ipAccessLog
where ipAccessLog.date > (:date)
group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date), ipAccessLog.ipAddress
having count(ipAccessLog.ipAddress) > (:threshold)