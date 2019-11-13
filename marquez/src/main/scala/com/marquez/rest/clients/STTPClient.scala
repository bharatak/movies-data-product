package com.marquez.rest.clients

import sttp.model.StatusCode

class STTPClient(baseURL: String) {

  private def constructJobPath(jobId: String) = s"$baseURL/jobs/runs/$jobId"

  private def postJobStatus(jobId: String, status: String): String = {
    val jobPath = constructJobPath(jobId)
    val postURL = status.toLowerCase match {
      case "complete" => s"$jobPath/complete"
      case "fail" | "failed" => s"$jobPath/fail"
    }
    postURL
  }

  def createJobRun(namespace: String, jobName: String): Option[String] = {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._
    import sttp.client.quick._
    import sttp.model.MediaType

    implicit val formats = DefaultFormats

    val basicResponse = basicRequest
      .post(uri"http://localhost:5000/api/v1/namespaces/$namespace/jobs/$jobName/runs")
      .contentType(MediaType.ApplicationJson)
      .body("""{"runArgs": {"email": "data@wework.com","emailOnFailure": false,"emailOnRetry": true, "retries": 1}}""")
      .send()

    if (basicResponse.code == StatusCode.Created) {
      (parse(basicResponse.body.right.get) \ "runId").extractOpt[String]
    } else {
      None
    }
  }

  def updateJobStatus(jobId: String, status: String): Int = {
    import sttp.client.quick._
    val response = quickRequest.post(uri"${postJobStatus(jobId, status)}").send()
    println(s"Status updated call returned, ${response.code.code}")
    response.code.code
  }
}

object STTPClient {
  def apply(baseURL: String): STTPClient = new STTPClient(baseURL)
}
