package httpserver

import java.io.{InputStream, OutputStream, FileOutputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

object FileSaveServer {
  val outputFileUri = "/tmp/test_picture.JPG"

  def main(args: Array[String]) {
    val server = HttpServer.create(new InetSocketAddress(8000), 0)
    server.createContext("/upload", new SaveFileHandler())
    server.setExecutor(null)

    server.start()

    println("Hit any key to exit...")

    System.in.read()
    server.stop(0)
  }

}

class SaveFileHandler extends HttpHandler {

  def handle(t: HttpExchange) {
    val query: String = t.getRequestURI.getQuery

    saveFile(
      toDestinationUri(
        extractQueryParam(query, "dest_path"),
        extractQueryParam(query, "file_name")
      ),
      t.getRequestBody
    )

    sendResponse(t)
  }

  private def toDestinationUri(destPath: String, fileName: String) =
    destPath + (if (destPath.endsWith("/")) "" else "/") + fileName

  private def extractQueryParam(query: String, param: String): String = query
    .substring(query.indexOf(param))
    .split("=")(1)
    .split("&")(0)

  private def saveFile(destUri: String, body: InputStream): Unit ={
    println()
    println("******************** REQUEST START ********************")
    println()
    copyStream(
      body,
      new FileOutputStream(destUri)
    )
    println()
    println("********************* REQUEST END *********************")
    println()
  }

  private def copyStream(in: InputStream, out: OutputStream) {
    Iterator
      .continually(in.read)
      .takeWhile(-1 !=)
      .foreach(out.write)
  }

  private def sendResponse(t: HttpExchange) {
    val response = "Ack!"
    t.sendResponseHeaders(200, response.length())
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}