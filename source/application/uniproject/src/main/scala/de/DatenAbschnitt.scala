package de

import de.MainApp.appendPar
import org.scalajs.dom
import org.scalajs.dom.{Element, document, html}
import org.scalajs.dom.raw.{FileReader, HTMLButtonElement, HTMLDivElement, HTMLElement, HTMLHeadingElement, HTMLInputElement, HTMLOptionElement, HTMLSelectElement, HTMLTableElement}

import scala.util.{Failure, Success}
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
 * Abschnitt, der das Einlesen der Datensätze verwaltet
 * @param root Übergeordnetes HMTLDivElement
 * @param coordsys Verwendetes Koordinatesystem, auf dem die eingelesenen Daten gezeichnet werden
 * @param normalisierungsabschnitt Abschnitt zur Normalisierung der Datenpunkte (Normalisierung wird beim Einlesen neuer Daten zurückgesetzt)
 */

class DatenAbschnitt(root: HTMLDivElement, coordsys: CoordinateSystem, normalisierungsabschnitt : NormalisierungAbschnitt) {

  val csvParser = new Parser
  var serverResourceReader = new ServerResourceReader()

  //Inhalt von datenDiv sind ein Dropdown und 2 Buttons
  //Datensatz vom Server laden Dropdown + Button
  //Eigenen Datensatz laden Button

  var datendiv = document.createElement("div").asInstanceOf[HTMLDivElement]
  datendiv.classList.add("datendiv")
  root.appendChild(datendiv)

  //Div, das Button und Dropdown enthält
  val divloadServerButtonAndSelect = document.createElement("div").asInstanceOf[HTMLDivElement]
  divloadServerButtonAndSelect.classList.add("ButtonUDropdown")

  val loadServerDataButton = createButton("load-server-data", "Beispieldaten laden", e => loadServerDataButtonListener())
  loadServerDataButton.classList.add("loadDataButton")
  //datendiv.appendChild(loadServerDataButton)

  //DropDown zur Auswahl der Beispieldaten
  val dropdownBeispieldaten = document.createElement("select").asInstanceOf[HTMLSelectElement]
  dropdownBeispieldaten.id = "dropdownBeispieldaten"
  dropdownBeispieldaten.classList.add("select")
  val tshirt = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  tshirt.textContent = "t-shirt" //Name muss mit Dateinamen übereinstimmen!
  tshirt.selected = true
  val iris = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  iris.textContent = "iris"
  val geraete = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  geraete.textContent = "geraete"
  val ausreisser = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  ausreisser.textContent = "ausreisser"
  val anzahl_klein = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  anzahl_klein.textContent = "anzahl_klein"
  val anzahl_gross = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  anzahl_gross.textContent = "anzahl_gross"
  val drei_punkte = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  drei_punkte.textContent = "drei_punkte"
  val verteilung = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  verteilung.textContent = "verteilung"
  val zwei_ringe = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  zwei_ringe.textContent = "zwei_ringe"
  val pilze = (document.createElement("option").asInstanceOf[HTMLOptionElement])
  pilze.textContent = "pilze"

  dropdownBeispieldaten.appendChild(anzahl_gross)
  dropdownBeispieldaten.appendChild(anzahl_klein)
  dropdownBeispieldaten.appendChild(ausreisser)
  dropdownBeispieldaten.appendChild(drei_punkte)
  dropdownBeispieldaten.appendChild(verteilung)
  dropdownBeispieldaten.appendChild(zwei_ringe)

  dropdownBeispieldaten.appendChild(geraete)
  dropdownBeispieldaten.appendChild(iris)
  dropdownBeispieldaten.appendChild(pilze)
  dropdownBeispieldaten.appendChild(tshirt)



  divloadServerButtonAndSelect.appendChild(loadServerDataButton)
  divloadServerButtonAndSelect.appendChild(dropdownBeispieldaten)

  datendiv.appendChild(divloadServerButtonAndSelect)

  var loadLocalDataButtonDiv = document.createElement("div").asInstanceOf[HTMLDivElement]

  val loadLocalDataButton = createButton("load-local-data", "Eigenen Datensatz hochladen", e => loadlocalCSVDataButtonListener())
  loadLocalDataButton.classList.add("loadDataButton")

