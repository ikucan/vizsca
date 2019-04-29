package ik.util

import java.awt.datatransfer.Transferable
import java.awt.Image
import java.awt.Component
import javax.swing.RepaintManager
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import javax.swing.JFrame
import java.awt.Toolkit
import javax.swing.SwingUtilities
import java.awt.Container
import javax.swing.JFileChooser

/**
 *
 */
package object img {

  /**
   * pop up file choser and write an image to a file
   */
  def sv(i: BufferedImage, prnt: Component) = {
    val fc = new JFileChooser
    if (fc.showSaveDialog(prnt) == JFileChooser.APPROVE_OPTION) img2fle(i, fc.getSelectedFile.getCanonicalPath)
  }

  /**
   * write an image to a file
   */
  def img2fle(i: BufferedImage, f: String) = ImageIO.write(i, "png", new File(f))

  /**
   * write an image of a component into a file
   */
  def frm2fle(fr: JFrame, f: String) = ImageIO.write(frm2img(fr), "png", new File(f))

  /**
   * rasterise a component into a buffered image
   */
  def frm2img(jf: JFrame) = {
    val img = new BufferedImage(jf.getWidth, jf.getHeight, BufferedImage.TYPE_INT_ARGB_PRE)
    val g = img.getGraphics
    SwingUtilities.paintComponent(g, jf.getContentPane.getComponent(0), jf, 0, 0, jf.getWidth, jf.getHeight)
    img
  }
  def cmp2img(c: Component, p: Container) = {
    val img = new BufferedImage(c.getWidth, c.getHeight, BufferedImage.TYPE_INT_ARGB_PRE)
    val g = img.getGraphics
    SwingUtilities.paintComponent(g, c, p, 0, 0, c.getWidth, c.getHeight)
    img
  }

  def img2clp(i: BufferedImage) = Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new img_slctn(i), null)

  def frm2clp(f: JFrame) = Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new img_slctn(frm2img(f)), null)

  /**
   * disable/enable double buffering
   */
  def dsbl_dbl(c: Component) = RepaintManager.currentManager(c).setDoubleBufferingEnabled(false)
  def enbl_dbl(c: Component) = RepaintManager.currentManager(c).setDoubleBufferingEnabled(true)

  /**
   *
   */
  private class img_slctn(img: Image) extends Transferable {
    override def getTransferDataFlavors: Array[DataFlavor] = Array(DataFlavor.imageFlavor)
    override def isDataFlavorSupported(f: DataFlavor) = DataFlavor.imageFlavor.equals(f)
    override def getTransferData(f: DataFlavor) =
      if (!isDataFlavorSupported(f)) throw new r_ex("data format not supported")
      else img
  }

  //  def main(argv: Array[String]) = {
  //    import javax.swing._
  //    import java.awt.Dimension
  //    println(123)
  //
  //    var ff: JFrame = null
  //
  //    ff = new JFrame("r plot")
  //    val pnl = new JPanel
  //    ff.add(pnl)
  //    pnl.add(new JLabel("blah"))
  //    pnl.add(new JLabel("BLAH"))
  //    pnl.add(new JLabel("blah"))
  //    ff.setSize(new Dimension(300, 150))
  //    ff.setVisible(true)
  //    ff.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  //
  //    println(234)
  //
  //    Thread.sleep(1000)
  //    //img.frm2clp(ff)
  //    img.frm2fle(ff, "/home/iztok/p.png")
  //    println(456)
  //    //ff.dispose
  //  }

}
