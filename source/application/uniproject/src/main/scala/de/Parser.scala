package de

import de.MainApp.appendPar

import java.util
import scala.concurrent.Future

/**
 * Hilfsklasse, die einen Datensatz einliest und für das Programm verarbeitbar macht
 */

class Parser {

  /**
   * Methode zum Einlesen eines Datensatzes
   * @param text Der einzulesende Datensatz als Textkette
   * @return Ein Datensatz-Objekt
   */
  def parse(text: String): Future[Datensatz] = {
    var trainingsdaten = new util.ArrayList[Datapoint]()
    var testdaten = new util.ArrayList[Datapoint]()
    var labelList = new util.ArrayList[String]()

    //appendPar(text)
    //X;Y 10;10 10;-10 -10;10 -10;-10 5;5
    //Lines ist eine Liste von Strings, in der die einzelnen Zeilen stehen --> noch an ; trennen
    var lines = text.split("\n")

    //In erster Zeile stehen Überschriften X;Y;Label
    var achsenBuffer = lines(0).split(";")
    var xAchse = achsenBuffer(0)
    var yAchse = achsenBuffer(1)
    var labelBegriff = achsenBuffer(2)

    var labelBuffer = lines(1).split(";")
    for( j <- labelBuffer){
      labelList.add(j.trim)
    }

    //Datenpunkte einlesen
    //Beginne in dritter Zeile, da in 2. Zeile "Trainingsdaten" steht
    var i = 3
    while(i < lines.length && !lines(i).contains("Testdaten")){
      var pointBuffer = lines(i).split(";")
      trainingsdaten.add(new Datapoint(pointBuffer(0).toDouble, pointBuffer(1).toDouble, pointBuffer(2).trim))

      //Label erfassen
      i = i+1
    }
    i = i+1
    while(i<lines.length){
      var pointBuffer = lines(i).split(";")
      testdaten.add(new Datapoint(pointBuffer(0).toDouble, pointBuffer(1).toDouble, pointBuffer(2).trim))

      //Label erfassen
      i = i+1
    }

    Future.successful(new Datensatz(xAchse, yAchse, labelBegriff, labelList, trainingsdaten, testdaten))
  }
}
