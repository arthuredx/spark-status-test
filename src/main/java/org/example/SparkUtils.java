package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.util.TimeZone;

public class SparkUtils {
    public static SparkSession createSparkSession(String sparkMaster){
        SparkConf conf = new SparkConf();

        if(sparkMaster != null && sparkMaster.trim() != "")
            conf = conf.setMaster(sparkMaster);

        SparkSession spark = SparkSession.builder().config(conf)
                .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
                .config("spark.sql.session.timeZone", "UTC")
                .getOrCreate();

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        //spark.sparkContext().cancelAllJobs();

        return spark;
    }

    public static SparkSession createSparkSession(){
        return createSparkSession("");
    }

    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    public static void log(String format, Object... args){
        System.out.printf(format + "\n", args);
    }

    public static void logWithClassName(String className, String format, Object... args){
        String finalFormat = className + ": " + format;
        log(finalFormat, args);
    }
}
