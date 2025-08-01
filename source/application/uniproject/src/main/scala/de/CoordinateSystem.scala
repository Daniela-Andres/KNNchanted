package de

import de.MainApp.appendPar
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLDivElement, HTMLInputElement, HTMLLabelElement}
import org.scalajs.dom.{MouseEvent, document, html, window}

import java.util
import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`
import scala.collection.mutable.ArrayBuffer
import scala.math.BigDecimal.RoundingMode

/**
 * Koordinatensystem zur Anzeige der Datenpunkte und des Ergebnisses der Klassifizierung
 */
class CoordinateSystem() {

  var canvas: html.Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]


  var backgroundcanvas : html.Canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
  backgroundcanvas.classList.add("overlappedCanvas")
  backgroundcanvas.id = "overlappedCanvas"

  val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  val ctx2 = backgroundcanvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  var canvasdiv = document.createElement("div").asInstanceOf[HTMLDivElement]
  var wrapperdiv = document.createElement("div").asInstanceOf[HTMLDivElement]

  wrapperdiv.appendChild(backgroundcanvas)
  wrapperdiv.appendChild(canvas)

  canvasdiv.appendChild(wrapperdiv)
  canvasdiv.classList.add("canvasdiv")
  wrapperdiv.classList.add("wrapper")



  //Checkbox für Testdaten
  var checkboxdiv = document.createElement("div").asInstanceOf[HTMLDivElement]

  var testdatencheckbox = document.createElement("input").asInstanceOf[HTMLInputElement]
  testdatencheckbox.`type` = "checkbox"

  var testdatencheckboxlabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  testdatencheckboxlabel.textContent = "Testdaten anzeigen"

  var testdatenFlag = false

  val colorList = new util.ArrayList(util.Arrays.asList("#ff0000", "#00e700", "#0165d5", "#ffff00", "#ff00ff", "#00ffff", "#d3d3d3", "#9b59b6", "#ff9a00"))


  testdatencheckbox.onchange = e => {
    //Invertiere Flag
    testdatenFlag = !testdatenFlag

    if (testdatenFlag) {
      drawTestdaten()
    } else {
      clear()
      drawData()
    }
  }

  checkboxdiv.appendChild(testdatencheckbox)
  checkboxdiv.appendChild(testdatencheckboxlabel)

  canvasdiv.appendChild(checkboxdiv)


  var canvasgroesse: Int = (window.innerHeight * 0.4).toInt
  canvas.width = canvasgroesse
  canvas.height = canvasgroesse

  backgroundcanvas.width = canvasgroesse
  backgroundcanvas.height = canvasgroesse


  //Kart
  var x_max: Double = Double.MinValue
  var y_max: Double = Double.MaxValue
  var x_min: Double = Double.MaxValue
  var y_min: Double = Double.MaxValue

  //Pixel
  var rahmenabstand = 20
  var mid: Double = canvasgroesse / 2.0
  var objectsize = 8
  var pointsize: Double = 8

  var anzahlstriche = 10.0
  var strichabstand = 5.0
  var zusaetzlicherAbstand = 25.0

  //Kart
  var startstrichX = 0.0
  var startstrichY = 0.0

  //Länge der sichtbaren Achse
  var deltaMax = 20.0
  var achsenlaengeInPixeln: Double = canvasgroesse - zusaetzlicherAbstand - objectsize // ganze Größe - abstand links + pfeilgröße
  var achsenlaengeInKart = 20.0

  var SCALE: Double = achsenlaengeInPixeln / achsenlaengeInKart


  /**
   * Malt die Testdaten
   */
  def drawTestdaten(): Unit = {
    for (i <- 0 until DataInfo.testdaten.size()) {
      val x = DataInfo.testdaten.get(i).getX()
      val y = DataInfo.testdaten.get(i).getY()

      //Testdaten als Kreis
      /*ctx.beginPath()
      ctx.strokeStyle = "#000000"
      ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, pointsize / 2, 0, Math.PI * 2, false)
      ctx.fillStyle = getColor(DataInfo.testdaten.get(i))
      ctx.fill()
      ctx.stroke()*/


      //Testdaten als Kreisring
      // Zuerst den äußeren Rand zeichnen
      ctx.beginPath()
      ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, pointsize/2, 0, Math.PI * 2, false)
      ctx.strokeStyle = "black"
      ctx.lineWidth = 2
      ctx.stroke()

      // Den inneren Rand zeichnen
      ctx.beginPath()
      ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, 0.5 * pointsize/2, 0, Math.PI * 2, false)
      ctx.stroke()

      // Den Kreisring füllen
      ctx.beginPath()
      ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, pointsize/2, 0, Math.PI * 2, false)
      ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, 0.5 * pointsize/2, 0, Math.PI * 2, true)
      ctx.closePath()
      ctx.fillStyle = getColor(DataInfo.testdaten.get(i))  // Farbe des Kreisrings
      ctx.fill()
    }
  }

  /**
   * Berechnet die Variablen max, min, achsenlaenge, SCALE neu.
   * Muss immer aufgerufen werden, wenn ein Datensatz neu geladen wird.
   */
  def updateParams(): Unit = {
    x_max = Double.MinValue
    y_max = Double.MinValue
    x_min = Double.MaxValue
    y_min = Double.MaxValue
    //MinMax aus Trainingsdaten
    for (i <- 0 until DataInfo.trainingsdaten.size()) {
      val x = DataInfo.trainingsdaten.get(i).getX()
      val y = DataInfo.trainingsdaten.get(i).getY()
      if (x > x_max) {
        x_max = x
      }
      if (y > y_max) {
        y_max = y
      }
      if (x < x_min) {
        x_min = x
      }
      if (y < y_min) {
        y_min = y
      }
    }
    //MinMax aus Testdaten
    for (i <- 0 until DataInfo.testdaten.size()) {
      val x = DataInfo.testdaten.get(i).getX()
      val y = DataInfo.testdaten.get(i).getY()
      if (x > x_max) {
        x_max = x
      }
      if (y > y_max) {
        y_max = y
      }
      if (x < x_min) {
        x_min = x
      }
      if (y < y_min) {
        y_min = y
      }
    }

    //appendPar("X: max: " + x_max + " min: " + x_min)
    //appendPar("Y: max: " + y_max + " min: " + y_min)

    var delta1 = x_max - x_min
    var delta2 = y_max - y_min

    deltaMax = math.max(delta1, delta2)

    //Achsenstriche, wie sie aus den Werten berechnet sein müssten. Evtl. so was wie 4,1 --> unschön!
    var gewuenschteStriche = 10 //Kann durch das Runden abweichen!
    var real_achsenstriche = deltaMax / gewuenschteStriche

    //Ziel: Achsensstriche ans näheste 1,2,5,10,20,... matchen
    var potenz = math.floor(math.log10(deltaMax / 10))
    var option1 = 1 * math.pow(10, potenz)
    var option2 = 2 * math.pow(10, potenz)
    var option3 = 5 * math.pow(10, potenz)
    var option4 = 10 * math.pow(10, potenz)

    var optionList = new util.ArrayList[Double]()
    optionList.add(option1)
    optionList.add(option2)
    optionList.add(option3)
    optionList.add(option4)

    var minabs = math.abs(real_achsenstriche - option1)
    var optionIndex = 0
    for (i <- 1 until optionList.length) {
      if (math.abs(real_achsenstriche - optionList(i)) < minabs) {
        minabs = math.abs(real_achsenstriche - optionList(i))
        optionIndex = i
      }
    }

    strichabstand = optionList(optionIndex)

    startstrichX = math.floor(x_min / strichabstand) * strichabstand
    var anzahlstricheX = 1
    while (startstrichX < x_max) {
      startstrichX = startstrichX + strichabstand
      anzahlstricheX = anzahlstricheX + 1
    }

    startstrichX = math.floor(x_min / strichabstand) * strichabstand

    startstrichY = math.floor(y_min / strichabstand) * strichabstand
    var anzahlstricheY = 1
    while (startstrichY < y_max) {
      startstrichY = startstrichY + strichabstand
      anzahlstricheY = anzahlstricheY + 1
    }

    startstrichY = math.floor(y_min / strichabstand) * strichabstand


    anzahlstriche = math.max(anzahlstricheX, anzahlstricheY)

    var startX = startstrichX
    var endX = startstrichX + (anzahlstriche - 1) * strichabstand
    var delX = endX - startX

    var startY = startstrichY
    var endY = startstrichY + (anzahlstriche - 1) * strichabstand
    var delY = endY - startY

    /**
     * appendPar("StartX " + startX)
     * appendPar("EndX " + endX)
     * appendPar("DelX " + delX)
     *
     * appendPar("StartY " + startY)
     * appendPar("EndY " + endY)
     * appendPar("DelY " + delY)
     * */

    achsenlaengeInKart = math.max(delX, delY) + 2 * strichabstand

    SCALE = achsenlaengeInPixeln / achsenlaengeInKart


  }

  /**
   * Malt die Koordinatensachsen
   */
  def drawAxis(): Unit = {
    //x-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(rahmenabstand, mid)
    ctx.lineTo(canvasgroesse - rahmenabstand, mid)
    ctx.stroke()

    //y-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(mid, rahmenabstand)
    ctx.lineTo(mid, canvasgroesse - rahmenabstand)
    ctx.stroke()

    //Achsenpfeile
    //x-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(canvasgroesse - rahmenabstand, mid)
    ctx.lineTo(canvasgroesse - rahmenabstand + objectsize / 2, mid)
    ctx.lineTo(canvasgroesse - rahmenabstand + objectsize / 2, mid - objectsize / 2)
    ctx.lineTo(canvasgroesse - rahmenabstand + objectsize, mid)
    ctx.lineTo(canvasgroesse - rahmenabstand + objectsize / 2, mid + objectsize / 2)
    ctx.lineTo(canvasgroesse - rahmenabstand + objectsize / 2, mid)
    ctx.fillStyle = "#000000"
    ctx.fill()
    ctx.stroke()

    //y-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(mid, rahmenabstand)
    ctx.lineTo(mid, rahmenabstand - objectsize / 2)
    ctx.lineTo(mid - objectsize / 2, rahmenabstand - objectsize / 2)
    ctx.lineTo(mid, rahmenabstand - objectsize)
    ctx.lineTo(mid + objectsize / 2, rahmenabstand - objectsize / 2)
    ctx.lineTo(mid, rahmenabstand - objectsize / 2)
    ctx.fillStyle = "#000000"
    ctx.fill()
    ctx.stroke()
  }

  /**
   * Todo: Zeichnet die Achsennamen
   */
  def writeAchsennamen(): Unit = {
    //TODO anpassen und einbauen

    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.fillStyle = "#000000"
    ctx.lineWidth = 1
    ctx.font = "10px Arial" // Schriftgröße und -art anpassen

    //X-Achsenbeschriftung
    var metricsX = ctx.measureText(DataInfo.xAchseName)
    metricsX.width

    var abs = 7
    ctx.fillText(DataInfo.xAchseName, canvasgroesse - rahmenabstand - metricsX.width + objectsize, mid - abs)

    //Y-Achsenbeschriftung
    ctx.fillText(DataInfo.yAchseName, mid + abs, rahmenabstand)


  }


  /**
   * Löscht die aktuelle Darstellung auf dem Canvas der Datenpunkte
   */
  def clear(): Unit = {
    ctx.clearRect(0, 0, canvasgroesse, canvasgroesse)
  }

  /**
   * Löscht die aktuelle Darstellung auf dem Hintergrund-Canvas
   */
  def clearBackground(): Unit = {
    ctx2.clearRect(0,0,canvasgroesse,canvasgroesse)
  }

  /**
   * Umrechnung von Bildschirmkoordinaten zu kartesischen Koordinaten
   * @param x Bildschirmkoordinate x
   */
  def toScreenCoordsX(x: Double): Double = {
    zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE
  }

  /**
   * Umrechnung von Bildschirmkoordinaten zu kartesischen Koordinaten
   * @param y Bildschirmkoordinate y
   */
  def toScreenCoordsY(y: Double): Double = {
    canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE
  }

  /**
   * Umrechnung von kartesischen Koordinaten zu Bildschirmkoordinaten
   * @param x kartesischen Koordinate x
   */
  def toDataCoordsX(x: Double): Double = {
    (x - zusaetzlicherAbstand)/SCALE + startstrichX - strichabstand
  }

  /**
   * Umrechnung von kartesischen Koordinaten zu Bildschirmkoordinaten
   * @param y kartesischen Koordinate y
   */
  def toDataCoordsY(y: Double): Double = {
    startstrichY - strichabstand - (y - canvasgroesse + zusaetzlicherAbstand)/SCALE
  }


  /**
   * Zeichnet Koordinatensystem und Datenpunkte
   */
  def drawData(): Unit = {

    //Achsenpfeile malen
    //x-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(0, canvasgroesse - zusaetzlicherAbstand)
    ctx.lineTo(canvasgroesse, canvasgroesse - zusaetzlicherAbstand)
    ctx.stroke()

    //y-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(zusaetzlicherAbstand, 0)
    ctx.lineTo(zusaetzlicherAbstand, canvasgroesse)
    ctx.stroke()

    //Achsenpfeile
    //x-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(canvasgroesse - objectsize, canvasgroesse - zusaetzlicherAbstand)
    ctx.lineTo(canvasgroesse - objectsize, canvasgroesse - zusaetzlicherAbstand - objectsize / 2)
    ctx.lineTo(canvasgroesse, canvasgroesse - zusaetzlicherAbstand)
    ctx.lineTo(canvasgroesse - objectsize, canvasgroesse - zusaetzlicherAbstand + objectsize / 2)
    ctx.lineTo(canvasgroesse - objectsize, canvasgroesse - zusaetzlicherAbstand)
    ctx.fillStyle = "#000000"
    ctx.fill()
    ctx.stroke()

    //y-Achse
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.lineWidth = 1
    ctx.moveTo(zusaetzlicherAbstand, objectsize)
    ctx.lineTo(zusaetzlicherAbstand - objectsize / 2, objectsize)
    ctx.lineTo(zusaetzlicherAbstand, 0)
    ctx.lineTo(zusaetzlicherAbstand + objectsize / 2, objectsize)
    ctx.lineTo(zusaetzlicherAbstand, objectsize)
    ctx.fillStyle = "#000000"
    ctx.fill()
    ctx.stroke()


    //Testing
    //appendPar("Anzahlstriche: " + anzahlstriche)
    //appendPar("Strichabstand: " + strichabstand)
    //appendPar("StartX: " + startstrichX)
    //appendPar("StartY: " + startstrichY)


    //Achsenstriche
    ctx.beginPath()
    ctx.strokeStyle = "#000000"
    ctx.fillStyle = "#000000"
    ctx.lineWidth = 1
    ctx.font = "10px Arial" // Schriftgröße und -art anpassen

    var linienlaenge = 5 //Tatsächliche Länge 2*linienlaenge
    //x-Achse
    var i = 0
    while (i < anzahlstriche) {
      var x = zusaetzlicherAbstand + strichabstand * SCALE * (i + 1)
      ctx.moveTo(x, canvasgroesse - zusaetzlicherAbstand - linienlaenge)
      ctx.lineTo(x, canvasgroesse - zusaetzlicherAbstand + linienlaenge)

      // Wert des Koordinatensystems schreiben
      val value = startstrichX + strichabstand * i
      //Value auf "passende" Nachkommastelle runden
      //An Strichabstand binden
      var bigDecimal: BigDecimal = strichabstand
      var nachkommastellen = bigDecimal.scale
      var valueBigDec: BigDecimal = value
      valueBigDec = valueBigDec.setScale(nachkommastellen, RoundingMode.HALF_EVEN)

      ctx.fillText(valueBigDec.toString, x - 4, canvasgroesse - zusaetzlicherAbstand + 20) // Anpassen der Position //TODO zahl je nach länge verschieben 4

      i = i + 1
    }

    //y-Achse
    i = 0
    while (i < anzahlstriche) {
      var y = canvasgroesse - zusaetzlicherAbstand - strichabstand * SCALE * (i + 1)
      ctx.moveTo(zusaetzlicherAbstand - linienlaenge, y)
      ctx.lineTo(zusaetzlicherAbstand + linienlaenge, y)

      // Wert des Koordinatensystems schreiben
      val value = startstrichY + strichabstand * i
      //Value auf "passende" Nachkommastelle runden
      //An Strichabstand binden
      var bigDecimal: BigDecimal = strichabstand
      var nachkommastellen = bigDecimal.scale
      var valueBigDec: BigDecimal = value
      valueBigDec = valueBigDec.setScale(nachkommastellen, RoundingMode.HALF_EVEN)


      ctx.fillText(valueBigDec.toString, zusaetzlicherAbstand - linienlaenge - 20, y + 5) // Anpassen der Position //TODO zahl je nach länge verschieben 20

      i = i + 1
    }
    ctx.stroke()



    //Datenpunkte malen
    if (DataInfo != null && DataInfo.trainingsdaten != null && DataInfo.trainingsdaten.size != 0) {

      //Trainingsdaten & Testdaten malen
      val anzahlPunkte = (DataInfo.trainingsdaten.size() + DataInfo.testdaten.size())
      if (anzahlPunkte < 15) {
        pointsize = 18
      } else if (anzahlPunkte < 25) {
        pointsize = 14
      } else {
        pointsize = 10
      }

      //Testdaten evtl. malen
      if (testdatenFlag) {
        drawTestdaten()
      }

      //Trainingsdaten
      for (i <- 0 until DataInfo.trainingsdaten.size()) {
        val x = DataInfo.trainingsdaten.get(i).getX()
        val y = DataInfo.trainingsdaten.get(i).getY()

        ctx.beginPath()
        ctx.lineWidth = 1
        ctx.strokeStyle = "#000000"
        ctx.arc(zusaetzlicherAbstand + (x - startstrichX + strichabstand) * SCALE, canvasgroesse - zusaetzlicherAbstand - (y - startstrichY + strichabstand) * SCALE, pointsize / 2, 0, Math.PI * 2, false)
        ctx.fillStyle = getColor(DataInfo.trainingsdaten.get(i))
        ctx.fill()
        ctx.stroke()

        //appendPar("Point-Koord: X: " + x + " Y: " + y)
        //appendPar("Koord-Koord: X " + (mid + x * SCALE )+ " Y: " + (mid - y * SCALE))

      }
    } else {
      //appendPar("Fehler bei drawData-Methode")
    }

  }


  /**
   * Hilfsmethode zur Zuweisung der Farben für die Label
   * @param point einzufärbender Punkt
   * @return Farbe
   */
  def getColor(point: Datapoint): String = {

    val index = DataInfo.labelList.indexOf(point.label)
    colorList.get(index)
  }


  /**
   * Dient zum Einfärben eines Hintergrundpixels in der Farbe des Labels seines Mittelpunktes.
   * @param x Bildschirm x-Koordinate
   * @param y Bildschirm y-Koordinate
   * @param w Breite des Rechtecks
   * @param h Höhe des Rechtecks
   * @param k Anzahl der betrachteten nächsten Nachbarn
   * @param metrik Verwendete Metrik
   */
  def rechteckEinfaerben(x : Double, y :Double, w : Double, h : Double, k: Int, metrik: Metrik): Unit = {
    //Mittelpunkt muss aus DataPoint-Welt kommen
    var mittelpunkt = new Point2D(toDataCoordsX(x+w/2),toDataCoordsY(y+h/2))

    var label = DataInfo.knn(mittelpunkt, k, metrik)

    //Farbe bestimmen
    var farbe = {
      if (label.isDefined) {
        val index = DataInfo.labelList.indexOf(label.get)
        colorList.get(index)
      }else{
        "#FFFFFF"
      }
    }

    //Quadrat malen
    ctx2.beginPath()
    ctx2.strokeStyle = farbe + "07"
    ctx2.lineWidth = 1
    ctx2.rect(x,y,w,h)
    ctx2.fillStyle = farbe + "44"
    ctx2.fill()
    ctx2.stroke()

  }



}
