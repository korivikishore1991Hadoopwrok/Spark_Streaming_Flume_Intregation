# Spark_Streaming Push Based 
A sample Spark Streaming Code written in both Scala and Python for Push based Streaming applications to link 
Spark_Streaming and Flume.  
Contain a Flume Script to push logs to Avro Sink onto a port in localhost from a driectory(were files get pushed in) 
in Local systeam  
  
## Runing the Flume agent  
```shell
flume_home_directory=/usr/hdp/current/flume/ # for HortonWorks
flume_home_directory=/usr/lib/flume-ng/ # for Cloudera
cd flume_home_directory
bin/flume-ng agent --conf conf --conf-file sparkstreamingflume.conf --name a1
```  
  
## Runing Spark applications  
```shell
export SPARK_MAJOR_VERSION=2
spark-submit --packages org.apache.spark:spark-streaming-flume_2.11:2.0.0 SparkFlume.py
```  

## Passing Inputs  
```shell
cp acess_log.txt /home/maria_dev/spool/log01.txt
cp acess_log.txt /home/maria_dev/spool/log02.txt
```