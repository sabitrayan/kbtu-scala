import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

import io.circe.syntax._

object ServiceThree extends App {

  import io.circe.generic.auto._

  implicit val system = ActorSystem("Systema") // materializer
  implicit val ec = system.dispatcher

  val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
  val committerConfig = system.settings.config.getConfig("akka.kafka.committer")
  val bootstrapServers = "localhost:9092"
  val groupId = "group1"
  val subTopics = List("consumer-doubler-topic", "consumer-tripler-topic")
  var records: List[Record] = List()

  val consumerSettings =
    ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val control =
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(subTopics: _*))
      .toMat(Sink.foreach(str => records = records :+ Record(str.record.value())))(DrainingControl.apply)
      .run()

  val done = Future {
    Thread.sleep(8000)
    control.drainAndShutdown()
  }

  done.onComplete {
    case Success(_) =>
      println("everything went ok")
      post(ListOfRecords(records))
      Thread.sleep(2000)
      system.terminate()
    case Failure(exception) =>
      println(s"something wrong happened: $exception")
      system.terminate()
  }

  def post(data: ListOfRecords): Unit = {
    val responseFuture: Future[HttpResponse] =
      Http(system).singleRequest(
        HttpRequest(
          HttpMethods.POST,
          uri = "http://localhost:8080/back",
          entity = HttpEntity(ContentTypes.`application/json`, ListOfRecords(records).asJson.toString())
        )
      )

    responseFuture.onComplete {
      case Success(value) =>
        println(value)
      case Failure(exception) =>
        println(exception)
    }
  }

}