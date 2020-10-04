object Solution {
    def tribonacci(n: Int): Int = {
        //if(n == 1 || n == 2) return 1
        //if(n == 0) return 0
        //return tribonacci(n-1) + tribonacci(n-2) + tribonacci(n-3)
        
        if(n<2) n
        else{
            var ans = Array(0,1,1)
            for(i<-2 to n)
                ans = ans :+ (ans(i) + ans(i-1) + ans(i-2))
            ans(n)
        }
    }
}