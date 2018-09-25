import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.flume.FlumeUtils

import scala.util.matching.Regex

object sparkStreamingSample {
  def main(args: Array[String]): Unit = {
    val parts ="""\s*(\S+) \S+ (\S+) \[(.+)\] "(.+)" ([0-9]+) (\S+) "(.*)" "(.*)"\s*\Z""".r
    //val line = """66.249.75.159 - - [29/Nov/2015:03:50:05 +0000] "GET /robots.txt HTTP/1.1" 200 55 "-" "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)""""
    //val line = """180.76.15.138 - - [29/Nov/2015:05:00:24 +0000] "GET / HTTP/1.1" 301 - "-" "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)"""""
    val conf = new SparkConf().setAppName("Spark Streaming Sample")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))

    val flumeStream = FlumeUtils.createStream(
      ssc, "localhost", 9092)

    val lines = flumeStream.map(x => new String(x.event.getBody.array()))
    // Or Can also try 
    // Assuming that our flume events are UTF-8 log lines
    // val lines = flumeStream.map{e => new String(e.event.getBody().array(), "UTF-8")}
    val urls = lines.map(line => extractURLRequest(
      line.toString, parts))

    // Reduce by URL over a 5-minute window sliding every second
    val urlCounts = urls.map(x => (x, 1)
    ).reduceByKeyAndWindow({(x, y)=> x+y}, {(x, y) => x - y},
      Seconds(300), Seconds(1))

    // Sort and print the results
    val sortedResults = urlCounts.transform(rdd =>
      rdd.sortBy({x => x._2}, ascending = false))
    sortedResults.print

    ssc.checkpoint("/home/arc/checkpoint")
    ssc.start()
    ssc.awaitTermination()
  }
  def extractURLRequest(line: String, parts: Regex):String ={
    val parts(host, user, time, request, status, size, referer, agent) = line
    val requestFields = request.split(" ")
    if(requestFields.length > 1){
      return requestFields(0)
    }
    else {
      return ""
    }
  }
}
