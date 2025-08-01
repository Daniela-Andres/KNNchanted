package de

/**
 * Hilfsklasse für allgemeine Punkte, die keine Datenpunkte des Datensatzes darstellen
 * @param x x-Koordinate
 * @param y y-Koordinate
 */
class Point2D(x : Double, y :Double){


  def getX(): Double = {
    x
  }


  def getY(): Double = {
    y
  }


  def winkel(zentrum : Point2D): Double = {
    Math.atan2(x - zentrum.getX(), y - zentrum.getY())
  }

  override def toString: String = {
    return "( " + x + " | "+ y  + ")"
  }


  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[Datapoint]) {
      var p: Datapoint = obj.asInstanceOf[Datapoint]

      if (x == p.getX() && y == p.getY()) {
        return true
      } else {
        return false
      }
    } else {
      return false
    }
  }
}
