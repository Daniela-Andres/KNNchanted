package de

import de.MainApp.appendPar
import org.scalajs.dom
import org.scalajs.dom.{document, html}
import org.scalajs.dom.raw.{HTMLDivElement, HTMLInputElement, HTMLLabelElement}

/**
 * AlgoAbschnitt dient der Bereitstellung einer Eingabezeile für k
 * und Möglichkeiten die Metrik individuell anzupassen.
 *
 * @param root Übergeordnetes HMTLDivElement
 */

class AlgoAbschnitt (root: HTMLDivElement){

  //Inhalt von AlgoAbschnitt = algodiv sind eine Eingabezeile für die subsetSize=k,
  // und eine Möglichkeit die Metrik einzustellen

  var algodiv = document.createElement("div").asInstanceOf[HTMLDivElement]
  algodiv.classList.add("algodiv")
  root.appendChild(algodiv)

  //subsetSize Wert --> Textfeld + Eingabefeld
  var div_k = document.createElement("div").asInstanceOf[HTMLDivElement]
  div_k.classList.add("input-container")

  //Überschrift K:
  var label_k = document.createElement("label").asInstanceOf[HTMLLabelElement]
  label_k.textContent = "K:"
  div_k.appendChild(label_k)

  var input_k = document.createElement("input").asInstanceOf[HTMLInputElement]
  input_k.`type` = "text"
  input_k.id = "inputWert"
  input_k.name = "inputWert"
  input_k.value = "3"
  input_k.classList.add("inputfeld")
  div_k.appendChild(input_k)

  algodiv.appendChild(div_k)

  input_k.addEventListener("input", (event: dom.Event) => handleInputChange(event))
  input_k.onchange = e => {
    AlgoInfo.kWert = input_k.value.toInt
  }

  //Metrik --> Textfeld und //Todo Optionen
  var div_metrik = document.createElement("div").asInstanceOf[HTMLDivElement]
  div_metrik.classList.add("metrikdiv")

  //Überschrift Metrik :
  var label_metrik = document.createElement("label").asInstanceOf[HTMLLabelElement]
  label_metrik.textContent = "Metrik:"
  div_metrik.appendChild(label_metrik)

  var metrik_waehlen_div = document.createElement("div").asInstanceOf[HTMLDivElement]
  var merkmale_gewichten_div = document.createElement("div").asInstanceOf[HTMLDivElement]
  var quad_gewichtsabnahme_div = document.createElement("div").asInstanceOf[HTMLDivElement]

  div_metrik.appendChild(metrik_waehlen_div)
  div_metrik.appendChild(merkmale_gewichten_div)
  div_metrik.appendChild(quad_gewichtsabnahme_div)

  algodiv.appendChild(div_metrik)


  // Optionen

  //1. metrik_waehlen_div
  //metrik_waehlen_div enthält zwei Radio Buttons mit den verfügbaren Metriken (Euklid/Manhattan)
  var divEuklid = document.createElement("div").asInstanceOf[HTMLDivElement]
  val radioEuklid = document.createElement("input").asInstanceOf[HTMLInputElement]
  radioEuklid.`type` = "radio"
  radioEuklid.name = "radioGruppe1"
  radioEuklid.checked = true

  //Bei Änderungen AlgoInfo updaten
  radioEuklid.onchange = e => {
    if(radioEuklid.checked){
      AlgoInfo.metrik = 0
    }
  }

  divEuklid.appendChild(radioEuklid)

  var euklLabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  euklLabel.textContent = "Euklidischer Abstand"
  euklLabel.addEventListener("click", (event: dom.Event) => {
    radioEuklid.click()
  })
  divEuklid.appendChild(euklLabel)


  var divManhattan = document.createElement("div").asInstanceOf[HTMLDivElement]
  val radioManhattan = document.createElement("input").asInstanceOf[HTMLInputElement]
  radioManhattan.`type` = "radio"
  radioManhattan.name = "radioGruppe1"

  //Bei Änderungen AlgoInfo updaten
  radioManhattan.onchange = e => {
    if(radioManhattan.checked){
      AlgoInfo.metrik = 1
    }
  }

  divManhattan.appendChild(radioManhattan)

  var ManhattanLabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  ManhattanLabel.textContent = "Manhattan-Distanz"
  ManhattanLabel.addEventListener("click", (event: dom.Event) => {
    radioManhattan.click()
  })
  divManhattan.appendChild(ManhattanLabel)

  metrik_waehlen_div.appendChild(divEuklid)
  metrik_waehlen_div.appendChild(divManhattan)


  //2. merkmale_gewichten_div
  //TODO: Normale Checkbox --> gleichzeitig an und abwählen!
  //merkmale_gewichten_div enthält eine Radio-Checkbox (Partner in 3. quad_gewichtsabnahme_div), um die individuelle Gewichtung der Merkmale zu ermöglich
  //Ist die Checkbox gecheckt, schalten sich zwei Eingabefelder frei
  //In denen können individuelle Gewichtungen der Merkmale der x- und y-Achse angegeben werden.
  //Radio Button
  var divIndividuelleGewichtungRadio = document.createElement("div").asInstanceOf[HTMLDivElement]

  val radioIndividuelleGewichtung = document.createElement("input").asInstanceOf[HTMLInputElement]
  radioIndividuelleGewichtung.`type` = "checkbox"
  radioIndividuelleGewichtung.checked = false
  radioIndividuelleGewichtung.id = "radioIndividuelleGewichtung"
  divIndividuelleGewichtungRadio.appendChild(radioIndividuelleGewichtung)



