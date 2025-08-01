package de

/**
 * Implementierung der Manhattan-Distanz
 * @param gewichtX Gewichtung des Merkmals der x-Achse
 * @param gewichtY Gewichtung des Merkmals der y-Achse
 */
class Manhattan(gewichtX : Double, gewichtY : Double) extends Metrik {

  /**
   * Berechnet die Manhattan-Distanz zwischen zwei Punkten.
   */
  override def abs(a: Point2D, b: Point2D): Double = {
    gewichtX * Math.abs(a.getX() - b.getX()) + gewichtY * Math.abs(a.getY() - b.getY())

  }
}