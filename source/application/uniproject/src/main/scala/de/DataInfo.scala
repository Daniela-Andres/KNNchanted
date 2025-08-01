package de

import de.MainApp.appendPar
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLInputElement

import java.util
import java.util.Comparator
import java.util.Map.Entry
import scala.::
import scala.collection.mutable.ArrayBuffer

/**
 * Hält die in das Projekt geladene Daten.
 * Hat Methoden, um den KNN-Algorithmus auszuführen.
 */
object DataInfo {

  var xAchseName = "X"
  var yAchseName = "Y"

  var labelList: util.ArrayList[String] = new util.ArrayList[String]()
  var labelBegriff = "" //z.B. T-Shirt Groesse

  var trainingsdaten: util.ArrayList[Datapoint] = new util.ArrayList[Datapoint]()
  var testdaten: util.ArrayList[Datapoint] = new util.ArrayList[Datapoint]()

  /**
   * Ermittelt das Label, das der KNN-Algorithmus einem neuen Punkt zugeorndet.
   * @param punkt Zu klassifizierender Punkt
   * @param k Anazhl der betrachteten nächsten Nachbarn
   * @param metrik Verwendete Metrik
   * @return Gibt Label, dem @param punkt zugeordnet wird zurück. Falls es einen Gleichstand beim Voting gibt, wird ein leeres Optional zurückgegeben.
   */
  def knn(punkt : Point2D, k : Int, metrik : Metrik): Option[String] = { //Gibt Label, dem es zugeordnet wird, zurück. Falls Gleichstand --> empty

    var points = new ArrayBuffer[Datapoint]()
    for (i <- 0 until DataInfo.trainingsdaten.size()) {
      points.addOne(new Datapoint(DataInfo.trainingsdaten.get(i).getX(), DataInfo.trainingsdaten.get(i).getY(), DataInfo.trainingsdaten.get(i).getLabel()))
    }
    points = points.sortBy(p => metrik.abs(p,punkt))

    //Manchmal, mehrere Punkte am k-t weitesten von @punkt entfernt
    //Rechenungenauigkeiten --> 0.0000000000000002 statt 0
    var anzahlUneindeutigePunkte = points.count(p => Math.abs(metrik.abs(p,punkt)-metrik.abs(points(k-1),punkt))<=0.0000000000000002)
    var anzahlEindeutigePunkte = points.count(p => metrik.abs(p,punkt)<metrik.abs(points(k-1),punkt))

    var uneindeutigePunkte =  new ArrayBuffer[Datapoint]() //Speichert uneindeutige Punkte (Summe > k)
    var eindeutigePunkte =  new ArrayBuffer[Datapoint]()

    for(i <- 0 until anzahlEindeutigePunkte){
      eindeutigePunkte.addOne(points(i))
    }
    for(i <- anzahlEindeutigePunkte until anzahlEindeutigePunkte + anzahlUneindeutigePunkte){
      uneindeutigePunkte.addOne(points(i))
    }

    var ergSet = new util.HashSet[Option[String]]()

    //Betrachte alle möglichen Teilmengen mit Größe k aus eindeutigen (alle) und uneindeutigen (nur manche) Punkten
    for(subset <- subsets(uneindeutigePunkte,k-anzahlEindeutigePunkte)){
      ergSet.add(abstimmung(punkt,eindeutigePunkte,subset,metrik))
    }

    //Wenn alle Abstimmungen dasselbe Ergebnis haben, dann färben
    if(ergSet.size()==1){
      ergSet.iterator().next()
    }else{
      //Ansonsten Untentschieden
      Option.empty
    }

  }


