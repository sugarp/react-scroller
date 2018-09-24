package com.ps.kotlinjs.basekit.utils

class BinarySearch {
    companion object {
        fun nearestValue(sorted: List<Int>, value: Int) : Int {
            var start = 0
            var end = sorted.count()

            if (value < sorted[0]) {
                return 0
            }
            if (value > sorted[end - 1]) {
                return end - 1
            }
            while (start + 1 < end) {
                var mid = (start + end).shr(1)
                var v = sorted[mid]

                if (v == value) {
                    return mid;
                } else if (v < value) {
                    start = mid;
                } else {
                    end = mid;
                }
            }

            return start;
        }
    }
}