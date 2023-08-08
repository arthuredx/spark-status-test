package org.example.testcase;

import org.apache.commons.cli.CommandLine;

public interface SparkApp {
    void run(CommandLine cmd) throws Exception;
}
