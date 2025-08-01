package de

import de.MainApp.appendPar

import java.util
import java.util.ArrayList

/**
 * Speichert einen Datensatz
 * Ein Datensatz besteht immer aus einem Trainingsdatensatz und einem Testdatensatz
 */


class Datensatz{

  var xAchseName = "X"
  var yAchseName = "Y"
  var labelBegriff = "" //z.B. T-Shirt Groesse

  var labelList: util.ArrayList[String] = new util.ArrayList[String]()

  var trainingsdaten: util.ArrayList[Datapoint] = new util.ArrayList[Datapoint]()
  var testdaten: util.ArrayList[Datapoint] = new util.ArrayList[Datapoint]()

  def this(trainingsdaten : util.ArrayList[Datapoint], testdaten : util.ArrayList[Datapoint]) = {
    this()
    this.trainingsdaten = trainingsdaten
    this.testdaten = testdaten
  }

  def this(xAchse : String, yAchse : String, labelBegriff : String, labelList : util.ArrayList[String], trainingsdaten : util.ArrayList[Datapoint], testdaten : util.ArrayList[Datapoint]) = {
    this()
    xAchseName = xAchse
    yAchseName = yAchse
    this.labelBegriff = labelBegriff
    this.labelList = labelList
    this.trainingsdaten = trainingsdaten
    this.testdaten = testdaten
  }


  def setXachse(name : String): Unit = {
    xAchseName = name
  }

  def setYachse(name: String): Unit = {
    yAchseName = name
  }

  def setTrainingsdaten(p : util.ArrayList[Datapoint]): Unit = {
    trainingsdaten.clear()
    for(i <- 0 until p.size()){
      trainingsdaten.add(p.get(i))
    }
  }

  def setTestdaten(p : util.ArrayList[Datapoint]): Unit = {
    testdaten.clear()
    for(i <- 0 until p.size()){
      testdaten.add(p.get(i))
    }
  }





}
