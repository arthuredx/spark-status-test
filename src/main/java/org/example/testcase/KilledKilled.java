package org.example.testcase;

import org.apache.commons.cli.CommandLine;
import org.apache.spark.sql.SparkSession;
import org.example.SparkUtils;

import static org.example.SparkUtils.log;
import static org.example.SparkUtils.logWithClassName;

public class KilledKilled extends BaseTest implements SparkApp {
    /**
     * Como vai criar a sessão do spark os parâmetros passados via spark-submit
     */
    @Override
    public void run(CommandLine cmd) throws InterruptedException {
        logWithClassName(getClassName(), "Starting spark test");
        SparkSession spark = SparkUtils.createSparkSession();
        log("will run spark sql");
        spark.sql("select 1 as id").show();

        checkSleep(cmd);

        logWithClassName(getClassName(),"finished..");
    }
}
