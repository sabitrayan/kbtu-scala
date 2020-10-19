import akka.actor._
import language.postfixOps
import AkkaMessages._
import ExampleCalc._

object AkkaComputeApp extends App {
  val exampleSystem = ActorSystem("exampleSystem")
  val computeManager = exampleSystem.actorOf(Props[ComputeManager], name = "computing-manager")
  computeManager ! Compute(inputSet)
}

class ComputeManager extends Actor with ActorLogging{
  var results = collection.mutable.Set[Any]()
  var computingWorker  = collection.mutable.Set[ActorRef]()
  override def receive: Receive = {
    case Compute(inputSet) => {
      inputSet.map{ input =>
        log.info(s"Creating new worker for $input")
        val compUnit = context.actorOf(Props(classOf[CompUnit],someCalculationFunc))
        context.watch(compUnit)
        computingWorker add compUnit
        compUnit ! SingleCompute(input)
      }
    }
    case Result(result) => {
      log.info(s"Got result $result")
      results add result
    }
    case Terminated(someOne) => {
      log.info(s"Got termination from  $someOne")
      computingWorker remove someOne
      if (computingWorker.isEmpty) {
        log.info(s"finished working, result : $results")
        context.stop(self)
      }
    }
  }
}

class CompUnit(compFunc: (Any => Any) ) extends Actor with ActorLogging{
  override def receive: Receive = {
    case SingleCompute(input) => {
      log.info(s"Start to compute $input")
      sender ! Result(compFunc(input))
      log.info(s"Finished to compute $input")
      context.stop(self)
    }
  }
}

object AkkaMessages {

  sealed trait Message
  case class Compute(inputSet:Iterable[Any]) extends Message
  case class SingleCompute(input:Any) extends Message
  case class Result(result:Any) extends Message

}

object ExampleCalc {
  val someCalculationFunc : (Int) => Int =  _ + 10
  val inputSet = Range(1,5)
}