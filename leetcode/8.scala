/*
 * // This is the custom function interface.
 * // You should not implement it, or speculate about its implementation
 * class CustomFunction {
 *     // Returns f(x, y) for any given positive integers x and y.
 *     // Note that f(x, y) is increasing with respect to both x and y.
 *     // i.e. f(x, y) < f(x + 1, y), f(x, y) < f(x, y + 1)
 *     def f(x: Int, y: Int): Int = {}
 * };
 */

object Solution {
    def findSolution(customfunction: CustomFunction, z: Int): List[List[Int]] = {
        var myMatrix =  List[List[Int]]()
        
        for(x<-1 to z){
            for(y<-1 to z){
                if(customfunction.f(x,y) == z) {
                    var first = List[Int]()
                    first = first :+ x
                    first = first :+ y
                    myMatrix=myMatrix:+first
                }
            }
        }
        myMatrix
    }
}

