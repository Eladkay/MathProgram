package eladkay.mathprogram

import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.sign


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

    fun drawCurveToList(difference: (Double, Double) -> Double, frameSize: Pair<Int, Int>, errorTerm: Double): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        for (x in 0..frameSize.first)
            for (y in 0..frameSize.second) {
                val xModified = modifyX(x, frameSize)
                val yModified = modifyY(y, frameSize)
                if (yModified < 0) continue // elliptic curves are always symmetric

                var condition = difference(xModified, yModified) == 0.0
                val s1 = difference(xModified + errorTerm, yModified + errorTerm).sign
                val s2 = difference(xModified + errorTerm, yModified - errorTerm).sign
                val s3 = difference(xModified - errorTerm, yModified + errorTerm).sign
                val s4 = difference(xModified - errorTerm, yModified - errorTerm).sign
                if (!condition && Math.abs(s1 + s2 + s3 + s4) != 4.0) // if they're not all the same sign
                    condition = true
                if (condition) {
                    ret.add(x to y)
                    ret.add(x to demodifyY(-yModified, frameSize))
                }
            }
        return ret
    }

    private fun demodifyY(y: Double, frameSize: Pair<Int, Int>): Int {
        return (-15.0 * y + frameSize.second / 2).toInt()
    }

    private fun demodifyX(x: Double, frameSize: Pair<Int, Int>): Int {
        return (x * 200.0 + frameSize.second / 2).toInt()
    }

    private fun modifyY(y: Int, frameSize: Pair<Int, Int>): Double {
        return (-y + frameSize.second / 2) / 15.0
    }

    private fun modifyX(x: Int, frameSize: Pair<Int, Int>): Double {
        return (x - frameSize.first / 2) / 200.0
    }
}