import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, _}
import akka.http.scaladsl.server.Route
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Backend {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val log: Logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      def route: Route =
        path("back") {
          post {
            entity(as[ListOfRecords]) { numbers =>
              val recordsCount = numbers.values.size
              val recordNames = numbers.values.map(_.value).mkString(", ")
              log.info(s"Received $recordsCount records: $recordNames")
              complete("ok")
            }
          }
        }

      startHttpServer(route)(context.system, context.executionContext)
      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "BackendServiceSisNine")
  }

  def startHttpServer(routes: Route)(implicit system: ActorSystem[_], ex: ExecutionContext): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}