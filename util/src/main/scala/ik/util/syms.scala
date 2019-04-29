package ik.util

/**
 * helper implicits for conversion of strings into symbols
 */
object syms {
  implicit def sym2str(s: Symbol): String = s.name
  implicit def str2sym(s: String): Symbol = Symbol(s)
  implicit def str2sym(sa: Array[String]): Array[Symbol] = sa map (Symbol(_))
  implicit def sym2str(sa: Array[Symbol]): Array[String] = sa map (_.name)
}