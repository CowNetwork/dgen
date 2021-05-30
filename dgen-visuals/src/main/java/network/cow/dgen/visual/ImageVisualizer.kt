package network.cow.dgen.visual

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JComponent

/**
 * @author Tobias Büser
 */
class ImageVisualizer(private val image: BufferedImage) : JComponent() {

    init {
        this.preferredSize = Dimension(image.width, image.height)
    }

    override fun paintComponent(g: Graphics) {
        val graphics = g as Graphics2D

        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()
    }

}
