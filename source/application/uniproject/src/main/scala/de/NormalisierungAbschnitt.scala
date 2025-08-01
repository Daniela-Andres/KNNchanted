package de

import de.MainApp.appendPar
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLDivElement, HTMLInputElement, HTMLLabelElement}

import java.util

/**
 * Dieser Abschnitt stellt eine Min-Max-Normalisierung auf Intervall [0,1] zur Verfügung
 * @param root Übergeordnetes HMTLDivElement
 * @param coordsys Verwendetes Koordinatensystem, auf dem die Änderungen gezeichnet werden
 */

class NormalisierungAbschnitt(root: HTMLDivElement, coordsys : CoordinateSystem) {


  var normdiv: HTMLDivElement = document.createElement("div").asInstanceOf[HTMLDivElement]
  normdiv.classList.add("normdiv")

  root.appendChild(normdiv)

  val xNormCheckbox = createCheckbox("xNorm", "Normalisierung in x-Richtung", "x")

  val xlabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  xlabel.textContent = "Normalisierung in x-Richtung"
  xlabel.id = "xNormLabel"

  val xCheck = document.createElement("div").asInstanceOf[HTMLDivElement]
  xCheck.classList.add("checkbox-wrapper")
  xCheck.id = "xCheck"
  xCheck.appendChild(xNormCheckbox)
  xCheck.appendChild(xlabel)
  normdiv.appendChild(xCheck)

  val yNormCheckbox = createCheckbox("yNorm", "Normalisierung in y-Richtung", "y")

  val ylabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  ylabel.textContent = "Normalisierung in y-Richtung"
  ylabel.id = "xNormLabel"

  val yCheck = document.createElement("div").asInstanceOf[HTMLDivElement]
  yCheck.id = "yCheck"
  yCheck.classList.add("checkbox-wrapper")
  yCheck.appendChild(yNormCheckbox)
  yCheck.appendChild(ylabel)
  normdiv.appendChild(yCheck)

  xNormCheckbox.onchange = e => {
    coordsys.clearBackground()

    var trainingsdaten = DataInfo.trainingsdaten
    var testdaten = DataInfo.testdaten
    var data = new util.ArrayList[Datapoint]()
    data.addAll(trainingsdaten)
    data.addAll(testdaten)
    //Wenn checked --> Normalisierung anwenden
    if(xNormCheckbox.checked){
      //Normalisierung: Min-Max Normalisierung auf Intervall [0,1]

      //ursprüngliche Daten zwischenspeichern
      Reverse.xData.clear()
      for(i <- 0 until data.size()){
        Reverse.xData.add(data.get(i).getX())
      }

      //Maximum und Minimum rausfinden
      var maxX = data.get(0).getX()
      var minX = data.get(0).getX()
      for (i <- 1 until data.size()) {
        var x = data.get(i).getX()

        if(x > maxX){
          maxX = x
        }
        if(x < minX){
          minX = x
        }
      }

      //Daten normalisieren
      var normData = new util.ArrayList[Datapoint]()
      for (i <- 0 until data.size()) {
        normData.add(new Datapoint((data.get(i).getX()-minX)/(maxX-minX),data.get(i).getY(),data.get(i).getLabel()))
      }

      //Zurück in Trainingsdaten/Testdaten
      var trainingslength = DataInfo.trainingsdaten.size()

      DataInfo.trainingsdaten.clear()
      for(i <- 0 until trainingslength){
        DataInfo.trainingsdaten.add(normData.get(i))
      }

      DataInfo.testdaten.clear()
      for(i <- trainingslength until normData.size()){
        DataInfo.testdaten.add(normData.get(i))
      }

    }else{
      //Wieder auf ursprüngliche Daten setzen!
      var ursprData = new util.ArrayList[Datapoint]()
      for (i <- 0 until data.size()) {
        ursprData.add(new Datapoint(Reverse.xData.get(i), data.get(i).getY(),data.get(i).getLabel()))
      }
      var trainingslength = DataInfo.trainingsdaten.size()

      DataInfo.trainingsdaten.clear()
      for(i <- 0 until trainingslength){
        DataInfo.trainingsdaten.add(ursprData.get(i))
      }

      DataInfo.testdaten.clear()
      for(i <- trainingslength until ursprData.size()){
        DataInfo.testdaten.add(ursprData.get(i))
      }

    }

    coordsys.updateParams()
    coordsys.clear()
    coordsys.drawData()

  }

  yNormCheckbox.onchange = e => {
    coordsys.clearBackground()


    var trainingsdaten = DataInfo.trainingsdaten
    var testdaten = DataInfo.testdaten
    var data = new util.ArrayList[Datapoint]()
    data.addAll(trainingsdaten)
    data.addAll(testdaten)
    //Wenn checked --> Normalisierung anwenden
    if(yNormCheckbox.checked){
      //Normalisierung: Min-Max Normalisierung auf Intervall [0,1]

      //ursprüngliche Daten zwischenspeichern
      Reverse.yData.clear()
      for(i <- 0 until data.size()){
        Reverse.yData.add(data.get(i).getY())
      }

      //Maximum und Minimum rausfinden
      var maxY = data.get(0).getY()
      var minY = data.get(0).getY()
      for (i <- 1 until data.size()) {
        var y = data.get(i).getY()

        if(y > maxY){
          maxY = y
        }
        if(y < minY){
          minY = y
        }
      }

      //appendPar("Max Y " + maxY)
      //appendPar("Min Y " + minY)

      //Daten normalisieren
      var normData = new util.ArrayList[Datapoint]()
      for (i <- 0 until data.size()) {
        normData.add(new Datapoint(data.get(i).getX(),(data.get(i).getY()-minY)/(maxY-minY),data.get(i).getLabel()))
        // appendPar("Neuer y-Wert: " + (data.get(i).getY()-minY)/(maxY-minY))
      }

      //Zurück in Trainingsdaten/Testdaten
      var trainingslength = DataInfo.trainingsdaten.size()

      DataInfo.trainingsdaten.clear()
      for(i <- 0 until trainingslength){
        DataInfo.trainingsdaten.add(normData.get(i))
      }

      DataInfo.testdaten.clear()
      for(i <- trainingslength until normData.size()){
        DataInfo.testdaten.add(normData.get(i))
      }

    }else{
      //Wieder auf ursprüngliche Daten setzen!
      var ursprData = new util.ArrayList[Datapoint]()
      for (i <- 0 until data.size()) {
        ursprData.add(new Datapoint(data.get(i).getX(), Reverse.yData.get(i),data.get(i).getLabel()))
      }
      var trainingslength = DataInfo.trainingsdaten.size()

      DataInfo.trainingsdaten.clear()
      for(i <- 0 until trainingslength){
        DataInfo.trainingsdaten.add(ursprData.get(i))
      }

      DataInfo.testdaten.clear()
      for(i <- trainingslength until ursprData.size()){
        DataInfo.testdaten.add(ursprData.get(i))
      }

    }

    coordsys.updateParams()
    coordsys.clear()
    coordsys.drawData()

  }


  def createCheckbox(id: String, text: String, dimension: String): HTMLInputElement = {
    val checkbox = document.createElement("input").asInstanceOf[HTMLInputElement]
    checkbox.`type` = "checkbox"
    checkbox.id = id + "box"
    checkbox.value = "no"
    checkbox.name = text
    checkbox.classList.add("checkbox")

    checkbox
  }



}
