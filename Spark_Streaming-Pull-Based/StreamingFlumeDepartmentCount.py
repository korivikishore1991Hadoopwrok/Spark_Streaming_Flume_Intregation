from pyspark import SparkConf, SparkContext
from pyspark.streaming import StreamingContext
from pyspark.streaming.flume import FlumeUtils

import sys

hostname = sys.argv[1]
port = int(sys.argv[2])

conf = SparkConf(). \
setAppName("Streaming Department Count"). \
setMaster("yarn-client")
sc = SparkContext(conf=conf)
ssc = StreamingContext(sc, 30)

agents = [(hostname, port)]
pollingStream = FlumeUtils.createPollingStream(ssc, agents)
messages = pollingStream.map(lambda msg: msg[1])
departmentMessages = messages. \
filter(lambda msg: msg.split(" ")[6].split("/")[1] == "department")
departmentNames = departmentMessages. \
map(lambda msg: (msg.split(" ")[6].split("/")[2], 1))
from operator import add
departmentCount = departmentNames. \
reduceByKey(add)

outputPrefix = sys.argv[3]
departmentCount.saveAsTextFiles(outputPrefix)

ssc.start()
ssc.awaitTermination()