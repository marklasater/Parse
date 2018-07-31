package com.ef.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommandLineParser {

    @Value("${command.line.default.argument.regex}")
    private String DEFAULT_PATTERN;

    @Value("${command.line.default.argument.flag}")
    private String DEFAULT_ARGUMENT_FLAG;

    @Value("${command.line.default.argument.assignment}")
    private String DEFAULT_ARGUMENT_ASSIGNMENT;

    @Value("${command.line.required.arguments}")
    private String DEFAULT_REQUIRED_COMMAND_LINE_ARGUMENTS;

    private String removeFlag(String arg, String argumentFlag){
        return arg.replace(argumentFlag,"");
    }

    private String[] splitArgument(String arg, String argumentAssignment){
        return arg.split(argumentAssignment);
    }

    private boolean commandLineArgument(String arg, Pattern commandLinePattern){
        return commandLinePattern.matcher(arg).matches();
    }

    public Map<String, String> parseIntoMap(String[] args, Pattern commandLinePattern, String argumentFlag, String argumentAssignment){

        return Arrays.asList(args).stream()
                .filter(arg -> commandLineArgument(arg, commandLinePattern))
                .map(arg -> removeFlag(arg, argumentFlag))
                .map(arg -> splitArgument(arg, argumentAssignment))
                .collect(Collectors.toMap(argArray -> argArray[0], argArray -> argArray[1]));
    }

    public Map<String, String> parseIntoMap(String[] args){
         Pattern pattern = Pattern.compile(DEFAULT_PATTERN);
        return parseIntoMap(args, pattern, DEFAULT_ARGUMENT_FLAG, DEFAULT_ARGUMENT_ASSIGNMENT);
    }

    private boolean isValid(Map<String, String> commandLineArguments, List<String> requiredParameters){
        return commandLineArguments.keySet().containsAll(requiredParameters);
    }

    public void validateInput(Map<String, String> commandLineArguments, List<String> requiredParameters){
        if(!isValid(commandLineArguments, requiredParameters)){
            List<String> theList = requiredParameters.stream()
                    .filter(parameter -> !commandLineArguments.keySet().contains(parameter))
                    .collect(Collectors.toList());
            throw new CommandLineException("Invalid command line arguments.  Require a value for " + theList.toString());
        }
    }

    public void validateInput(Map<String, String> commandLineArguments){
        List<String> requiredParameters = Arrays.asList(DEFAULT_REQUIRED_COMMAND_LINE_ARGUMENTS.split(","));
        validateInput(commandLineArguments, requiredParameters);
    }
}
