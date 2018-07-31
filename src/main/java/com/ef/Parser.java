package com.ef;

import com.ef.model.IPAccessLogViolation;
import com.ef.service.ActivityAuditorService;
import com.ef.service.ParserService;
import com.ef.common.CommandLineParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration
@EntityScan
@ComponentScan
@Slf4j
public class Parser {

    private static CommandLineParser commandLineParser;
    private static ParserService parserService;
    private static ActivityAuditorService activityAuditorService;

    private static String startDate;
    private static String accessLog;
    private static String threshold;
    private static String duration;
    private static String defaultAccessLogLocation;
    private static String commandLineDateFormat;

    public Parser(CommandLineParser commandLineParser,
                  ParserService parserService,
                  ActivityAuditorService activityAuditorService,
                  @Value("${command.line.required.arguments.startDate}") String startDate,
                  @Value("${command.line.arguments.accesslog}") String accessLog,
                  @Value("${command.line.required.arguments.threshold}") String threshold,
                  @Value("${command.line.required.arguments.duration}") String duration,
                  @Value("${command.line.default.accesslog.location}") String defaultAccessLogLocation,
                  @Value("${command.line.date.format}")  String commandLineDateFormat) {
        this.commandLineParser = commandLineParser;
        this.parserService = parserService;
        this.activityAuditorService = activityAuditorService;
        this.startDate = startDate;
        this.accessLog = accessLog;
        this.threshold = threshold;
        this.duration = duration;
        this.defaultAccessLogLocation = defaultAccessLogLocation;
        this.commandLineDateFormat =  commandLineDateFormat;
    }

    public static void main(String[] args) {
        SpringApplication.run(Parser.class, args);

        Map<String, String> commandLineArgumentMap = getCommandLineArguments(args);

        parserService.parseLogFileIntoDatabase(commandLineArgumentMap.get(accessLog));

        findAndOutputLogViolators(commandLineArgumentMap);
    }

    /**
     * This method will get all the violators and output them to the log.
     *
     * @param commandLineArgumentMap
     */
    private static void findAndOutputLogViolators(Map<String, String> commandLineArgumentMap){
        try {
            DateFormat dateFormat = new SimpleDateFormat(commandLineDateFormat);

            Date theStartDate = dateFormat.parse(commandLineArgumentMap.get(startDate));
            Long theThreshold = Long.parseLong(commandLineArgumentMap.get(threshold));
            String theDuration = commandLineArgumentMap.get(duration);

            List<IPAccessLogViolation> violators = activityAuditorService.findViolators(theStartDate, theDuration, theThreshold);

            for (IPAccessLogViolation violation:violators) {
                log.info(violation.toString());
            }

        } catch (Exception exception){
            log.error(exception.getMessage());
        }
    }


    /**
     * This method will parse the command line args into a map
     *
     * @param args
     * @return
     */
    private static Map<String, String> getCommandLineArguments(String[] args){
        Map<String, String> commandLineArgumentMap = commandLineParser.parseIntoMap(args);

        commandLineParser.validateInput(commandLineArgumentMap);

        setAccessLogLocation(commandLineArgumentMap);

        return commandLineArgumentMap;
    }

    /**
     * This method will check if the access log location was included in the command line parameters.
     * If not, it will add the default access log location to {@param commandLineArgumentMap}
     *
     * @param commandLineArgumentMap
     */
    private static void setAccessLogLocation(Map<String, String> commandLineArgumentMap){
        ClassPathResource classPathResource = new ClassPathResource(defaultAccessLogLocation);
        try{
            if(!commandLineArgumentMap.containsKey(accessLog)){
                commandLineArgumentMap.put(accessLog, classPathResource.getFile().getAbsolutePath());
            }
        } catch (IOException exception){
            log.error("File Not Found", exception);
        }

    }

}
