package com.marquez.spark.readers

import com.marquez.rest.clients.STTPClient
import org.apache.spark.SparkContext
import org.apache.spark.scheduler.{JobSucceeded, SparkListener, SparkListenerApplicationEnd, SparkListenerJobEnd}
import org.apache.spark.sql.{DataFrame, SparkSession}

object Abv2NamesReader {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.
      master("local[*]").
      appName("Console").
      getOrCreate()
    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("error")
    val jobId: String =
      STTPClient("http://localhost:5000/api/v1")
        .createJobRun("wedata", "room_bookings_7_days").get
    sc.addSparkListener(new JobMetadataPushListener(jobId))
    val abv2namesDF: DataFrame =
      spark.read.format("csv")
        .option("delimiter", "\t")
        .option("header", "true")
        .load("resources/abbrevs-to-names.tsv")
    println(s"Number of lines: ${abv2namesDF.rdd.map(_ => None.get).count()}")
    spark.close()
  }
}

class JobMetadataPushListener(jobId: String) extends SparkListener {
  /*
  TODO - We can use SQLAppStatusListener to retrieve information about SQL jobs
  As of now, we are not able to identify the failures when the code is utilising
  SQL based constructs (i.e., DataFrame, DataSet and Spark SQL). Hence, we end up
  calling Marquez with "complete" status even though we are getting exceptions.
  Note: we are able to identify failed status in case of RDD operations, with the
  current code
   */
  private var finalApplicationStatus: String = "start"

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    STTPClient("http://localhost:5000/api/v1")
      .updateJobStatus(jobId, finalApplicationStatus)
    println(s"Job ID: $jobId")
    println("Application ended, finally!")
  }

  //  override def onJobStart(jobStart: SparkListenerJobStart): Unit = {
  //    println("Stage Information ::: ")
  //    jobStart.stageInfos.foreach(x => println(x.details))
  //    println(s"SQL job id: ${jobStart.properties.getProperty(SQLExecution.EXECUTION_ID_KEY)}")
  //    println(s"Normal job id: ${jobStart.jobId}")
  //  }

  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = {
    println(s"Job's status - ${jobEnd.jobResult}")
    finalApplicationStatus = jobEnd.jobResult match {
      case JobSucceeded => "complete"
      case _ => "fail"
    }
  }

  //  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
  //    println(s"Status :: ${taskEnd.taskInfo.status}")
  //  }
}


