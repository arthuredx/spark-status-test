package org.example.testcase;

import org.apache.commons.cli.CommandLine;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.example.SparkUtils;

import static org.example.SparkUtils.log;
import static org.example.SparkUtils.logWithClassName;

public class Foreach extends BaseTest implements SparkApp{
    /**
     * Executa um select no spark e faz um sleep durante a execução do job do spark de acordo com o tempo passado
     * e imprime o dado na tela.
     * @param cmd
     * @throws Exception
     */
    @Override
    public void run(CommandLine cmd) throws Exception {
        logWithClassName(getClassName(), "Starting spark test");
        SparkSession spark = SparkUtils.createSparkSession();
        log("will run spark sql");

        Dataset<Row> dataframe = spark.sql("select 1 as id");
        dataframe.foreach(new ForeachPartition(cmd));

        logWithClassName(getClassName(),"finished..");
    }

    private static class ForeachPartition implements ForeachFunction<Row> {
        CommandLine cmd;
        public ForeachPartition(CommandLine cmd){
            this.cmd = cmd;
        }
        @Override
        public void call(Row row) throws Exception {
            log("starting foreach");
            String sleepArg = cmd.getOptionValue("sleep");
            if(sleepArg != null) {
                Integer sleepTime = Integer.parseInt(cmd.getOptionValue("sleep"));
                log("sleeping %s seconds", sleepTime);
                Thread.sleep(sleepTime * 1000);
            }
            log("row id: %s", row.get(0));
        }
    }
}
