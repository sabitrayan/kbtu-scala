object Solution {
    def subarraySum(nums: Array[Int], k: Int): Int = {
        var cnt = 0
        for (l <- 0 to nums.length - 1) {
            var sum = 0
            for (r <- l to nums.length - 1) {
                sum += nums(r)
                if (sum == k) cnt += 1
            }
        }
        cnt
    }
}