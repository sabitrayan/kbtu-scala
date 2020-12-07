import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

object NumberGeneratorService extends App {
  implicit val system = ActorSystem("System")
  implicit val ec = system.dispatcher

  val config = system.settings.config.getConfig("akka.kafka.producer")
  val bootstrapServers = "localhost:9092"
  val topic = "producer-topic"

  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  val done: Future[Done] = Source.fromIterator(() => Iterator.continually(Random.nextInt(100)).take(15)).throttle(1, 1.seconds)
    .map(_.toString)
    .map(value => new ProducerRecord[String, String](topic, value))
    .runWith(Producer.plainSink(producerSettings))

  handle(done)

  def handle(f: Future[Any]) = {
    f.onComplete {
      case Success(value) =>
        println(s"everything went ok, the result is: $value")
        system.terminate()
      case Failure(exception) =>
        println(s"something wrong happened: $exception")
        system.terminate()
    }
  }
}