package org.apache.spark.examples.streaming
import java.util.Properties
import kafka.producer._
import scala.util.parsing.json._
import org.apache.spark.streaming._
import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import com.datastax.spark.connector.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
object brandsstat {
def main(args: Array[String]) {
//Spark Cluster da master node uzerinde iş başlatmak için 4 parametreye ihtiyaç var.
//Bu parametrelerin verilip verilmediğini aşağıdaki IF
//kontrol eder. Eğer geçerli parametreler verilmedi ise hata mesajı ile programdan çıkar.
        if (args.length < 4) {
                System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
                System.exit(1)
                }
        StreamingExamples.setStreamingLogLevels()
/*
conf değişkeninin üzerine gerekli konfigürasyon parametreleri atanır.
 Parametre 1: Cassandra cluster da seed olarak belirlenen makinaların isim yada IP adresleri yazılır.
                        IP adreslerinin değişme olasılığına karşı host name olarak vermek daha verimli olacaktır.
 Parametre 2: Spark master ın ismi yada IP adresi ve port numarası
 Parametre 3: Çalıştırılacak uygulamanın adı.
*/
        val conf = new SparkConf(true).set("spark.cassandra.connection.host", "cassandra1").setMaster("spark://sparkmaster:7077").setAppName("brandsstat")
//Dışardan parametre olarak verdiğimiz değerleri array in içine gönderiyoruz.
        val Array(zkQuorum, group, topics, numThreads) = args
/*
  Streaming context in parametrelerini ssc değişkenine veriyoruz.
  Parametre1: Konfigurasyon parametrelirini içinde barındıran conf değişkeni.
  Parametre2: 1. parametrede ki konfigürasyon ayarları ile gelen DStream zincirinin okunması gereken periyot. Milisaniye mertebelerinden
  saniyelere kadar istediğimiz periyodu verebiliriz.
*/
        val ssc = new StreamingContext(conf, Seconds(5))
/*
        Fault Tolerant için checkpoint oluşturuluyor. İşlem devam ederken çeşitli noktalarda checkpoint bilgisini checkpoint klasörüne
        yazıyor. Eğer hata olursa checkpoint lerden geri dönülebilir. Daha fazla bilgi için aşağıdaki link i takip edebilirsiniz.
        https://spark.apache.org/docs/1.2.0/streaming-programming-guide.html#checkpointing
*/
        ssc.checkpoint("checkpoint")
/*
        topicMap degiskenine uygulamayı çalıştırmak için verdiğimiz parametrelerden olan topic Name i aradaki virgül dikkate alınarak parse
        edilir. Thread sayısınıda topic adıyla beraber parametre olarak bu değişkene veriyoruz.
*/
        val topicMap = topics.split(",").map((_,numThreads.toInt)).toMap
// lines değişkenine her bir kayıt ı alıyoruz.
        val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2).map(x => (x, 1)).reduceByKey(_ + _).saveToCassandra("brands", "brands_sta$
//      lines.print()
        ssc.start()
        ssc.awaitTermination()
        }
}
