object Solution {
    def repeatedNTimes(A: Array[Int]):Int = {
        val res = A.map(x => A.count(y => x == y))
        for((value,index) <- res.zipWithIndex){
            if(value == A.length / 2) return A(index)
        }
        -1
    }
}

