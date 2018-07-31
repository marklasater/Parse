package com.ef.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IPAccessLogViolation {
    private String ipAddress;
    private Long count;
    private int year;
    private int month;
    private int day;
    private int hour;

    public IPAccessLogViolation(String ipAddress, Long count, int year, int month, int day){
        this.ipAddress = ipAddress;
        this.count = count;
        this.year = year;
        this.month =month;
        this.day = day;
        this.hour = -1;
    }

    @Override
    public String toString() {
        String result = ipAddress + " was logged " + count + " time(s) on " + month + "/"+ day +"/" + year;
        if(hour >= 0){
            result = result + " during " + hour + ":00";
        }
        return result;
    }
}
