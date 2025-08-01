package de

/**
 * Trait zur Repräsentation der Metrik
 */
trait Metrik {

  def abs(a : Point2D, b : Point2D): Double

}