  /**
   * Erhält k viele Punkte. Gibt das Label zurück, für das diese k Punkte voten. Falls die Abstimmung nicht eindeutig ist, wird ein leeres Optional zurückgegeben
   * @param eindeutigePunkte Punkte, die auf jeden Fall in den nächsten k Nachbarn dabei sind
   * @param uneindeutigePunkte Punkte, die auf gleichem Rang in den k nächsten Nachbarn dabei sind
   * @return Label, falls ein eindeutiges gefunden wurde, ansonsten leeres Optional
   */
  def abstimmung(punkt : Point2D, eindeutigePunkte: ArrayBuffer[Datapoint], uneindeutigePunkte: ArrayBuffer[Datapoint],metrik : Metrik): Option[String] = {
    var map = new util.HashMap[String,Double]() //Label, Anzahl (key, value)

    for(i <- 0 until eindeutigePunkte.size + uneindeutigePunkte.size){

      //Aktueller anderer Punkt aus Liste
      var aktuellerPunkt = {
        if(i < eindeutigePunkte.size){
          eindeutigePunkte(i)
        }else{
          uneindeutigePunkte(i-eindeutigePunkte.size)
        }
      }

      var aktuellesLabel = aktuellerPunkt.getLabel()

      //Stimmen zählen
      map.putIfAbsent(aktuellesLabel,0)
      if(document.getElementById("radioQuadratischeGewichtung").asInstanceOf[HTMLInputElement].checked){
        //Quadratische Wertung der Stimmen in Abhängigkeit vom Abstand gewünscht

        //Bestimme d(punkt,aktuellerPunkt)
        var abstand = metrik.abs(punkt,aktuellerPunkt)

        //alpha auslesen
        var alpha_num = 1.0
        var alpha = document.getElementById("input_alpha").asInstanceOf[HTMLInputElement].value

        //Mit , und . einlesen können
        alpha = alpha.replaceAll(",", ".")

        //Auf sinnvolle Werte prüfen
        try {
          alpha_num = alpha.toDouble
        } catch {
          case e: NumberFormatException => appendPar("Nicht sinnvolle Eingabe bei \"Abnahmegeschwindigkeit\". Wert wird auf 1 gesetzt.")
          case e: Exception =>
        }

        var w_i = 1/(1+alpha_num*Math.pow(abstand,2))

        map.put(aktuellesLabel,map.get(aktuellesLabel)+w_i)
      }else{
        //Alles normal, jede Stimme hat Gewicht 1
        map.put(aktuellesLabel,map.get(aktuellesLabel)+1)
      }
    }

    //Liste sortieren
    var sorted = new util.ArrayList[Entry[String,Double]](map.entrySet())

    //sorted.sort(Entry.comparingByValue[String,Int]().reversed())

    //Früher, wirft jetzt fehler
    //sorted.sort((e1, e2)  => { (e2.getValue-e1.getValue) } )

    // Sortieren – absteigend nach dem Value (Double-Wert)
    /*sorted.sort(new Comparator[Entry[String, Double]] {
      override def compare(e1: Entry[String, Double], e2: Entry[String, Double]): Int = {
        java.lang.Double.compare(e2.getValue, e1.getValue) // e2 zuerst = absteigend
      }
    })*/

    sorted.sort((e1, e2) => java.lang.Double.compare(e2.getValue, e1.getValue))

    if((sorted.size() >= 2 && sorted.get(0).getValue == sorted.get(1).getValue) || sorted.size()==0){
      Option.empty
    }else{
      Option(sorted.get(0).getKey)
    }
  }


  /**
   * Ermitteln der Teilmengen der uneindeutigen Punkte. Unterroutine der Methode abstimmung()
   * @param list Alle uneindeutigen Punkte
   * @param subsetSize Anzahl der benötigten uneindeutigen Punkte
   * @return Liste von Teilmengen der uneindeutigen Punkte
   */
  def subsets(list: ArrayBuffer[Datapoint], subsetSize: Int): ArrayBuffer[ArrayBuffer[Datapoint]] = {
    var result: ArrayBuffer[ArrayBuffer[Datapoint]] = new ArrayBuffer[ArrayBuffer[Datapoint]]
    if (list.length <= subsetSize) {
      result.addOne(list)
      result
    } else if (subsetSize == 0) {
      result.addOne(new ArrayBuffer[Datapoint]())
      result
    } else {
      // Neue Liste list2 = list ohne letztes Element
      var list2 = new ArrayBuffer[Datapoint]()
      for (i <- 0 until list.length - 1) {
        list2.addOne(list(i))
      }

      var listlist1 = subsets(list2, subsetSize)
      var listlist2 = subsets(list2, subsetSize - 1)

      //Alle Listen aus listlist1 hinzufügen
      for (i <- 0 until listlist1.length) {
        result.addOne(listlist1(i))
      }
      for (i <- 0 until listlist2.length) {
        var plusLetztesElement = listlist2(i)
        plusLetztesElement.addOne(list(list.length - 1))
        result.addOne(plusLetztesElement)
      }
      result
    }

  }


}
