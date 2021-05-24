package wordcount

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Durations, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

object SparkStreamingWordCount {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("SparkStreamingWordCount")
    // conf.set("spark.streaming.blockInterval", "1000")
    // 设置批次时间5S
    val duration: Duration = Durations.seconds(5)
    val context: StreamingContext = new StreamingContext(conf,duration)
    context.checkpoint("./dir")
    // 指定socket数据源
    val sourceDStream: ReceiverInputDStream[String] = context.socketTextStream("localhost", 9000)
    // 计算WordCount
    val resultDStream: DStream[(String, Int)] = sourceDStream.flatMap(f => f.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    resultDStream.updateStateByKey(updateFunc).print()

    context.start()             // Start the computation
    context.awaitTermination()  // Wait for the computation to terminate
  }

  //currentValues:当前批次的value值,如:1,1,1 (以测试数据中的hadoop为例)
  //historyValue:之前累计的历史值,第一次没有值是0,第二次是3
  //目标是把当前数据+历史数据返回作为新的结果(下次的历史数据)
  def updateFunc(currentValues:Seq[Int], historyValue:Option[Int] ):Option[Int] ={
    // currentValues当前值
    // historyValue历史值
    val result: Int = currentValues.sum + historyValue.getOrElse(0)
    // Some  数据是什么就返回什么  没有返回 None
    Some(result)

  }

}
