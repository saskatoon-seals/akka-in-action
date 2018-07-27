package httpserver

import scalaj.http.{Http, HttpOptions, HttpResponse}
import java.nio.file._

object SimpleHttpClient {
  val URL = "http://127.0.0.1:8000/"
  val fileUri = "/home/ales/Pictures/Edmonton/DSC03463.JPG"

  def main(args: Array[String]): Unit = {
    print(
      sendJson().body
    )
  }

  def sendJson(): HttpResponse[String] = {
    Http(URL).postData("""{"id":"12","json":"data"}""")
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
  }

  def transferFile(fileUri: String): HttpResponse[String] = {
    val fileBytes: Array[Byte] = Files.readAllBytes(
      Paths.get(fileUri)
    )

    Http(URL)
      .postData(fileBytes)
      .option(HttpOptions.readTimeout(10000)).asString
  }
}
