import sttp.client.quick._
val jobId = "00783b41-9918-4abb-8aec-f8a6a86369f5"
val responseBody = quickRequest.get(uri"http://localhost:5000/api/v1/jobs/runs/$jobId").send().body
import org.json4s._
import org.json4s.jackson.JsonMethods._
implicit val formats = DefaultFormats
(parse(responseBody) \ "runId").extract[String]
(parse(responseBody) \ "runState").extract[String]
