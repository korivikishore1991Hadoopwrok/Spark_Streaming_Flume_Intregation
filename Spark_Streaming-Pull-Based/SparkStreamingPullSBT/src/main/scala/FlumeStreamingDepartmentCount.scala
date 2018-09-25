import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}
import org.apache.spark.streaming.flume._

object FlumeStreamingDepartmentCount {
  def main(args: Array[String]) {
    val conf = new SparkConf().
      setAppName("Flume Streaming Word Count")
    val ssc = new StreamingContext(conf, Seconds(30))

    val stream = FlumeUtils.createPollingStream(ssc,
      "localhost", 7779)
    val messages = stream.
      map(s => new String(s.event.getBody.array()))
    val departmentMessages = messages.
      filter(msg => {
        val endPoint = msg.split(" ")(6)
        endPoint.split("/")(1) == "department"
      })
    val departments = departmentMessages.
      map(rec => {
        val endPoint = rec.split(" ")(6)
        (endPoint.split("/")(2), 1)
      })
    val departmentTraffic = departments.
      reduceByKey((total, value) => total + value)
    departmentTraffic.saveAsTextFiles(
      "/user/cloudera/sparkstreamingHDFS1/cnt")

    ssc.start()
    ssc.awaitTermination()

  }
}