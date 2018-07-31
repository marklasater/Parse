package com.ef.common;

/**
 * {@code LogException} is the class of those
 * exceptions that can be thrown during log parsing
 *
 *
 * @author  Mark Lasater
 */
public class LogException extends RuntimeException  {
    public LogException(String message) {
        super(message);
    }

}
