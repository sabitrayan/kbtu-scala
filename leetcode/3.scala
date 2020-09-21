object Solution {
    def smallerNumbersThanCurrent(nums: Array[Int]): Array[Int] = {
         val ans: Array[Int] = Array.ofDim[Int](nums.length)
        
        for ( x <- 0 to (nums.length - 1)) {
            var count = 0
            for(y<-0 to (nums.length-1)){
                if(x != y){
                    if(nums(x)>nums(y)) count+=1
                }
            }
            ans(x) = count
        }
        ans
    }
}


