package ik.viz

import java.awt.Font

import scala.collection.mutable.ListBuffer
import scala.math.max
import scala.math.min

import processing.core.PConstants
import processing.core.PFont

abstract class book(val dpth: Int = 10) {
  val bids = (0 until dpth).toArray map (_ => (0f, 0, 0))
  val asks = (0 until dpth).toArray map (_ => (0f, 0, 0))

  val bid_ordrs = (0 until dpth).toArray map (_ => ListBuffer[Option[Int]]())
  val ask_ordrs = (0 until dpth).toArray map (_ => ListBuffer[Option[Int]]())

  private def insrt_lvl(lvl: Int, sde: Array[(Float, Int, Int)], ordrs: Array[ListBuffer[Option[Int]]]) = {
    (lvl until (dpth - 1)).reverse foreach { x =>
      sde(x + 1) = sde(x)
      ordrs(x + 1) = ordrs(x)
    }
    sde(lvl) = (0f, 0, 0)
    ordrs(lvl) = ListBuffer[Option[Int]]()
  }

  private def rm_lvl(lvl: Int, sde: Array[(Float, Int, Int)], ordrs: Array[ListBuffer[Option[Int]]]) = {
    (lvl until (dpth - 1)) foreach { x =>
      sde(x) = sde(x + 1)
      ordrs(x) = ordrs(x + 1)
    }
    sde(dpth - 1) = (0f, 0, 0)
    ordrs(dpth - 1) = ListBuffer[Option[Int]]()
  }

  def new_lvl(bid: Boolean, lvl: Int) =
    if (bid) insrt_lvl(lvl - 1, bids, bid_ordrs)
    else insrt_lvl(lvl - 1, asks, ask_ordrs)

  def del_lvl(bid: Boolean, lvl: Int) =
    if (bid) rm_lvl(lvl - 1, bids, bid_ordrs)
    else rm_lvl(lvl - 1, asks, ask_ordrs)

}

class cme_book(override val dpth: Int = 10) extends book(dpth) {

}

class book_vw(sktch: p_sktch, bk: book) extends p_area(sktch) {
  val hdr_hght = () => 100f
  val ftr_hght = () => 30f
  val mid_gap = () => 50f
  val lvl_gap = () => 20f
  val bk_wdth = () => 160f

  private def dsply_ordrs(ordrs: ListBuffer[Option[Int]], grd: List[(Float, Float, Float, Float)]) = {
    (0 until ordrs.size) foreach { x =>
      if (x < grd.size) {
        box_frm(grd(x))
        ordrs(x) match {
          case Some(i) => box_txt(i.toString, grd(x))
          case None => box_txt("?", grd(x))
        }
      }
    }
  }

