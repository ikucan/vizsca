import scala.math.exp

import scala.math.min
import ik.util.r_ex
import ik.util.vctr.mk_vctr_wrppr

package ik.analytics {

  object ewma {

    /**
     * async ewma
     * vector method, computes from inputs
     */
    def async(t: Array[Long], v: Array[Double], hl: Double, n_init: Int = 30) = {
      val N_i = min(t.size, n_init)
      if (t.size != v.size) throw new r_ex("asymetric time and value vectors.")
      if (t.size > 0) {
        val ma = new Array[Double](t.size)
        ma(0) = v.slice(0, N_i) filter (!_.isNaN) mean
        var (i, v_acc, t_acc) = (1, ma(0), 1.0)
        while (i < t.size) {
          if (v(i).isNaN) ma(i) = ma(i - 1)
          else {
            val dt = (t(i) - t(i - 1)).toDouble
            val dcy = exp(-dt / hl)
            v_acc = dt * v(i) + dcy * v_acc
            t_acc = dt + dcy * t_acc
            ma(i) = v_acc / t_acc
          }
          i += 1
        }
        ma
      } else Array[Double]()
    }

    /**
     * class for incremental management
     */
    class async(val hl: Double, val t0: Long = 0, val v0: Double = Double.NaN) {
      private var (t, ma) = (t0, v0)
      private var (t_acc, v_acc) = (1.0, v0)

      /**
       * [re]initialise the ewma with a time and value
       */
      def init(t_init: Long, v_init: Double) = {
        t_acc = 1.0;
        v_acc = v_init;
        t = t_init;
        ma = v_init
        ma
      }

      /**
       * increment the ewma with a time and value
       */
      def +=(t_new: Long, v_new: Double) = {
        assert(t_new >= t, "times must be monotonically increasing")
        assert(!v_new.isNaN, "adding a NaN value to the ma")

        if (ma.isNaN) init(t_new, v_new)
        else {
          val dt = (t_new - t).toDouble
          val dcy = exp(-dt / hl)
          v_acc = dt * v_new + dcy * v_acc
          t_acc = dt + dcy * t_acc
          ma = v_acc / t_acc
          t = t_new
          ma
        }
      }

      /**
       * just get the moving average out
       */
      def apply() = ma
    }

    /**
     * a syncrhonous ewma
     */
    def sync(v: Array[Double], lmbd: Double, n_init: Int = 30) = {
      val N_i = min(v.size, n_init)
      if (v.size > 0) {
        val ma = new Array[Double](v.size)
        ma(0) = v.slice(0, N_i) filter (!_.isNaN) mean;
        for (i <- 1 until v.size) {
          ma(i) =
            if (v(i).isNaN) ma(i - 1)
            else ma(i - 1) * (1 - lmbd) + v(i) * lmbd
        }
        ma
      } else Array[Double]()
    }
  }

  /**
   * test equivalence of the two async ewma's
   */
//  object tst {
//    def main(argv: Array[String]) = {
//      val t = Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9) map (_.toLong)
//      val v = Array(3, 2, 4, 2, 4, 5, 6, 7, 8, 8) map (_.toDouble)
//
//      val ma1 = ewma.async(t, v, 5, 1)
//
//      val ma = new ewma.async(5, t(0), v(0))
//      val ma2 = List(ma()) ++ ((1 until t.size) map { i => ma += (t(i), v(i)) })
//
//      for (i <- 0 until t.size) {
//        println(i + ":: " + v(i) + ", ma1: " + ma1(i) + ", ma2: " + ma2(i) + ". diff: " + math.abs(ma1(i) - ma2(i)))
//      }
//
//    }
//  }

}