package org.example.testcase;

import org.apache.commons.cli.CommandLine;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.example.SparkUtils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.example.SparkUtils.log;
import static org.example.SparkUtils.logWithClassName;

public class CollectMiB extends BaseTest implements SparkApp{
    /**
     * Executa um select no spark e faz um sleep durante a execução do job do spark de acordo com o tempo passado
     * e imprime o dado na tela.
     * @param cmd
     * @throws Exception
     */
    @Override
    public void run(CommandLine cmd) throws Exception {
        SparkUtils.logWithClassName(getClassName(), "Starting spark test");
        SparkSession spark = SparkUtils.createSparkSession();
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        int byteSize = 1;
        int recordSizeInByte = byteSize * 1024; // 1 KiB per line
        char[] chars = new char[recordSizeInByte];
        Arrays.fill(chars, 'a');
        String text = new String(chars);

        /*
        1024 bytes = 1 KiB
        1024 kiB = 1 MiB
        1024 Mib = 1 GiB
         */
        int targetMiB = getDataSetTargeSize(cmd); //default 1024
        int targetKiB = targetMiB * 1024;
        int targetBytes = targetKiB * 1024;
        int totalRecords = targetBytes / recordSizeInByte;

        log("totalRecords: %s, targetBytes: %s, targetMiB: %s", totalRecords, targetBytes, targetMiB);

        JavaRDD<Long> rddLongs = jsc.parallelize(
                LongStream.rangeClosed(1, totalRecords).boxed().collect(Collectors.toList())
        );

        StructType schema = new StructType()
                .add("long", DataTypes.LongType)
                .add("text", DataTypes.StringType);

        Dataset<Row> df = spark.createDataFrame(
                rddLongs.map(x -> {
                    return RowFactory.create(x, text);
                }),
                schema
        );

        checkSaveDf(df, cmd);

        df.collect();

        checkSleep(cmd);

        logWithClassName(getClassName(),"finished..");
    }


}
