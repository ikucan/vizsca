
// import scala.concurrent.{ Future, Await }
// import scala.concurrent.ExecutionContext.Implicits.global
// import scala.concurrent.duration._
// import scala.math._

// class hy_crrel {

//   /**
//    * calcluate the hayashi-yoshida correlation for two asynco random variables applying a lag to the second (Y)
//    * the lag value is subtracted from each of the times in y
//    */

//   def lggd(X: Array[Double], t: Array[Long], Y: Array[Double], s: Array[Long], h: Long, n: Int): Array[Double] = lggd(X, t, Y, s, (-n to n).toArray map (_ * h))

//   def lggd(X: Array[Double], t: Array[Double], Y: Array[Double], s: Array[Double], lags: Array[Double]): Array[Double] = lggd(X, t map (_.toLong), Y, s map (_.toLong), lags map (_.toLong))

//   def lggd(X: Array[Double], t: Array[Long], Y: Array[Double], s: Array[Long], lags: Array[Long]): Array[Double] = {
//     chck(X, t, Y, s)
//     val (rX, rY) = (log_rtrn(X), log_rtrn(Y))
//     lags map { lag => Future { raw(rX, t, rY, s map (_ + lag)) } } map (Await.result(_, 1 hour))
//   }

//   def smpl(X: Array[Double], t: Array[Long], Y: Array[Double], s: Array[Long]): Double = raw(log_rtrn(X), t, log_rtrn(Y), s)

//   private def raw(rX: Array[Double], t: Array[Long], rY: Array[Double], s: Array[Long]): Double = {
//     // first range ends before second starts
//     def y_b4_x(i: Int, j: Int): Boolean = s(j) <= t(i - 1)
//     // ranges overlap
//     def x_ovrlp_y(i: Int, j: Int): Boolean = {
//       val (tI0, tI1, tJ0, tJ1) = (t(i - 1), t(i), s(j - 1), s(j))
//       (tJ0 < tI0 && tJ1 > tI0) || (tJ0 < tI1 && tJ1 > tI1) || (tJ0 >= tI0 && tJ1 <= tI1) || (tJ0 <= tI0 && tJ1 >= tI1)
//     }

//     // summary variables
//     var (sum_xx, sum_yy, sum_xy) = (0.0, 0.0, 0.0)

//     var (i, j) = (1, 1)
//     // pivot on X and drag Y with it
//     while (i < rX.length) {
//       sum_xx += rX(i) * rX(i)

//       if (j > 1 && !y_b4_x(i, j - 1)) sum_xy += rX(i) * rY(j - 1)
//       else
//         // make j catch up with i within bounds of Y
//         while (j < rY.length && y_b4_x(i, j)) {
//           sum_yy += rY(j) * rY(j)
//           j += 1
//         }

//       // compute on the overlap within bounds of Y
//       while (j < rY.length && x_ovrlp_y(i, j)) {
//         sum_yy += rY(j) * rY(j)
//         sum_xy += rX(i) * rY(j)
//         j += 1
//       }

//       i += 1
//     }
//     // X is at end, but drag Y to the end also
//     while (j < rY.length) {
//       sum_yy += rY(j) * rY(j)
//       j += 1
//     }
//     // calc
//     sum_xy / sqrt(sum_xx * sum_yy)
//   }

//   /**
//    * util: calculate log returns
//    */
//   private def log_rtrn(X: Array[Double]) = {
//     val rX = new Array[Double](X.length)
//     (2 until X.length) foreach (i => rX(i) = log(X(i) / X(i - 1)))
//     //(2 until X.length) foreach (i => rX(i) = X(i) - X(i - 1))
//     rX
//   }
//   /**
//    * check inputs for symetry
//    */
//   private def chck(X: Array[Double], t: Array[Long], Y: Array[Double], s: Array[Long]) = {
//     if (!(X.length == t.length)) throw stat_err("X and its time index should be symetric. you provided " + X.length + " X values and " + t.length + " t values")
//     if (!(Y.length == s.length)) throw stat_err("Y and its time index should be symetric. you provided " + Y.length + " Y values and " + s.length + " s values")
//     if (X.length < 2) throw stat_err("X must provide at least one interval")
//     if (Y.length < 2) throw stat_err("Y must provide at least one interval")

//     if (!mntnc(t)) throw stat_err("both time indices need to be monotonically increasing. t failed")
//     if (!mntnc(s)) throw stat_err("both time indices need to be monotonically increasing. s failed")
//   }

//   /**
//    * make sure values are monotonically increasing
//    */
//   def mntnc(x: List[Array[Long]]): Boolean = ((true) /: x)(_ && mntnc(_))

//   def mntnc(x: Array[Long]): Boolean = {
//     var i = 1
//     while (i < x.length) {
//       if (x(i) < x(i - 1)) {
//         println("error at index: " + i + ". prev value was " + x(i - 1) + " current value is " + x(i))
//         return false
//       }
//       i = i + 1
//     }
//     return true
//   }
// }

// object hy_crrel {
//   def apply() = new hy_crrel
// }
