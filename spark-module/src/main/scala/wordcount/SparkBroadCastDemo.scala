package wordcount

import org.apache.spark.{Accumulator, SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object SparkBroadCastDemo {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("SparkBroadCastDemo").setMaster("local")
    val context = new SparkContext(conf)

    var arr: Array[Int] = Array(1, 2, 3, 4, 5)
    val sourceRDD: RDD[Int] = context.parallelize(arr, 5)

    // 广播变量
    val broad: Broadcast[Array[Int]] = context.broadcast(arr)
    // 累加器
    val acc: Accumulator[Int] = context.accumulator(0)

    val reduceResult: Int = sourceRDD.reduce((a, b) => {
      val broadT: Array[Int] = broad.value
      //这是使用的广播变量
      println(s"broad:${broadT.toList}")
      //这是使用的外部变量
      println(s"arr:${arr.toList}")
      //累加结果只能使用累加器，不能使用外部变量，因为在分布式环境下，外部变量是不同步的
      acc.add(1)
      a + b
    })
    println(s"打印reduceResult:${reduceResult}")
    println(acc)

  }
}

