object Solution {
    def kidsWithCandies(candies: Array[Int], extraCandies: Int): Array[Boolean] = {
        val maxInt = candies.reduceLeft(_ max _)
        var ans = new Array[Boolean](candies.length)
        for(i<-0 to (candies.length-1)){
            if(candies(i) + extraCandies >= maxInt) ans(i) = true
            else ans(i) = false
        }
        return ans
    }
}