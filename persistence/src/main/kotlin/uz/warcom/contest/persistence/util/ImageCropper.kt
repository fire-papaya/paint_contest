package uz.warcom.contest.persistence.util

import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.min

object ImageCropper {
    fun cropSquare(original: BufferedImage): BufferedImage {
        if (original.height == original.width)
            return original

        val squareSide = min(original.height, original.width)

        val (cropStartX, cropStartY) = if (original.height > original.width) {
            Pair(0, (original.height/2 - squareSide/2).toInt())
        } else {
            Pair((original.width/2 - squareSide/2).toInt(), 0)
        }

        val rect = Rectangle(squareSide, squareSide)
        val cropped = BufferedImage(rect.width, rect.height, original.type)

        val g = cropped.graphics
        g.drawImage(
            original,
            0,
            0,
            rect.width,
            rect.height,
            cropStartX,
            cropStartY,
            cropStartX + squareSide,
            cropStartY + squareSide,
            null
        )
        g.dispose()

        return cropped
    }
}