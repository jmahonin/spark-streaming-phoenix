import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, Minutes, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.phoenix.spark._

/*
Requires a Phoenix table with the following DDL:
CREATE TABLE STREAM_TEST(DT DATE NOT NULL, COUNT BIGINT CONSTRAINT pk PRIMARY KEY(DT));
 */

object SparkStreamingPhoenix extends App{
  val kafkaZk = "integration2:2181"
  val phoenixZk = "fennyserver:2181"

  val conf = new SparkConf()
    .setAppName("SparkStreamingPhoenix")
    .setMaster("local[4]")

  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(5))

  val topics = Map("josh-test" -> 1)
  val kafkaStream = KafkaUtils.createStream(ssc, kafkaZk, "spark-streaming-phoenix", topics)

  // countByWindow would be better, I can't get it working though
  kafkaStream.foreachRDD { rdd =>
    val count = rdd.count()
    // Rebroadcast the time and count as an RDD to use the builtin 'saveToPhoenix'
    // This could just as easily be a JDBC UPSERT
    sc
      .parallelize(
        Seq(
          (new Date().getTime(), count)
        )
      )
      .saveToPhoenix("STREAM_TEST", Seq("DT", "COUNT"), zkUrl = Some(phoenixZk))
  }

  ssc.start()
  ssc.awaitTermination()
}