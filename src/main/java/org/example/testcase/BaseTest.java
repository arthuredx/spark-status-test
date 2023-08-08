package org.example.testcase;

import org.apache.commons.cli.CommandLine;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import static org.example.SparkUtils.log;
import static org.example.SparkUtils.logWithClassName;

public class BaseTest {
    public String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    public void checkSleep(CommandLine cmd) throws InterruptedException {
        String sleepArg = cmd.getOptionValue("sleep");
        if(sleepArg != null) {
            Integer sleepTime = Integer.parseInt(cmd.getOptionValue("sleep"));
            log("sleeping %s seconds", sleepTime);
            Thread.sleep(sleepTime * 1000);
        }
    }

    protected void checkSaveDf(Dataset<Row> df, CommandLine cmd) {
        String output = cmd.getOptionValue("output");
        if(output != null) {
            log("saving df to %s", output);
            df.write()
                    .mode(SaveMode.Overwrite)
                    .format("csv")
                    .save(output);
        }
    }

    protected int getDataSetTargeSize(CommandLine cmd) {
        String targetSize = cmd.getOptionValue("target");
        if(targetSize != null) {
            return Integer.parseInt(targetSize);
        }
        return 1024;
    }
}
