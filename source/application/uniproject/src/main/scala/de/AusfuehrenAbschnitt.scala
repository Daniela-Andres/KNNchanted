package de

import de.MainApp.appendPar
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.{HTMLButtonElement, HTMLDivElement, HTMLElement, HTMLInputElement, HTMLTableElement, HTMLTableRowElement}

import java.util
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

/**
 * Stellt eine Schaltfläche zum Ausführen des KNN-Algorithmus zur Verfügung
 *
 * @param root     Übergeordnetes HMTLDivElement
 * @param coordsys Verwendetes Koordinatensystem, auf dem die Ergebnisse des KNN-Algorithmus gezeichnet werden
 */

//Enthält Button. Wenn gedrückt, wird Algorithmus ausgeführt --> coordsys gefärbt + Qualitätsanzeige erstellt/angezeigt
class AusfuehrenAbschnitt(root: HTMLDivElement, coordsys: CoordinateSystem) {

  val colorList = new util.ArrayList(util.Arrays.asList("#ff0000", "#00e700", "#0165d5", "#ffff00", "#ff00ff", "#00ffff", "#d3d3d3", "#9b59b6", "#ff9a00"))


  var ausfuehrenDiv = document.createElement("div").asInstanceOf[HTMLDivElement]
  ausfuehrenDiv.classList.add("ausfuehrenDiv")
  root.appendChild(ausfuehrenDiv)


  val knnButton = createButton("knn", "KNN ausfuehren", e => try {
    knn()
    updateKonfusionsmatrix()
  } catch {
    case exception: Exception => appendPar(exception.getMessage + " \n " + exception.getStackTrace.toString)
  })
  knnButton.classList.add("knnButton")
  ausfuehrenDiv.appendChild(knnButton)


  /**
   * Färbt den Hintergrund nach Anklicken des Ausführen-Buttons
   */
  def knn(): Unit = {
    var metrik: Metrik = getMetrik()

    coordsys.clearBackground()

    var k = AlgoInfo.kWert
    var points = new ArrayBuffer[Datapoint]()
    for (i <- 0 until DataInfo.trainingsdaten.size()) {
      points.addOne(new Datapoint(DataInfo.trainingsdaten.get(i).getX(), DataInfo.trainingsdaten.get(i).getY(), DataInfo.trainingsdaten.get(i).getLabel()))
    }

    var i = 8 // --> 2^8
    var groesseKaestchen = coordsys.canvasgroesse / Math.pow(2, i)

    for (x <- 0 until Math.pow(2, i).toInt) {
      for (y <- 0 until Math.pow(2, i).toInt) {
        coordsys.rechteckEinfaerben(x * groesseKaestchen, y * groesseKaestchen, groesseKaestchen, groesseKaestchen, k, metrik)
      }
    }

  }


  /**
   * Hilfsmethode zum Erstellen von Schaltflächen
   */
  def createButton(id: String, label: String, listener: dom.MouseEvent => Unit): HTMLElement = {
    val button = document.createElement("button").asInstanceOf[HTMLButtonElement]
    button.setAttribute("id", id)
    button.textContent = label
    button.addEventListener("click", (e: dom.MouseEvent) => {
      listener.apply(e)
      false
    })
    button.classList.add("dataLoadButton")
    button
  }


