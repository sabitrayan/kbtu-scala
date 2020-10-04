/**
 * Definition for a binary tree node.
 * class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
 *   var value: Int = _value
 *   var left: TreeNode = _left
 *   var right: TreeNode = _right
 * }
 */
object Solution {
  var res = 999999
  var x: Integer = null
  def minDiffInBST(root: TreeNode): Int = {
    if(root.left != null)
        minDiffInBST(root.left);
    if(x!=null)
        res=Math.min(res,Math.abs(root.value-x));
    x=root.value;
      
    if(root.right!=null)
        minDiffInBST(root.right);
    return res;
  }
}
