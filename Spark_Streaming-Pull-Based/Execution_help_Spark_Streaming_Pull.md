# Spark_Streaming Pull Based 
A sample Spark Streaming Code written in both Scala and Python for Pull based Streaming applications to link 
Spark_Streaming and Flume.  
Contain a Flume Script to push logs to HDFS and Spark custom Avro Sink onto a port in localhost from a CMD output
in Local systeam  
  
  
## Prechecks
Sink JARs: Add the JARs for spark-streaming-flume-sink_2.10-1.6.0.jar, commons-lang3-3.3.2.jar and  
scala-library-2.10.5.jar to Flume’s Home Path under /usr/hdp/current/flume/lib(for HortonWorks) or  
/usr/lib/flume-ng/lib(for Cloudera) folder.  
  
  
## Runing the Flume agent  
    
HDFS Directory preparation:  
```shell
hadoop fs -mkdir /user/cloudera/flume_demo1
hadoop fs -ls /user/cloudera/flume_demo1
```
  
Run the Flume agent:  
```shell
flume-ng agent --name sdc --conf-file sdc.conf
```
  
  
## Runing Spark applications  
```shell
# Testing Using code Spark shell
spark-shell --master yarn --executor-cores 3 --num-executors 2 --executor-memory 3G\
 --jars /home/cloudera/spark-streaming-flume_2.10-1.6.0.jar, /usr/lib/flume-ng/lib/commons-lang-2.6.jar, \
 /usr/lib/flume-ng/lib/scala-library-2.10.5.jar, /usr/lib/flume-ng/lib/spark-streaming-flume-sink.jar

# Running Scala Code
chmod +x SparkStreamingPullSBT.jar
zip -d SparkStreamingPullSBT.jar 'META-INF/.SF' 'META-INF/.RSA' 'META-INF/*SF'
spark-submit --master yarn --class FlumeStreamingDepartmentCount /home/cloudera/SparkStreamingPullSBT.jar

# Running Python Script
spark-submit --master yarn \
--jars "/usr/hdp/2.5.0.0-1245/spark/lib/spark-streaming-flume_2.10-1.6.2.jar,\
/usr/hdp/2.5.0.0-1245/spark/lib/spark-streaming-flume-sink_2.10-1.6.2.jar,\
/usr/hdp/2.5.0.0-1245/flume/lib/flume-ng-sdk-1.5.2.2.5.0.0-1245.jar" \
StreamingFlumeDepartmentCount.py \
gw01.itversity.com 8123 /user/dgadiraju/streamingflumedepartmentcount/cnt
```  

## Passing Inputs  
Start the log generation:  
```shell
[cloudera@quickstart strdeptcount]$ cd /opt/gen_logs/
[cloudera@quickstart gen_logs]$ ls -ltr
total 24
-rwxr-xr-x 1 cloudera cloudera   51 Aug  1  2014 tail_logs.sh
drwxr-xr-x 2 cloudera cloudera 4096 Sep 25  2014 logs
drwxr-xr-x 2 cloudera cloudera 4096 Sep 25  2014 data
-rwxr-xr-x 1 cloudera cloudera   76 Oct  8  2014 start_logs.sh
-rwxr-xr-x 1 cloudera cloudera  131 May 14  2015 stop_logs.sh
drwxr-xr-x 2 cloudera cloudera 4096 Oct 23  2017 lib
[cloudera@quickstart gen_logs]$ ./start_logs.sh 
[cloudera@quickstart gen_logs]$ tail -F logs/access.log 
```