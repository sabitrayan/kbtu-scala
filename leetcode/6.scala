object Solution {
    def sumZero(n: Int): Array[Int] = {
        var x = n/2
        val res = new Array[Int](n)
        for (i <- 0 to x) {
            res(i) = -(x) + i
        }
        
        var cnt = 1;
        for (i <- x to n) {
            res(i) = cnt
            cnt += 1
        }
        if (n % 2 == 1) 
            res(n - 1) = 0
        
        res
    }
}