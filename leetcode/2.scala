/**
 * Definition for singly-linked list.
 * class ListNode(_x: Int = 0, _next: ListNode = null) {
 *   var next: ListNode = _next
 *   var x: Int = _x
 * }
 */
object Solution {
   def getDecimalValue(head: ListNode): Int = {
    def rec(head: ListNode, res: String): String = {
      if (head.next == null)
        res + head.x
      else
        rec(head.next, res + head.x)
    }
    Integer.parseInt(rec(head, ""), 2)
  }


}

