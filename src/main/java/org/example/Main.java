package org.example;

import org.apache.commons.cli.*;
import org.example.testcase.SparkApp;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        runTest(args);
    }

    private static void runTest(String[] args) {
        try {
            CommandLineParser parser = new PosixParser();
            Options options = createOptions();

            CommandLine cmd = parser.parse(options, args);
            String className = cmd.getOptionValue("test");

            // get the test class name from enum
            TestOption chosenClass = TestOption.valueOf(className);

            // instantiating the test class
            try {
                Class<?> clazz = Class.forName(chosenClass.getClassName());
                SparkApp instance = (SparkApp) clazz.getDeclaredConstructor().newInstance();
                instance.run(cmd);
            } catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                System.err.println("Error instantiating the class: " + e.getMessage());
                System.exit(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (ParseException e) {
            System.err.println("Error parsing the command line: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        // Add test option to the command option
        Option classOption = new Option("t", "test", true, "The class test must run.");
        classOption.setRequired(true);
        options.addOption(classOption);

        // Add sleep option to the command option
        Option sleepOption = new Option("s", "sleep", true, "Sleep duration in seconds.");
        sleepOption.setType(Integer.class);
        sleepOption.setArgName("sleep");
        sleepOption.setArgs(1);
        options.addOption(sleepOption);

        Option outputOption = new Option("o", "output", true, "The output destination.");
        options.addOption(outputOption);

        Option target = new Option("tg", "target", true, "Dataset target size.");
        target.setType(Integer.class);
        options.addOption(target);

        return options;
    }
}
