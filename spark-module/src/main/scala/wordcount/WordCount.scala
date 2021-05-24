package wordcount

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    conf.setMaster("local[3]")
      .setAppName("wordCount")
    val context = new SparkContext(conf)
    val sourceRdd = context.parallelize(
      List(
        "a b c d e f g h m n o p",
        "a b c d e f g h m n o",
        "a b c d e f g h m n",
        "a b c d e f g h m",
        "a b c d e f g h",
        "a b c d e f g",
        "a b c d e f",
        "a b c d e",
        "a b c d",
        "a b c",
        "a b",
        "a"
      ), numSlices = 10
    )
    val wcRDD = sourceRdd.flatMap(f => f.split("\\s"))
      .map((_, 1)).reduceByKey((_ + _), 8)
    wcRDD.foreach(f => {
      println(f)
    })
    println(wcRDD.toDebugString)
    context.stop()
  }
}
