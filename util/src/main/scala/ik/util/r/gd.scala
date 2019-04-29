package ik.util.r

import java.awt.Component
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.awt.image.BufferedImage

import scala.collection.mutable.ListBuffer

import org.rosuda.javaGD.GDInterface
import org.rosuda.javaGD.JGDBufferedPanel

import ik.util.img.cmp2img
import ik.util.img.img2clp
import ik.util.log.loggd
import javax.swing.JFrame

trait plt_lstnr {
  def updt(img: BufferedImage)
}

object plt_lstr {
  private val lst = new ListBuffer[plt_lstnr]
  def +=(l: plt_lstnr) = lst += l
  def -=(l: plt_lstnr) = lst -= l
  def nu(i: BufferedImage) = lst foreach (_.updt(i))
}

/**
 * wrapper which sets up
 */
object gd {
  // a default title
  var ttl: String = ""

  // initialise the graphics device and set it up as the r plotting device
  def apply(): Unit = apply("")

  // initialise without title
  def apply(t: String = "r plot device") = {
    ttl = t
    r(".setenv<-if(exists(\"Sys.setenv\")) Sys.setenv else Sys.putenv")
    r(".setenv(\"JAVAGD_CLASS_NAME\"=\"ik/util/r/gd\")")
    r("library(JavaGD)")
    r("library(ggplot2)")
    r("library(grid)")
    r("options(device=\"JavaGD\")")
  }

  /**
   * turn off the graphics device
   */
  def off = r("if(length(dev.list()) >= 1) dev.off()")
}

/**
 * custom java graphics device for R plots
 */
class gd extends GDInterface with wnd_lstnr with loggd {
  var f: JFrame = null

  /**
   * the gd open override event
   */
  override def gdOpen(w: Double, h: Double) = {
    if (f != null) gdClose()
    f = new JFrame(gd.ttl)
    f.addWindowListener(this)
    c = new JGDBufferedPanel(w, h)
    f.getContentPane.add(c.asInstanceOf[Component])
    f.pack
    //f.setSize(new Dimension(w.toInt, h.toInt))
    //f.add(new JLabel("blah"))

    f.setVisible(true)
    f.addKeyListener(new KeyListener() {
      override def keyPressed(e: KeyEvent): Unit = {}
      override def keyReleased(e: KeyEvent): Unit = {}
      override def keyTyped(e: KeyEvent): Unit = {
        if (e.getModifiers == 2 && e.getKeyChar.toInt == 3) {
          val i = cmp2img(c.asInstanceOf[Component], f)
          img2clp(i)
        } else if (e.getModifiers == 2 && e.getKeyChar.toInt == 18) log_dbg("<CTRL>+<R>")
      }
    })
  }

  /**
   * gd close override
   */
  override def gdClose: Unit = {
    super.gdClose
    log_dbg("gdClose")
    if (f != null) {
      c = null
      f.removeAll
      f.dispose
      f = null
    }
  }

  /**
   * gd mode override
   */
  //  override def gdMode(m: Int) = { super.gdMode(m); log_dbg("gdMode: " + m) }
  //  override def gdActivate = { super.gdActivate; log_dbg("gdActivate") }
  //  override def gdDeactivate = { super.gdDeactivate; log_dbg("gdDeactivate") }
  //  override def gdNewPage(i: Int) = { super.gdNewPage(i); log_dbg("gdNewPage: " + i) }
}

trait wnd_lstnr extends WindowListener {
  self: gd =>

  def windowActivated(e: WindowEvent): Unit = {}
  def windowClosed(e: WindowEvent): Unit = {}
  def windowClosing(e: WindowEvent): Unit = {}
  def windowDeactivated(e: WindowEvent): Unit = {}
  def windowDeiconified(e: WindowEvent): Unit = {}
  def windowIconified(e: WindowEvent): Unit = {}
  def windowOpened(e: WindowEvent): Unit = {}

}