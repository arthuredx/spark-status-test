package org.example.testcase;

import org.apache.commons.cli.CommandLine;
import org.apache.spark.sql.SparkSession;
import org.example.SparkUtils;

import static org.example.SparkUtils.log;
import static org.example.SparkUtils.logWithClassName;

public class FailedFailed extends BaseTest implements SparkApp {
    /**
     * Como vai criar a sessão do spark usando local[*] se executar em um cluster yarn
     * vai gerar state failed para YARN e final status failed para spark.
     */
    @Override
    public void run(CommandLine cmd) throws InterruptedException {
        logWithClassName(getClassName(),"Starting spark test");
        SparkSession spark = SparkUtils.createSparkSession("local[*]");
        log("will run spark sql");
        spark.sql("select 1 as id").show();
        checkSleep(cmd);
        logWithClassName(getClassName(),"finished..");
    }
}
