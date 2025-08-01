package de

/**
 * Implementierung des Euklidischen Abstands
 * @param gewichtX Gewichtung des Merkmals der x-Achse
 * @param gewichtY Gewichtung des Merkmals der y-Achse
 */
class Euklid(gewichtX : Double, gewichtY : Double) extends Metrik {


    override def abs(a: Point2D, b: Point2D): Double = {
      Math.sqrt(gewichtX*Math.pow(a.getX() - b.getX(), 2) + gewichtY*Math.pow(a.getY() - b.getY(), 2))
    }


}
