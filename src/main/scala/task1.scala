package com.tedneward.calc
{
  private[calc]  abstract class Allin
  private[calc]  case class Number(value : Double) extends Allin
  private[calc]  case class UnaryOp(operator : String, arg : Allin) extends Allin
  private[calc]  case class BinaryOp(operator : String, left : Allin, right : Allin)
    extends Allin
  object Calculator
  {
    def evaluate(e : Allin) : Double =
    {
      operand_Code(e) match {
        case Number(x) => x
        case UnaryOp("-", x) => -(evaluate(x))
        case BinaryOp("+", x1, x2) => (evaluate(x1) + evaluate(x2))
        case BinaryOp("-", x1, x2) => (evaluate(x1) - evaluate(x2))
        case BinaryOp("*", x1, x2) => (evaluate(x1) * evaluate(x2))
        case BinaryOp("/", x1, x2) => (evaluate(x1) / evaluate(x2))
      }
    }

    def operand_Code(e : Allin) : Allin =
    {
      e match {
        case UnaryOp("-", UnaryOp("-", x)) => x
        case UnaryOp("+", x) => x
        case BinaryOp("*", x, Number(1)) => x
        case BinaryOp("*", Number(1), x) => x
        case BinaryOp("*", x, Number(0)) => Number(0)
        case BinaryOp("*", Number(0), x) => Number(0)
        case BinaryOp("/", x, Number(1)) => x
        case BinaryOp("+", x, Number(0)) => x
        case BinaryOp("+", Number(0), x) => x
        case _ => e
      }
    }
  }
}
