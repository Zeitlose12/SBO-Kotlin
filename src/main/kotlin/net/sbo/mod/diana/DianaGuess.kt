package net.sbo.mod.diana

/*
    This code would not have been possible without the source code of it from @Bloxigus from the @SkyHanni mod.
    All credit for the core logic goes to them.
*/

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.events.Register
import net.sbo.mod.utils.SboVec
import net.sbo.mod.utils.data.PlayerInteractEvent
import net.sbo.mod.utils.waypoint.WaypointManager
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.World
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Matrix(val data: List<List<Double>>) {
    val rows: Int = data.size
    val cols: Int = if (data.isNotEmpty()) data[0].size else 0

    fun transpose(): Matrix {
        val result = MutableList(this.cols) { MutableList(this.rows) { 0.0 } }
        for (j in 0 until this.cols) {
            for (i in 0 until this.rows) {
                result[j][i] = this.data[i][j]
            }
        }
        return Matrix(result)
    }

    fun multiply(other: Matrix): Matrix {
        if (this.cols != other.rows) {
            throw IllegalArgumentException("Matrix dimensions do not match for multiplication")
        }
        val result = MutableList(this.rows) { MutableList(other.cols) { 0.0 } }
        for (i in 0 until this.rows) {
            for (j in 0 until other.cols) {
                var sum = 0.0
                for (k in 0 until this.cols) {
                    sum += this.data[i][k] * other.data[k][j]
                }
                result[i][j] = sum
            }
        }
        return Matrix(result)
    }

    fun inverse(): Matrix {
        if (this.rows != this.cols) {
            throw IllegalArgumentException("Only square matrices can be inverted")
        }
        val n = this.rows
        val augmented = Array(n) { DoubleArray(2 * n) }

        for (i in 0 until n) {
            for (j in 0 until n) {
                augmented[i][j] = this.data[i][j]
            }
            for (j in 0 until n) {
                augmented[i][j + n] = if (i == j) 1.0 else 0.0
            }
        }

        for (i in 0 until n) {
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(augmented[k][i]) > abs(augmented[maxRow][i])) {
                    maxRow = k
                }
            }

            val temp = augmented[i]
            augmented[i] = augmented[maxRow]
            augmented[maxRow] = temp

            if (abs(augmented[i][i]) < 1e-12) {
                throw IllegalArgumentException("Matrix is singular and cannot be inverted")
            }

            val pivot = augmented[i][i]
            for (j in 0 until 2 * n) {
                augmented[i][j] /= pivot
            }

            for (k in 0 until n) {
                if (k != i) {
                    val factor = augmented[k][i]
                    for (j in 0 until 2 * n) {
                        augmented[k][j] -= factor * augmented[i][j]
                    }
                }
            }
        }

        val inv = MutableList(n) { MutableList(n) { 0.0 } }
        for (i in 0 until n) {
            for (j in 0 until n) {
                inv[i][j] = augmented[i][j + n]
            }
        }
        return Matrix(inv)
    }
}

class PolynomialFitter(private val degree: Int) {
    private val xPointMatrix = mutableListOf<List<Double>>()
    private val yPoints = mutableListOf<List<Double>>()

    fun addPoint(x: Double, y: Double) {
        this.yPoints.add(listOf(y))
        val xArray = MutableList(this.degree + 1) { 0.0 }
        for (i in xArray.indices) {
            xArray[i] = x.pow(i.toDouble())
        }
        this.xPointMatrix.add(xArray)
    }

    fun fit(): List<Double> {
        val xMatrix = Matrix(this.xPointMatrix)
        val yMatrix = Matrix(this.yPoints)

        val coeffsMatrix = xMatrix.transpose().multiply(xMatrix).inverse().multiply(xMatrix.transpose()).multiply(yMatrix)
        val coeffsRow = coeffsMatrix.transpose().data[0]
        return coeffsRow
    }
}

object PreciseGuessBurrowState {
    var finalLocation: SboVec? = null
    var lastGuessTime: Long = 0
}

class PreciseGuessBurrow {
    private var particleLocations = mutableListOf<SboVec>()
    private var guessPoint: SboVec? = null
    private var lastLavaParticle: Long = 0