  loadLocalDataButtonDiv.appendChild(loadLocalDataButton)
  datendiv.appendChild(loadLocalDataButtonDiv)


  /**
   * Hilfsmethode zum Erstellen einer Schaltfläche
   * @param id ID des Buttons
   * @param label Label des Buttons
   * @param listener Zugewiesener Listener (=Was soll beim Klick auf den Button passieren)
   * @return Erstellten Button
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
   * Lädt Beispieldaten vom Server. Falls das Laden nicht erfolgreich ist, wird eine entsprechende Fehlermeldung ausgegeben.
   */
  def loadServerDataButtonListener(): Unit = {
    //lade je nach ausgewählt im DropDown einen Datensatz
    //Lese Daten in Datensatz ein

    val selectedIndex = dropdownBeispieldaten.selectedIndex
    val selectedOption = dropdownBeispieldaten.options(selectedIndex).textContent

    //appendPar("dropdown selectedOption: " + selectedOption)

    var fileName = "Fehler"

    var ordner = "Beispieldaten"

    fileName = ordner + "/" + selectedOption + ".txt"

    //appendPar(fileName)

    //hole File von Server
    val data: Future[List[String]] = serverResourceReader.getResourceLines(fileName)

    //Lade Daten
    data.onComplete {
      case Success(list) =>
        var sb = ""
        for (m <- 0 until list.size) {
          sb = sb + list(m) + "\n"
        }

        val tobeData = csvParser.parse(sb)
        tobeData.onComplete {
          case Success(d) =>
            //Normalisierung deaktivieren
            normalisierungsabschnitt.xNormCheckbox.checked = false
            normalisierungsabschnitt.yNormCheckbox.checked = false


            DataInfo.xAchseName = d.xAchseName
            DataInfo.yAchseName = d.yAchseName
            DataInfo.trainingsdaten = d.trainingsdaten
            DataInfo.testdaten = d.testdaten
            DataInfo.labelList = d.labelList
            DataInfo.labelBegriff = d.labelBegriff

            //Parameter updaten
            coordsys.updateParams()

            //clearen & stuff malen
            coordsys.clear()
            coordsys.clearBackground()

            //Konfusionsmatrix clearen
            var table = document.getElementById("tabelle").asInstanceOf[HTMLTableElement]
            table.innerHTML = "" // bewirkt table.children.clear()


            //Daten malen
            try {
              coordsys.drawData()
            } catch {
              case ex: Exception => {appendPar(ex.getMessage)}
            }


          case Failure(d) =>
            appendPar("Failure beim Parsen")
        }

      case Failure(list) =>
        appendPar("Failure beim Laden")
    }

  }

  /**
   * Lädt einen lokalen Datensatz von der Festplatte.
   */
  def loadlocalCSVDataButtonListener(): Unit = {

    val input = document.createElement("input").asInstanceOf[HTMLInputElement]
    input.`type` = "file"
    input.accept = ".csv,.txt"
    input.onchange = e => {
      var filelist = input.files
      var fileReader = new FileReader()
      fileReader.readAsText(filelist(0), "UTF-8")
      fileReader.onload = event => {

        val text = fileReader.result.toString

        val tobeData = csvParser.parse(text)

        tobeData.onComplete {
          case Success(d) =>
            //Normalisierung deaktivieren
            normalisierungsabschnitt.xNormCheckbox.checked = false
            normalisierungsabschnitt.yNormCheckbox.checked = false

            DataInfo.xAchseName = d.xAchseName
            DataInfo.yAchseName = d.yAchseName
            DataInfo.trainingsdaten = d.trainingsdaten
            DataInfo.testdaten = d.testdaten
            DataInfo.labelList = d.labelList
            DataInfo.labelBegriff = d.labelBegriff

            //Parameter updaten
            coordsys.updateParams()

            //clearen & stuff malen
            coordsys.clear()
            coordsys.clearBackground()

            //Konfusionsmatrix clearen
            var table = document.getElementById("tabelle").asInstanceOf[HTMLTableElement]
            table.innerHTML = "" // bewirkt table.children.clear()

            //Daten malen
            try {
              coordsys.drawData()

            } catch {
              case ex: Exception => appendPar(ex.getMessage)
            }


          case Failure(d) => ex: Exception => appendPar(ex.getMessage)

        }
      }
    }
    input.click()

  }

}
