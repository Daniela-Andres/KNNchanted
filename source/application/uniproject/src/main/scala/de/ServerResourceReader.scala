package de

import org.scalajs.dom.ext.Ajax

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

/**
 * Hilfsklasse zum Empfangen von Daten vom Server
 */
class ServerResourceReader {

  private implicit val context: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def resolveFullName(resourceName: String): String = {
    if (resourceName.startsWith("/")) throw new IllegalArgumentException("Invalid resource name: must not begin with / in server mode!")
    "./" + resourceName
  }

  def getResourceBytes(resourceName: String): Future[Array[Byte]] = {
    println("getResourceBytes(" + resourceName + "), resolving to: " + resolveFullName(resourceName))
    Ajax.get(resolveFullName(resourceName), responseType = "arraybuffer").map(_.response).map(_.asInstanceOf[ArrayBuffer]).map(parseByteBuffer)
  }

  private def parseByteBuffer(buf: ArrayBuffer): Array[Byte] = {
    val typedBuffer = TypedArrayBuffer.wrap(buf)
    val arr = new Array[Byte](typedBuffer.remaining)
    typedBuffer.get(arr)
    arr
  }

  def getResourceLines(resourceName: String): Future[List[String]] = {
    getResourceText(resourceName).map(_ split "\n").map(_.toList)
  }

  def getResourceText(resourceName: String): Future[String] = {
    println("getResourceBytes(" + resourceName + "), resolving to: " + resolveFullName(resourceName))
    Ajax.get(resolveFullName(resourceName)).map(_.responseText)
  }



}
