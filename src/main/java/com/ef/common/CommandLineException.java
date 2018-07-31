package com.ef.common;

/**
 * {@code CommandLineException} is the class of those
 * exceptions that can be thrown during the command line parsing
 *
 *
 * @author  Mark Lasater
 */
public class CommandLineException extends RuntimeException {


    public CommandLineException(String message) {
        super(message);
    }

}
