package network.cow.dgen.visual

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JComponent

/**
 * @author Tobias Büser
 */
class SpriteVisualizer(private val image: BufferedImage) : JComponent() {

    init {
        this.preferredSize = Dimension(128, 128)
    }

    override fun paintComponent(g: Graphics) {
        val graphics = g as Graphics2D

        graphics.scale(7.0, 7.0)
        graphics.drawImage(image, 0, 0, image.width, image.height, null)
        graphics.dispose()
    }

}