  var individuelleGewichtungLabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  individuelleGewichtungLabel.textContent = "Individuelle Gewichtung der Merkmale"
  individuelleGewichtungLabel.addEventListener("click", (event: dom.Event) => {
    radioIndividuelleGewichtung.click()
  })
  divIndividuelleGewichtungRadio.appendChild(individuelleGewichtungLabel)

  merkmale_gewichten_div.appendChild(divIndividuelleGewichtungRadio)

  //Inputfelder
  var input_plus_label_X = document.createElement("div").asInstanceOf[HTMLDivElement]
  var input_plus_label_Y = document.createElement("div").asInstanceOf[HTMLDivElement]
  input_plus_label_X.classList.add("input-container")
  input_plus_label_Y.classList.add("input-container")

  merkmale_gewichten_div.appendChild(input_plus_label_X)
  merkmale_gewichten_div.appendChild(input_plus_label_Y)

  //Inputfeld X
  var label_individuelle_gewichtung_x = document.createElement("label").asInstanceOf[HTMLLabelElement]
  label_individuelle_gewichtung_x.textContent = "Individuelle Gewichtung des x-Merkmals:"

  input_plus_label_X.appendChild(label_individuelle_gewichtung_x)

  var input_individuelle_gewichtung_x = document.createElement("input").asInstanceOf[HTMLInputElement]
  input_individuelle_gewichtung_x.`type` = "text"
  input_individuelle_gewichtung_x.value = "1"
  input_individuelle_gewichtung_x.id = "input_individuelle_gewichtung_x"


  input_individuelle_gewichtung_x.addEventListener("input", (event: dom.Event) => handleInputChange2(event))

  input_plus_label_X.appendChild(input_individuelle_gewichtung_x)

  //Inputfeld Y
  var label_individuelle_gewichtung_y = document.createElement("label").asInstanceOf[HTMLLabelElement]
  label_individuelle_gewichtung_y.textContent = "Individuelle Gewichtung des y-Merkmals:"

  input_plus_label_Y.appendChild(label_individuelle_gewichtung_y)

  var input_individuelle_gewichtung_y = document.createElement("input").asInstanceOf[HTMLInputElement]
  input_individuelle_gewichtung_y.`type` = "text"
  input_individuelle_gewichtung_y.value = "1"
  input_individuelle_gewichtung_y.id = "input_individuelle_gewichtung_y"

  input_individuelle_gewichtung_y.addEventListener("input", (event: dom.Event) => handleInputChange2(event))

  input_plus_label_Y.appendChild(input_individuelle_gewichtung_y)




  //3. quad_gewichtsabnahme_div
  //Soll das Gewicht quadratisch mit wachsendem Abstand abhnehmen, muss noch eine Konstante/Abnahmegeschwindigkeit alpha eingestellt werden
  // Das Div besteht aus einem Radio Button und einem eingabefeld (jeweils + Label)
  var divQuadratischeGewichtungRadio = document.createElement("div").asInstanceOf[HTMLDivElement]

  val radioQuadratischeGewichtung = document.createElement("input").asInstanceOf[HTMLInputElement]
  radioQuadratischeGewichtung.`type` = "checkbox"
  radioQuadratischeGewichtung.id = "radioQuadratischeGewichtung"
  divQuadratischeGewichtungRadio.appendChild(radioQuadratischeGewichtung)


  var quadratischeGewichtungLabel = document.createElement("label").asInstanceOf[HTMLLabelElement]
  quadratischeGewichtungLabel.textContent = "Quadratische Gewichtung des Abstands"
  quadratischeGewichtungLabel.addEventListener("click", (event: dom.Event) => {
    radioQuadratischeGewichtung.click()
  })
  divQuadratischeGewichtungRadio.appendChild(quadratischeGewichtungLabel)

  quad_gewichtsabnahme_div.appendChild(divQuadratischeGewichtungRadio)

  //Inputfeld Alpha = Abnahmegeschwindigkeit
  var alphadiv = document.createElement("div").asInstanceOf[HTMLDivElement]

  var label_alpha = document.createElement("label").asInstanceOf[HTMLLabelElement]
  label_alpha.textContent = "Abnahmegeschwindigkeit: "

  alphadiv.appendChild(label_alpha)

  var input_alpha = document.createElement("input").asInstanceOf[HTMLInputElement]
  input_alpha.`type` = "text"
  input_alpha.value = "1"
  input_alpha.id = "input_alpha"

  input_alpha.addEventListener("input", (event: dom.Event) => handleInputChange2(event))

  alphadiv.appendChild(input_alpha)

  quad_gewichtsabnahme_div.appendChild(alphadiv)


  /**
   * Handler für die Eingabe von k.
   * Zugelassen sind nur positive Zahlen 0-9
   * @param event Eingabeevent
   */
  def handleInputChange(event: dom.Event): Unit = {
    val target = event.target.asInstanceOf[html.Input]
    val inputValue = target.value

    // Remove non-numeric characters using a regular expression
    val numericValue = inputValue.replaceAll("[^0-9]", "")

    // Update the input value with the cleaned numeric value
    target.value = numericValue
  }

  /**
   * Handler für die Eingabe der Gewichte.
   * Zugelassen sind nur positive Dezimalzahlen, die mit Komma oder Punkt geschrieben werden dürfen
   * @param event Eingabeevent
   */
  //Input für Gewichtung der Merkmale und Abnahmegeschwindigkeit --> Kommazahlen mit . und ,
  def handleInputChange2(event: dom.Event): Unit = {
    val target = event.target.asInstanceOf[html.Input]
    val inputValue = target.value

    // Remove non-numeric characters using a regular expression
    val numericValue = inputValue.replaceAll("[^0-9,.]", "")

    // Update the input value with the cleaned numeric value
    target.value = numericValue
  }



}
