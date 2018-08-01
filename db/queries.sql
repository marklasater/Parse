--Query to find daily violations - JPA format
select ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date)) from IPAccesLogEntity ipAccessLog
where ipAccessLog.date > (:date)
group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), ipAccessLog.ipAddress
having count(ipAccessLog.ipAddress) > (:threshold)


--Query to find daily violations - Native format
select ip_address, YEAR(date) as year,MONTH(date) as month, day(date) as day from parser.ip_access_log
where date > '2017-01-01'
group by year,month, day, ip_address
having count(ip_address) > 500;


--Query to find hourly violations - JPA format
select ipAccessLog.ipAddress as ipAddress, count(*) as count, YEAR(ipAccessLog.date), MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date) from IPAccesLogEntity ipAccessLog
where ipAccessLog.date > (:date)
group by YEAR(ipAccessLog.date),MONTH(ipAccessLog.date), DAY(ipAccessLog.date), HOUR(ipAccessLog.date), ipAccessLog.ipAddress
having count(ipAccessLog.ipAddress) > (:threshold)


--Query to find hourly violations - Native format
select ip_address, YEAR(date) as year,MONTH(date) as month, day(date) as day, hour(date) as hour  from parser.ip_access_log
where date > '2017-01-01'
group by year,month, day, hour, ip_address
having count(ip_address) > 500;