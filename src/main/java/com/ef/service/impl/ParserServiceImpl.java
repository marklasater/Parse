package com.ef.service.impl;

import com.ef.common.LogException;
import com.ef.model.IPAccesLogEntity;
import com.ef.repository.IPAccessLogRepository;
import com.ef.service.ParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ParserServiceImpl implements ParserService {

    private IPAccessLogRepository ipAccessLogRepository;

    @Value("${log.file.delimiter}")
    private  String DEFAULT_LOG_DELIMITER;

    @Value("${log.file.date.location}")
    private int LOG_FILE_DATE_LOCATION;

    @Value("${log.file.ip.address.location}")
    private int LOG_FILE_IP_ADDRESS_LOCATION;

    @Value("${log.file.date.format}")
    private String LOG_FILE_DATE_FORMAT;

    private static final Pattern IPV4_PATTERN = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    public ParserServiceImpl(IPAccessLogRepository ipAccessLogRepository){
        this.ipAccessLogRepository = ipAccessLogRepository;
    }


    /**
     * This method will parse a log file at {@param logFileLocation} and persist it into the database
     *
     * @param logFileLocation
     */
    @Transactional
    public void parseLogFileIntoDatabase(String logFileLocation){

        List<IPAccesLogEntity> ipAccesLogEntities = parseLogFile(logFileLocation);
        ipAccessLogRepository.saveAll(ipAccesLogEntities);
    }

    /**
     * This method will parse a log file at {@param logFileLocation} into a {@link List<IPAccesLogEntity>}
     *
     * @param logFileLocation
     * @return
     */
    private List<IPAccesLogEntity> parseLogFile(String logFileLocation){
        try{
            BufferedReader bufferedReader = new BufferedReader(getStream(logFileLocation));

            String currentLine = bufferedReader.readLine();
            List<IPAccesLogEntity> ipAccesLogEntities = new ArrayList<>();
            while(currentLine != null){
                Optional<IPAccesLogEntity> ipAccesLogEntity = parseLogLine(currentLine);
                ipAccesLogEntity.ifPresent(ipAccesLogEntities::add);
                currentLine = bufferedReader.readLine();
            }

            return ipAccesLogEntities;

        } catch (FileNotFoundException exception){
            log.error("Log File not found: " + logFileLocation);
            log.error(exception.getMessage(), exception);
        } catch (IOException exception){
            log.error("Log file not saved to database " + logFileLocation);
            log.error(exception.getMessage(), exception);

        }

        return new ArrayList<>();
    }

    /**
     * This method will return an {@link InputStreamReader} representing the file.
     * The file can either be on the classpath or on the file system.
     *
     * @param logFileLocation
     * @return
     */
    private InputStreamReader getStream(String logFileLocation){
        try{
            return new InputStreamReader(new FileInputStream(URLDecoder.decode(logFileLocation, "UTF-8")));
        } catch (Exception e){
           log.info("The file is not on the file system.  Will check the classpath: " + logFileLocation);
        }

        try{
            return new InputStreamReader(new ClassPathResource(logFileLocation).getInputStream());
        } catch (Exception e){
            log.info("The not on the classpath and not on the file system: " + logFileLocation);

        }

        throw new LogException("Log file not found " + logFileLocation);
    }

    /**
     * This method will parse a {@param logLine} into a {@link IPAccesLogEntity}
     *
     * @param logLine
     * @return
     */
    private Optional<IPAccesLogEntity> parseLogLine(String logLine){
        try{
            String[] logArray = logLine.split(DEFAULT_LOG_DELIMITER);
            IPAccesLogEntity ipAccesLogEntity = new IPAccesLogEntity();

            DateFormat dateFormatter = new SimpleDateFormat(LOG_FILE_DATE_FORMAT);
            Date date = dateFormatter.parse(logArray[LOG_FILE_DATE_LOCATION]);

            String ipAddress= logArray[LOG_FILE_IP_ADDRESS_LOCATION];

            validateIpAddress(ipAddress);
            ipAccesLogEntity.setIpAddress(ipAddress);
            ipAccesLogEntity.setDate(date);

            return Optional.of(ipAccesLogEntity);

        } catch (ParseException exception){
            log.error("Log Line not saved to database - Invalid Date " + logLine);
            log.error(exception.getMessage(), exception);
        } catch (LogException exception){
            log.error("Log Line not saved to database - Invalid IP Address " + logLine);
            log.error(exception.getMessage(), exception);
        }

        return Optional.empty();
    }


    private void validateIpAddress(String ipAddress) throws LogException {
        if(!isIPv4Address(ipAddress) && !isIPv6Address(ipAddress)){
            throw new LogException("Invalid IP Address");
        }
    }

    private boolean isIPv4Address(String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    private boolean isIPv6StandardAddress(String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }

    private boolean isIPv6HexCompressedAddress(String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }

    private boolean isIPv6Address(String input) {
        return isIPv6StandardAddress(input) || isIPv6HexCompressedAddress(input);
    }
}
