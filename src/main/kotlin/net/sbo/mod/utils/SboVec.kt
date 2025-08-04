package net.sbo.mod.utils

import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.pow

data class SboVec(val x: Double, val y: Double, val z: Double) {

    fun distanceTo(other: SboVec): Double {
        return sqrt((other.x - this.x).pow(2) + (other.y - this.y).pow(2) + (other.z - this.z).pow(2))
    }

    operator fun plus(other: SboVec): SboVec {
        return SboVec(this.x + other.x, this.y + other.y, this.z + other.z)
    }

    operator fun minus(other: SboVec): SboVec {
        return SboVec(this.x - other.x, this.y - other.y, this.z - other.z)
    }

    operator fun times(d: Double): SboVec {
        return SboVec(this.x * d, this.y * d, this.z * d)
    }

    fun clone(): SboVec = this.copy()

    fun down(amount: Double): SboVec {
        return this.copy(y = this.y - amount)
    }

    fun roundLocationToBlock(): SboVec {
        val roundedX = round(this.x - 0.499999)
        val roundedY = round(this.y - 0.499999)
        val roundedZ = round(this.z - 0.499999)
        return SboVec(roundedX, roundedY, roundedZ)
    }

    fun toCleanString(): String {
        return "%.2f, %.2f, %.2f".format(this.x, this.y, this.z)
    }

    fun toDoubleArray(): DoubleArray {
        return doubleArrayOf(this.x, this.y, this.z)
    }

    fun length(): Double {
        return sqrt(x * x + y * y + z * z)
    }

    companion object {
        fun fromArray(arr: DoubleArray): SboVec {
            require(arr.size >= 3) { "Array must contain at least 3 elements for x, y, z." }
            return SboVec(arr[0], arr[1], arr[2])
        }
    }
}