package de

import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLAnchorElement, HTMLButtonElement, HTMLDivElement, HTMLElement, HTMLLabelElement}
import org.scalajs.dom.{Blob, URL, document}

import java.util
import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Hilfsobjekt zum Erstellen der Startseite
 */

object MainApp {

  val menueButtonBackground1 = "dimgray"
  val menueButtonBackground2 = "#004188"

  def main(args: Array[String]): Unit = {
    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupDoc()
    })
  }


  def setupDoc(): Unit = {
    val coordsys = new CoordinateSystem()

    val normalisierungAbschnitt = new NormalisierungAbschnitt((document.getElementById("normalisierung").asInstanceOf[HTMLDivElement]),coordsys)

    val datenabschnitt = new DatenAbschnitt(document.getElementById("datensatz").asInstanceOf[HTMLDivElement], coordsys, normalisierungAbschnitt )

    val algoAbschnitt = new AlgoAbschnitt(document.getElementById("parameter").asInstanceOf[HTMLDivElement])
    val ausfuehrenAbschnitt = new AusfuehrenAbschnitt((document.getElementById("ausfuehren").asInstanceOf[HTMLDivElement]),coordsys)

    val canvasabschnitt = document.getElementById("coordsys").asInstanceOf[HTMLDivElement]
    canvasabschnitt.appendChild(coordsys.canvasdiv)

    coordsys.drawAxis()


  }

  def appendPar(text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.textContent = text
    document.getElementById("test-div").appendChild(parNode)
  }

  def createButton(id: String, label: String, classList: String, root: HTMLDivElement): HTMLElement = {
    val button = document.createElement("button").asInstanceOf[HTMLButtonElement]
    button.setAttribute("id", id)
    button.textContent = label
    button.classList.add(classList)
    root.appendChild(button)
    button
  }

  def createLabel(id: String, text: String, classList: String, root: HTMLDivElement): HTMLElement = {
    val label = document.createElement("label").asInstanceOf[HTMLLabelElement]
    label.setAttribute("id", id)
    label.textContent = text
    label.classList.add(classList)
    root.appendChild(label)
    label
  }

}
