package de

/**
 * Klasse zur Modellierung eines Datenpunktes (Egal ob Trainings- oder Testdatenpunkt)
 * @param x Merkmal auf der x-Achse
 * @param y Merkmal auf der y-Achse
 * @param label (Erwartetes) Label in Textform
 */

class Datapoint(var x : Double, var y : Double, var label: String) extends Point2D(x,y) {


  def setX(x : Double): Unit ={
    this.x = x
  }

  def setY(y: Double): Unit = {
    this.y = y
  }


  def setLabel(l : String): Unit ={
    label = l
  }

  def getLabel(): String = {
    return label
  }


  override def toString: String = {
    "X: " + x + ", Y: " + y + " , Label " + label
  }


}