  override def draw_impl = if (visible || D) {
    val bx = box
    val x_mid = box_xmid(bx)
    val y_hdr = min(bx._2 + hdr_hght(), bx._4)
    val y_ftr = max(bx._4 - ftr_hght(), bx._2)
    if (visible) {
      // left/right background
      p.fill(0xff, 0xe0, 0xe0, 0xff)
      p.stroke(0xff, 0xe0, 0xe0, 0xff)
      box_fll(l_hlf())
      p.fill(0xe0, 0xe0, 0xff, 0xff)
      p.stroke(0xe0, 0xe0, 0xff, 0xff)
      box_fll(r_hlf())

      // draw the boxes for each side of the stack
      // left
      val bid_stck = box_stack(r_strip(trim(l_hlf(), t = hdr_hght(), b = ftr_hght(), r = mid_gap()), bk_wdth()), bk.dpth, lvl_gap())
      val ask_stck = box_stack(l_strip(trim(r_hlf(), t = hdr_hght(), b = ftr_hght(), l = mid_gap()), bk_wdth()), bk.dpth, lvl_gap())

      val b_ord_stck = box_stack(trim(l_hlf(), l = 20, t = hdr_hght(), b = ftr_hght(), r = bk_wdth() + mid_gap() + 20), bk.dpth, lvl_gap())
      val a_ord_stck = box_stack(trim(r_hlf(), r = 20, t = hdr_hght(), b = ftr_hght(), l = bk_wdth() + mid_gap() + 20), bk.dpth, lvl_gap())

      // fill each side of the stack with information
      lazy val slot_hght = box_hght(bid_stck(0)).toInt
      lazy val slot_wdth = box_wdth(bid_stck(0)).toInt
      lazy val ordr_wdth = box_wdth(b_ord_stck(0)).toInt

      lazy val n_ord_lns = slot_hght / 25
      lazy val n_ord_cols = ordr_wdth / 40

      (0 until bk.dpth) foreach { tck_lvl =>
        // --- bid  side ---
        //draw the bid box
        p.textAlign(PConstants.RIGHT)
        p.textFont(new PFont(new Font("Arial", Font.PLAIN, min(max(15, slot_hght - 30), 50)), false))
        val bd_lvl_bx = bid_stck(tck_lvl)
        p.fill(0xcc, 0xaa, 0xaa, 0xaa)
        p.text((tck_lvl + 1).toString, bd_lvl_bx._1 + 9 * slot_wdth / 10, bd_lvl_bx._2 + slot_hght - 10)
        p.fill(0xbb, 0x33, 0x11, 0x66)
        p.stroke(0x55, 0x22, 0x11)
        box_fll(bd_lvl_bx)
        // fill out bid info
        p.fill(0x55, 0x22, 0x11)
        p.textAlign(PConstants.LEFT)
        p.textFont(new PFont(new Font("Courier", Font.BOLD, 20), false))
        p.text(bk.bids(tck_lvl)._1.toString, bd_lvl_bx._1 + 10, bd_lvl_bx._2 + 25)
        p.text(bk.bids(tck_lvl)._2.toString, bd_lvl_bx._1 + 10, bd_lvl_bx._2 + 45)
        p.text(bk.bids(tck_lvl)._3.toString, bd_lvl_bx._1 + 10, bd_lvl_bx._2 + 65)
        // bid orders for level
        p.textAlign(PConstants.CENTER)
        p.textFont(new PFont(new Font("Courier", Font.PLAIN, 15), false))
        p.stroke(0x55, 0x22, 0x11, 0x44)
        dsply_ordrs(bk.bid_ordrs(tck_lvl), box_grid(b_ord_stck(tck_lvl), n_ord_lns, n_ord_cols, 3) flatMap (x => x.reverse))

        // --- ask side ---
        // draw the ask box
        p.textAlign(PConstants.RIGHT)
        p.textFont(new PFont(new Font("Arial", Font.PLAIN, min(max(15, slot_hght - 30), 50)), false))
        val ask_lvl_bx = ask_stck(tck_lvl)
        p.fill(0xaa, 0xaa, 0xcc, 0xaa)
        p.text((tck_lvl + 1).toString, ask_lvl_bx._1 + 9 * slot_wdth / 10, ask_lvl_bx._2 + slot_hght - 10)
        p.fill(0x66, 0x88, 0xbb, 0x66)
        p.stroke(0x11, 0x22, 0x55)
        box_fll(ask_lvl_bx)
        // fill out ask info
        p.fill(0x11, 0x22, 0x55)
        p.textFont(new PFont(new Font("Courier", Font.BOLD, 20), false))
        p.textAlign(PConstants.LEFT)
        p.text(bk.asks(tck_lvl)._1.toString, ask_lvl_bx._1 + 10, ask_lvl_bx._2 + 25)
        p.text(bk.asks(tck_lvl)._2.toString, ask_lvl_bx._1 + 10, ask_lvl_bx._2 + 45)
        p.text(bk.asks(tck_lvl)._3.toString, ask_lvl_bx._1 + 10, ask_lvl_bx._2 + 65)
        // ask orders for level
        p.textAlign(PConstants.CENTER)
        p.textFont(new PFont(new Font("Courier", Font.PLAIN, 15), false))
        p.stroke(0x11, 0x22, 0x55, 0x44)
        dsply_ordrs(bk.ask_ordrs(tck_lvl), box_grid(a_ord_stck(tck_lvl), n_ord_lns, n_ord_cols, 5) flatMap (x => x))
      }
    }

    if (D) {
      p.textAlign(PConstants.LEFT)
      p.stroke(0x66, 0x33, 0x44)
      p.fill(0x66, 0x33, 0x44)
      p.textFont(new PFont(new Font("Arial", Font.PLAIN, 10), false))
      p.text("book view in debug mode", bx._1 + 10, hdr_hght() + 30)
      p.stroke(0xbb, 0x33, 0x44)
      box_frm
      p.line(x_mid, bx._2, x_mid, bx._4)
      p.line(bx._1, y_hdr, bx._3, y_hdr)
      p.line(bx._1, y_ftr, bx._3, y_ftr)
    }
  }
}
