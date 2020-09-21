object Solution {
    def buildArray(target: Array[Int], n: Int): List[String] = {
        var ans = List[String]()
        var cnt = 1
        for(i <- 1 to target(target.length - 1)){
            if(target.contains(i)) ans = ans:+ "Push"
            else{
                ans = ans:+ "Push"
                ans = ans:+ "Pop"
            }
        }
        ans
    }
}


