package com.ef.service;

import org.springframework.stereotype.Service;

@Service
public interface ParserService {

    void parseLogFileIntoDatabase(String logFileLocation);

}
