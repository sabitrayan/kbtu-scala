import java.io.{File, PrintWriter}
import java.util.Base64.Encoder
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.matching.Regex

object Parser extends App {

  def pars(): Unit = {
    val file = Source.fromFile("/Users/corogly/Desktop/scala_SIS5/raw.txt").mkString

    case class Product(id: String, name: String, count: String, cost: String, totalPrice: String)
    case class Check(products: Array[Product])

    var LineCnt = 1
    var name: String = ""
    var id: String = ""
    var count: String = ""
    var cost: String = ""
    var totalPrice: String = ""

    var check = Array[Product]()

    for(i <- 0 to file.length) {
      if (file(i).trim.matches("[0-9]+[.]")) {
        id = file(i)
        LineCnt = LineCnt + 1;
        name = file(i);
        LineCnt = LineCnt + 1;
        count = file(i).substring(0, file(i).indexOf("x") + 2)
        cost = file(i).substring(0, file(i).indexOf("x") - 1)
        LineCnt = LineCnt + 1;
        totalPrice = file(i)
        LineCnt = LineCnt + 3;
        val product = Product(id, name, count, cost, totalPrice)
        check = check :+ product
        LineCnt = LineCnt + 1;
      }
    }

    var Json = ListBuffer[String]()
    for (p <- check) {
      Json= Encoder[Product].apply(p).toString
    }

    val pw = new PrintWriter(new File("/Users/corogly/Desktop/scala_SIS5/ans.txt"))
    for (p <- Json) {
      pw.write(p + "\n")
    }
    pw.close
  }
  pars()
}

