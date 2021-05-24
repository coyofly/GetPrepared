package wordcount

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SparkWordCountSecondarySort {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("SparkWordCountSecondarySort").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd: RDD[(SecondarySortKey, String)] = sc.parallelize(
      List(
        "a b c b e",
        "a b c d",
        "c b c",
        "c b",
        "a")
    ).flatMap(f => f.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .map(f => {
        (new SecondarySortKey(f._1, f._2), s"${f._1}\t${f._2}")
      })
      .sortBy(_._1, true)

    println(rdd.toDebugString)
//    rdd.foreach(x => {println(x._1.count,x._1.word,x._2)})
    rdd.take(10).map(f => println(s"${f._1.word} ${f._1.count}"))
  }

  class SecondarySortKey(val word: String, val count: Int) extends Ordered[SecondarySortKey] with Serializable {
    override def compare(that: SecondarySortKey): Int = {
      if (this.word.compareTo(that.word) != 0) {
        this.word.compareTo(that.word)
      } else {
        this.count - that.count
      }
    }
  }
}