  /**
   * Unterroutine zum Aktualisieren der Konfusionsmatrix
   */
  def updateKonfusionsmatrix(): Unit = {
    var table = document.getElementById("tabelle").asInstanceOf[HTMLTableElement]
    table.innerHTML = "" // bewirkt table.children.clear()

    val labelList = DataInfo.labelList
    val length = labelList.size

    var hoehe = labelList.size + 2
    var rowList: util.ArrayList[HTMLTableRowElement] = new util.ArrayList[HTMLTableRowElement]()
    for (i <- 0 until hoehe) {
      rowList.add(table.insertRow(i).asInstanceOf[HTMLTableRowElement])

    }

    var aktuelleZeile = rowList.get(0)
    var zelle1 = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
    zelle1.colSpan = 2
    zelle1.rowSpan = 2
    var zelle2 = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
    zelle2.colSpan = length+1
    zelle2.textContent = "Vorhergesagter Wert"
    zelle2.classList.add("ueberschrift")


    aktuelleZeile = rowList.get(1)
    for (i <- 0 until length) {
      var zelle = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
      zelle.textContent = labelList.get(i)
      zelle.classList.add("highlight")
    }
    var zelle = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
    zelle.textContent = "uneindeutig"
    zelle.classList.add("highlight2")


    aktuelleZeile = rowList.get(2)
    var zelle4 = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
    zelle4.rowSpan = length
    zelle4.textContent = "Tatsächlicher Wert"
    zelle4.classList.add("ueberschrift")
    var zelle5 = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
    zelle5.textContent = labelList.get(0)
    zelle5.classList.add("highlight")

    for (i <- 0 until length+1) {
      var zelle = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
      zelle.textContent = "0"
    }

    for (i <- 1 until length) {
      aktuelleZeile = rowList.get(2 + i)
      var zelle = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
      zelle.textContent = labelList.get(i)
      zelle.classList.add("highlight")
      for (j <- 0 until length+1) {
        var zelle = aktuelleZeile.insertCell(-1).asInstanceOf[org.scalajs.dom.html.TableCell]
        zelle.textContent = "0"
      }
    }

    //Inhalte der Zellen anpassen!
    for (i <- 0 until DataInfo.testdaten.size()) {
      //Tatsächliches Label ist Index der Zeile
      var testpunkt = DataInfo.testdaten.get(i)
      var tLabel = testpunkt.getLabel()
      var zeileIndex = labelList.indexOf(tLabel)


      // Durch KNN vorhergesagtes Label herausfinden
      var metrik: Metrik = getMetrik()

      var k = AlgoInfo.kWert

      /*
      var sortierteTrainingsdaten: ArrayBuffer[Datapoint] = new ArrayBuffer[Datapoint]()
      for (j <- 0 until DataInfo.trainingsdaten.size()) {
        sortierteTrainingsdaten.addOne(DataInfo.trainingsdaten.get(j))
      }
      sortierteTrainingsdaten = sortierteTrainingsdaten.sortBy(x => metrik.abs(x, testpunkt))

      //appendPar("Konfusionsmatrik : " + sortierteTrainingsdaten.toString())

      //Ersten subsetSize-Knoten der distanzListe voten
      var anzahlArray = new Array[Int](labelList.size())

      for (j <- 0 until k) {
        var aktuellesLabel = sortierteTrainingsdaten(j).getLabel()
        for (m <- 0 until labelList.size()) {
          if (labelList.get(m) == aktuellesLabel) {
            anzahlArray(m) = anzahlArray(m) + 1
          }
        }
      }
      //Finde Maximum.
      //Falls Gleichstand --> weiß
      var sortedAnzahl = new Array[Int](anzahlArray.length)
      for (n <- 0 until sortedAnzahl.length) {
        sortedAnzahl(n) = anzahlArray(n)
      }
      sortedAnzahl = sortedAnzahl.sortBy(x => x)
      sortedAnzahl = sortedAnzahl.reverse

       */

      //Winner = Option mit zugewiesenem Label
      var winner = DataInfo.knn(testpunkt,k,metrik)
      var spalteIndex = if(winner.isDefined){
        labelList.indexOf(winner.get)
      }else{
        length
      }


      //zeileIndex, spalteIndex auf length x length Tabelle --> anderes Format durch "Außenrum"-Kram


      if (zeileIndex == 0) {
        zeileIndex = zeileIndex + 2
        spalteIndex = spalteIndex + 2
      } else {
        zeileIndex = zeileIndex + 2
        spalteIndex = spalteIndex + 1
      }

      val aktuelleZeile = table.rows(zeileIndex).asInstanceOf[HTMLTableRowElement]
      val aktuelleZelle = aktuelleZeile.cells(spalteIndex).asInstanceOf[org.scalajs.dom.html.TableCell]

      aktuelleZelle.textContent = (aktuelleZelle.textContent.toInt + 1).toString

    }


    //for(i <- 0 until hoehe){
    //  for(i <- 0 until hoehe){
    //    rowList.get(i).insertCell(-1)
    //  }
    //}


  }

  /**
   * Ermittelt die Metrik mit den in Zone "Parameter" eingestellten Eigenschaften
   * @return verwendete Metrik
   */
  def getMetrik(): Metrik = {

    //Schauen, ob in Eingabefeldern etwas sinnvolles drin steht
    var gewichtX_num = 1.0
    var gewichtY_num = 1.0

    if(document.getElementById("radioIndividuelleGewichtung").asInstanceOf[HTMLInputElement].checked){
      //Individuelle Gewichte --> gewichtX und gewichtY anschauen
      var gewichtX = document.getElementById("input_individuelle_gewichtung_x").asInstanceOf[HTMLInputElement].value
      var gewichtY = document.getElementById("input_individuelle_gewichtung_y").asInstanceOf[HTMLInputElement].value

      //Mit , und . einlesen können
      gewichtX = gewichtX.replaceAll(",", ".")
      gewichtY = gewichtY.replaceAll(",", ".")

      //Auf sinnvolle Werte prüfen
      try {
        gewichtX_num = gewichtX.toDouble
      } catch {
        case e: NumberFormatException => appendPar("Nicht sinnvolle Eingabe bei \"Individuelle Gewichtung des x-Merkmals\". Wert wird auf 1 gesetzt.")
        case e: Exception =>
      }
      try {
        gewichtY_num = gewichtY.toDouble
      } catch {
        case e: NumberFormatException => appendPar("Nicht sinnvolle Eingabe bei \"Individuelle Gewichtung des y-Merkmals\". Wert wird auf 1 gesetzt.")
        case e: Exception =>
      }
    }


    var metrik: Metrik = new Euklid(gewichtX_num, gewichtY_num)
    if (AlgoInfo.metrik == 1) {
      metrik = new Manhattan(gewichtX_num, gewichtY_num)
    }

    //appendPar(gewichtX_num.toString)
    //appendPar(gewichtY_num.toString)

    metrik

  }

}



