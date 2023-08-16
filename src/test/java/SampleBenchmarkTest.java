import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SampleBenchmarkTest {
    private static final int NUMBER_EXECUTIONS = 100;
    private static final String UTM_SAMPLE = "inscr%C3%ADbete+a+mi+programa+%22soy+abundante%22!";

    @Test
    public void testA(){
        long time = 0;
        for (int i = 0; i < NUMBER_EXECUTIONS; i++) {
            long startTime = System.nanoTime();
            //do something here
            long endTime = System.nanoTime();
            //increment total time
            time = time + (endTime - startTime);
        }
        long avgTime = time / NUMBER_EXECUTIONS;
        double seconds = TimeUnit.NANOSECONDS.toSeconds(avgTime);
        System.out.printf("testA executed %s times with avgTime: %s nanossegundos (%s segundos)",NUMBER_EXECUTIONS, avgTime, seconds);
    }

    @Test
    public void testB(){
        long time = 0;
        SparkSession spark = createSparkSession();
        Dataset<Row> dfBase = spark.sql(String.format("SELECT '%s' as utm", UTM_SAMPLE));
        dfBase.createOrReplaceTempView("source");
        for (int i = 0; i < NUMBER_EXECUTIONS; i++) {
            long startTime = System.nanoTime();
            //do something here
            spark.sql("SELECT utm, regexp_replace(utm, '%22', '%5C%22') as sanitized_utm from source").show(false);
            long endTime = System.nanoTime();
            //increment total time
            time = time + (endTime - startTime);
        }
        long avgTime = time / NUMBER_EXECUTIONS;
        double seconds = TimeUnit.NANOSECONDS.toSeconds(avgTime);
        System.out.printf("testA executed %s times with avgTime: %s nanossegundos (%s segundos)",NUMBER_EXECUTIONS, avgTime, seconds);
    }

    public SparkSession createSparkSession(){
        SparkConf conf = new SparkConf();

        SparkSession spark = SparkSession.builder().config(conf)
                .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
                .config("spark.sql.session.timeZone", "UTC")
                .master("local[*]")
                .getOrCreate();

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return spark;
    }
}