    fun onWorldChange() {
        this.guessPoint = null
        this.particleLocations.clear()
        PreciseGuessBurrowState.finalLocation = null
    }

    fun onReceiveParticle(packet: ParticleS2CPacket) {
        if (packet.parameters.type != ParticleTypes.DRIPPING_LAVA || packet.count != 2 || packet.speed != -0.5f) return
        val currLoc = SboVec(packet.x, packet.y, packet.z)
        this.lastLavaParticle = System.currentTimeMillis()
        if (System.currentTimeMillis() - PreciseGuessBurrowState.lastGuessTime > 3000) return

        if (this.particleLocations.isEmpty()) {
            this.particleLocations.add(currLoc)
            return
        }

        val distToLast = this.particleLocations.last().distanceTo(currLoc)
        if (distToLast > 3 || distToLast == 0.0) return
        this.particleLocations.add(currLoc)

        val guessPosition = this.guessBurrowLocation()
        if (guessPosition == null) return
        PreciseGuessBurrowState.finalLocation = guessPosition.down(0.5).roundLocationToBlock()
        PreciseGuessBurrowState.finalLocation = guessPosition.down(0.5).roundLocationToBlock();
        WaypointManager.updateGuess(PreciseGuessBurrowState.finalLocation);
    }

    fun guessBurrowLocation(): SboVec? {
        if (this.particleLocations.size < 4) return null
        val fitters = List(3) { PolynomialFitter(3) }

        this.particleLocations.forEachIndexed { index, location ->
            val x = index.toDouble()
            location.toDoubleArray().forEachIndexed { i, value ->
                fitters[i].addPoint(x, value)
            }
        }

        val coefficients = fitters.map { it.fit() }
        val startPointDerivative = SboVec.fromArray(coefficients.map { it[1] })

        val pitch = this.getPitchFromDerivative(startPointDerivative)
        val controlPointDistance = sqrt(24 * sin(pitch - PI) + 25)
        val t = (3 * controlPointDistance) / startPointDerivative.length()
        val result = coefficients.map { coeff ->
            coeff[0] + coeff[1] * t + coeff[2] * t.pow(2) + coeff[3] * t.pow(3)
        }
        return SboVec.fromArray(result)
    }

    private fun getPitchFromDerivative(derivative: SboVec): Double {
        val xzLength = sqrt(derivative.x.pow(2) + derivative.z.pow(2))
        val pitchRadians = -atan2(derivative.y, xzLength)

        var guessPitch = pitchRadians
        var windowMin = -PI / 2
        var windowMax = PI / 2

        repeat(100) {
            val resultPitch = atan2(sin(guessPitch) - 0.75, cos(guessPitch))

            if (resultPitch == pitchRadians) {
                return guessPitch
            }

            if (resultPitch < pitchRadians) {
                windowMin = guessPitch
            } else {
                windowMax = guessPitch
            }
            guessPitch = (windowMin + windowMax) / 2
        }
        return guessPitch
    }

    fun onUseSpade(action: String, event: PlayerInteractEvent?) {
        val player = mc.player
        val item = player?.mainHandStack
        if (item?.isEmpty == true) return
        if (item == null || !item.name.string.contains("Spade") || action != "useItem") return

        if (System.currentTimeMillis() - this.lastLavaParticle < 200) {
            event?.isCanceled = true
            return
        }

        if (System.currentTimeMillis() - PreciseGuessBurrowState.lastGuessTime < 3000) return
        this.particleLocations.clear()
        PreciseGuessBurrowState.lastGuessTime = System.currentTimeMillis()
    }
}

object DianaGuessHandler {
    private val preciseGuess = PreciseGuessBurrow()

    fun init() {
        Register.onPlayerInteract { action, pos, event ->
            if (!Diana.dianaBurrowGuess) return@onPlayerInteract
            preciseGuess.onUseSpade(action, event)
        }
        Register.onWorldChange {
            if (!Diana.dianaBurrowGuess) return@onWorldChange
            preciseGuess.onWorldChange()
        }
        Register.onPacketReceived(ParticleS2CPacket::class.java) { packet ->
            if (!Diana.dianaBurrowGuess || World.getWorld() != "Hub") return@onPacketReceived
            preciseGuess.onReceiveParticle(packet)
        }
    }
}