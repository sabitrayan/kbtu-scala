/**
 * Definition for a binary tree node.
 * class TreeNode(_value: Int = 0, _left: TreeNode = null, _right: TreeNode = null) {
 *   var value: Int = _value
 *   var left: TreeNode = _left
 *   var right: TreeNode = _right
 * }
 */
object Solution {
    def longestUnivaluePath(root: TreeNode): Int = {
        var ans = 0
        
        def rec(root: TreeNode): Int = {
            if (root == null) return 0
        
            var lans = rec(root.left)
            var rans = rec(root.right)
        
            if (root.left != null && root.value == root.left.value) lans += 1
            else lans = 0
        
            if (root.right != null && root.value == root.right.value) rans += 1
            else rans = 0
            
            ans = Math.max(ans, lans + rans)
            return Math.max(lans, rans)
        }
        
        rec(root)
        ans 
    }
}