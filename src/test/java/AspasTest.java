import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.util.TimeZone;

public class AspasTest {

    @Test
    public void testAspas(){
        SparkConf conf = new SparkConf();

        SparkSession spark = SparkSession.builder().config(conf)
                .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
                .config("spark.sql.session.timeZone", "UTC")
                .master("local[*]")
                .getOrCreate();

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String text01 = "texto com \"aspas.\"";
        String query = String.format("select 'texto sem aspas' as textoSemAspas, '%s' as textoComAspas", text01);
        System.out.printf("query: %s \n\n", query);

        Dataset<Row> df = spark.sql(query);
        df.show(2);
        String destination1 = "target/parquet-com-aspas";
        df.write().format("parquet").mode(SaveMode.Overwrite).save(destination1);

        df.createOrReplaceTempView("sourceDf");

        Dataset<Row> df2 = spark.sql("select *, map('textoSemAspas',textoSemAspas, 'textoComAspas', textoComAspas) as map, to_json(map('textoSemAspas',textoSemAspas, 'textoComAspas', textoComAspas)) as json  from sourceDf");
        df2.show();
        String destination2 = "target/parquet-com-json";
        df2.write().format("parquet").mode(SaveMode.Overwrite).save(destination2);

        String tags = " select 'inscríbete a mi programa \"soy abundante!\"' as utm_campaign, 'social' as utm_medium";
        String tags_semAspas = " select '{\"utm_source\":\"linktree\",\"utm_medium\":\"social\"}' as tags";
        Dataset<Row> df3 = spark.sql(tags);
        df3.show(2);
        String destination3 = "target/parquet-com-gson";
        df3.write().format("parquet").mode(SaveMode.Overwrite).save(destination3);
        df3.createOrReplaceTempView("sourceDf2");
        Dataset<Row> df4 = spark.sql("select *, map('utm_campaign',utm_campaign, 'utm_medium', utm_medium) as filtered_tags   from sourceDf2");
        df4.show();
        df4.write().format("parquet").mode(SaveMode.Overwrite).save(destination3);
        df4.createOrReplaceTempView("sourceDf3");
        Dataset<Row> df5 = spark.sql("select *, transform_values(filtered_tags, (k, v) -> regexp_replace(v, '%(?![0-9a-fA-F]{2})', '%25')   ) AS sanitized_tags from sourceDf3");
        df5.show();
        df5.write().format("parquet").mode(SaveMode.Overwrite).save(destination3);
        df5.createOrReplaceTempView("sourceDf4");
        Dataset<Row> df6 = spark.sql("select *, regexp_replace(to_json(sanitized_tags), '(\\\\n|\\\\t|\\\\r|\\\\b)', '') AS tags from sourceDf4");
        df6.show();
        df6.write().format("parquet").mode(SaveMode.Overwrite).save(destination3);

    }

    @Test
    public void testAspas2(){
        SparkConf conf = new SparkConf();

        SparkSession spark = SparkSession.builder().config(conf)
                .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
                .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
                .config("spark.sql.session.timeZone", "UTC")
                .master("local[*]")
                .getOrCreate();

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String file = "part-00000-1cdb38fd-d5a2-414b-b555-97d67d227120.c000.snappy.parquet";
        String source = "/Users/arthur.edson/Downloads/"+file;
        Dataset<Row> df = spark.read().parquet(source).select("hub_transaction_datetime", "tags"); //.where("affiliation_code='T84834910'");
        df = df.where("tags LIKE '%soy abundante%'");
        df.show(10);
        String destination2 = "target/parquet-com-json-filtrado";
        df.write().mode(SaveMode.Overwrite).parquet(destination2);

    }

}
