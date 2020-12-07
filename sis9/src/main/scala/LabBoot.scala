import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

object LabBoot extends App {
  implicit val system = ActorSystem("System") // materializer
  implicit val ec = system.dispatcher

  def f1() = {
    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    // connect the Source to the Sink, obtaining a RunnableGraph
    val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

    // materialize the flow and get the value of the FoldSink
    val sum: Future[Int] = runnable.run()

    // handle the future
    handle(sum)
  }

  def f2() = {
    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    val sum: Future[Int] = source.runWith(sink)

    // handle the future
    handle(sum)
  }

  def f3() = {
    // connect the Source to the Sink, obtaining a RunnableGraph
    val sink = Sink.fold[Int, Int](0)(_ + _)
    val runnable: RunnableGraph[Future[Int]] =
      Source(1 to 10).toMat(sink)(Keep.right)

    // get the materialized value of the FoldSink
    val sum1: Future[Int] = runnable.run()
    val sum2: Future[Int] = runnable.run()

    // sum1 and sum2 are different Futures!
    handle(sum1)
    handle(sum2)
  }

  def f4() = {
    Source(1 to 6).via(Flow[Int].map(_ * 6)).to(Sink.foreach(println)).run()
  }

  def f5() = {
    // Broadcast to a sink inline
    val otherSink: Sink[Int, NotUsed] = Flow[Int].alsoTo(Sink.foreach(println(_))).to(Sink.ignore)
    Source(1 to 6).to(otherSink).run()
  }

  def f6() = {
    val config = system.settings.config.getConfig("akka.kafka.producer")
    val bootstrapServer = system.settings.config.getString("akka.kafka.producer.kafka-clients.bootstrapServer")
    val topic = system.settings.config.getString("akka.kafka.producer.kafka-clients.topic")

    val producerSettings =
      ProducerSettings(config, new StringSerializer, new StringSerializer)
        .withBootstrapServers(bootstrapServer)

    val done: Future[Done] = Source.fromIterator(() => Iterator.continually(Random.nextInt(100)).take(15))
      .map(_.toString)
      .map(value => new ProducerRecord[String, String](topic, value))
      .runWith(Producer.plainSink(producerSettings))

    handle(done)
  }

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

  def test() = {
    Iterator.continually(Random.nextInt(10)).take(10).foreach(println)
  }

  f6()
}