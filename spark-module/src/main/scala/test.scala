import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
class testRdd {
  def anylizeRdd():Unit={
    val spark= new SparkContext(new SparkConf().setMaster("local").setAppName("test"))
    var arr: Array[Int] = Array(1, 2, 3, 4, 5)
    val bc = spark.broadcast(arr)
    val sourceRDD: RDD[Int] = spark.parallelize(arr, 5)
    val number=sourceRDD.repartition(10).aggregate(50)((x:Int,y:Int)=>{
      x+y
    },(a:Int,b:Int)=>{
      //b表示每一个分区最后计算的值，a表示计算中间值，初始为0
      a+b
    })
    println(number)
  }
}
object  testRdd{
//  def main(args: Array[String]): Unit = {
//    new testRdd().anylizeRdd()
//  }

  def main(args: Array[String]): Unit = {
    val seq = Seq(("a1",("A","1,2,3")),("a2",("A","1,2,3")),("a3",("A","1,2,3")),("a4",("A","1,2,3")))

    val res = seq.aggregate("2,4,6")( (x,y) => getAddition(x, y._2._2), (x,y) => getAddition(x,y))
    println(res)
  }
  def getAddition(fist:String,second:String):String ={
    val s1 = fist.split("\\,")
    val s2 = second.split("\\,")
    val resSeq = for (i <- 0 until s1.length) yield (s1(i).toInt + s2(i).toInt).toString
    resSeq.mkString(",")
  }

}
