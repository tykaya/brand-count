# brand-count
brand counter
Cluster Yapısı:
  
  1 adet Kafka barındıran makina
  1 adet spark barındıran "sparkmaster" makinası
  1 adet spark barındıran "sparkworker" makinası
  1 adet Cassandra barındıran "cassandra1" makinası
  
Kafka üzerine Node.js kurulup producer.js run edilmeli.
Spark Spark Streaming içeren versiyonu ile birlikte kurulmuş olmalı. 

1) Kolay olması icin brandsstat.scala dosyası aşağıdaki path a koyulmalı: 

spark/examples/scala-2.10/src/main/scala/org/apache/spark/examples/streaming/brandsstat.scala

2) Ardından aşağıdaki komut ile compile edilip örneklerin bulunduğu jar dosyasına eklenmeli.

mvn package -rf :spark-examples_2.10

3) sparkmaster master olarak çalıştırılır.

./sbin/start-master.sh

4) sparkworker worker olarak çalıştırılır

./bin/spark-class org.apache.spark.deploy.worker.Worker spark://spark-master:7077

5) Cassandra makinasının üzerinde cql den aşağıdaki gibi keyspace ve tablo oluşturulur.

CREATE KEYSPACE brands WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};
CREATE TABLE brands.brands_stat (brand text PRIMARY KEY,count int);

6) Kafka üzerinde ZooKeeper çalıştırılır.

bin/zookeeper-server-start.sh config/zookeeper.properties

7) Kafka üzerinde Kafka server çalıştırılır.

bin/kafka-server-start.sh config/server.properties

8) Aşağıdaki satır, sparkmaster üzerinden çalıştırılır.

./bin/spark-submit --class org.apache.spark.examples.streaming.brandsstat --master spa
rk://sparkmaster:7077 ./examples/target/scala-2.10/spark-examples-1.2.0-hadoop1.0.4.jar  104.155.10.21:2181 test
-consumer-group test 1

9) sparkmaster üzerinde producer.js dosyası çalıştırılır.

node producer.js


