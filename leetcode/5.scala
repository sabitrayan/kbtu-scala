object Solution {
    def decompressRLElist(nums: Array[Int]): Array[Int] = {
        var ans = Seq[Int]()
        for(i <- 0 to nums.length -1 by 2) {
            for(x <- 1 to nums(i)){
                ans = ans :+ nums(i + 1)
            }
        }
        Array.from(ans);
    }
}


