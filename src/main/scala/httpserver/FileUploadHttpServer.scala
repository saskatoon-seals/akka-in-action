package httpserver

import java.io.{InputStream, OutputStream, FileOutputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

/**
  * HTTP file upload server
  *
  * This server must be able to handle HTTP requests made by curl:
  *  - curl --upload-file "src_path/file_name" http://server_ip:server_port/dest_path/"
  *
  * which are translated into:
  *  - HTTP PUT server_ip:server_port/dest_path/filename
  *
  * CONSTRAINTS:
  *  - dest_dir must exist on the server's file system before the request is made
  */
object FileUploadHttpServer {
  def main(args: Array[String]) {
    val server = HttpServer.create(new InetSocketAddress(8000), 0)

    //Sever is dedicated to file uploads only (that's why a single route only)
    server.createContext("/", new SaveFileHandler())

    server.setExecutor(null)
    server.start()

    println("Hit any key to exit...")

    System.in.read()
    server.stop(0)
  }

}

class SaveFileHandler extends HttpHandler {
  def handle(context: HttpExchange) {
    saveFile(
      context.getRequestURI.toString,
      context.getRequestBody
    )

    sendResponse(context)
  }

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

  private def sendResponse(context: HttpExchange) {
    val response = "Ack!\n"
    context.sendResponseHeaders(200, response.length())
    val os = context.getResponseBody
    os.write(response.getBytes)
    os.close()
  }
}