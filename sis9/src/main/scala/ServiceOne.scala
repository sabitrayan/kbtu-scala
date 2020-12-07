import akka.actor.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Consumer, Producer}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

object ServiceOne extends App {
  implicit val system = ActorSystem("Systema") // materializer
  implicit val ec = system.dispatcher

  val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
  val committerConfig = system.settings.config.getConfig("akka.kafka.committer")
  val producerConfig = system.settings.config.getConfig("akka.kafka.producer")
  val bootstrapServers = "localhost:9092"
  val groupId = "group1"
  val mainTopic = "consumer-doubler-topic"
  val subTopic = "producer-topic"

  val consumerSettings =
    ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings =
    ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  val committerSettings = CommitterSettings(committerConfig)

  def double(value: String): String = (Integer.parseInt(value) * 2).toString

  val control =
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(subTopic))
      .map { msg =>
        ProducerMessage.single(
          new ProducerRecord(mainTopic, msg.record.key, double(msg.record.value)),
          msg.committableOffset
        )
      }
      .toMat(Producer.committableSink(producerSettings, committerSettings))(DrainingControl.apply)
      .run()

  val done = Future {
    Thread.sleep(8000)
    control.drainAndShutdown()
  }

  done.onComplete(_ => system.terminate())
}