object Solution {
    def kWeakestRows(mat: Array[Array[Int]], k: Int): Array[Int] = {
      
      val ans = new Array[Int](mat.size)
      
      var x = 0
      
      while (x < mat.size) {
        if (mat(x)(ans(x)) == 1) ans(x) += 1
        if (ans(x) == mat(0).size || mat(x)(ans(x)) == 0) x += 1
      }
      
      ans.zipWithIndex.sortBy(c => c._1).take(k).unzip._2
    }
}