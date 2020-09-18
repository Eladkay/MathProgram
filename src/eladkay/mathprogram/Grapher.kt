package eladkay.mathprogram

import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.*


// Based on https://github.com/Eladkay/EllipticCurve/blob/master/src/eladkay/ellipticcurve/simulationengine/EllipticSimulator.kt
object Grapher {

    fun evaluateBase64(text: String, size: Pair<Int, Int>): String {
        val difference: (Double, Double)-> Double = {
            x, y -> y-ExpressionUtils.evaluate(text, x)
        }
        val list = drawCurveToList(difference, size, 0.0001)
        return imageToBase64(createImageFromList(size, 1, list))
    }

    private fun imageToBase64(buf: BufferedImage): String {
        val output = ByteArrayOutputStream()
        ImageIO.write(buf, "GIF", output)
        return Base64.getEncoder().encodeToString(output.toByteArray())
    }

    @Suppress("SameParameterValue")
    private fun createImageFromList(frameSize: Pair<Int, Int>, pointSize: Int, list: List<Pair<Int, Int>>): BufferedImage {
        val ret = BufferedImage(frameSize.first, frameSize.second, TYPE_INT_RGB)
        val graphics = ret.graphics
        for(point in list) graphics.drawOval(point.first, point.second, pointSize, pointSize)
        return ret
    }

    private fun drawCurveToList(difference: (Double, Double) -> Double, frameSize: Pair<Int, Int>, errorTerm: Double, amountChecks: Int = 4): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        for (x in 0..frameSize.first)
            for (y in 0..frameSize.second) {
                val xModified = modifyX(x, frameSize)
                val yModified = modifyY(y, frameSize)

                var condition = difference(xModified, yModified) == 0.0
                condition = condition || diskCheck(amountChecks, errorTerm, xModified to yModified, difference)
                if (condition) {
                    ret.add(x to y)
                }
            }
        return ret
    }

    private fun diskCheck(amount: Int, radius: Double, point: Pair<Double, Double>, difference: (Double, Double) -> Double): Boolean {
        // We divide the circle of radius [radius] around the point [point] into [amount] equal arcs:
        val testPoints = mutableListOf<Pair<Double, Double>>()
        val theta = 2 * PI / amount
        for(i in 0..amount) {
            testPoints.add(Pair(point.first + radius * cos(i * theta), point.second + radius * sin(i * theta)))
        }
        // Check if all points are on the same side of the curve whose difference function is [difference].
        return abs(testPoints.map { difference(it.first, it.second).sign }.sum()) != amount.toDouble()
    }

    private fun modifyY(y: Int, frameSize: Pair<Int, Int>): Double {
        return (-y + frameSize.second / 2) / 5.0
    }

    private fun modifyX(x: Int, frameSize: Pair<Int, Int>): Double {
        return (x - frameSize.first / 2) / 80.0
    }
}