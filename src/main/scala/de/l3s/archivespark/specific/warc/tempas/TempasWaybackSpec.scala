package de.l3s.archivespark.specific.warc.tempas

import java.net.URLEncoder

import de.l3s.archivespark.dataspecs.DataSpec
import de.l3s.archivespark.utils.RddUtil
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.SystemDefaultHttpClient
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.io.Source

class TempasWaybackSpec private (query: String, from: Option[Int] = None, to: Option[Int] = None, pages: Int, resultsPerPage: Int) extends DataSpec[TempasYearResult, TempasWaybackRecord] {
  import TempasWaybackSpec._

  def searchUrl(page: Int): String = {
    val queryEncoded = URLEncoder.encode(query, "UTF-8")
    var url = TempasSearchUrl.replace("$r", resultsPerPage.toString).replace("$p", page.toString).replace("$q", queryEncoded)
    if (from.isDefined) url += "&from=" + from.get
    if (to.isDefined) url += "&to=" + to.get
    url
  }

  override def load(sc: SparkContext, minPartitions: Int): RDD[TempasYearResult] = {
    RddUtil.parallelize(sc, 1 to pages, minPartitions).flatMap{ page =>
      val client = new SystemDefaultHttpClient()
      val get = new HttpGet(searchUrl(page))
      get.setHeader("Accept", AcceptType)
      val in = client.execute(get).getEntity.getContent
      try {
        Source.fromInputStream(in).getLines().toList.flatMap { line =>
          TempasYearResult.resultsFromTsv(line)
        }
      } finally {
        in.close()
      }
    }.cache
  }

  override def parse(result: TempasYearResult): Option[TempasWaybackRecord] = {
    Some(new TempasWaybackRecord(result))
  }
}

object TempasWaybackSpec {
  val TempasSearchUrl = "http://tempas.l3s.de/v2/query?resultsPerPage=$r&page=$p&q=$q"
  val DefaultResultsPerPage = 100
  val DefaultPages = 100
  val AcceptType = "text/tab-separated-values"

  def apply(query: String, from: Int = -1, to: Int = -1, pages: Int = DefaultPages, resultsPerPage: Int = DefaultResultsPerPage): TempasWaybackSpec = {
    val fromOpt = if (from < 0) None else Some(from)
    val toOpt = if (to < 0) None else Some(to)
    new TempasWaybackSpec(query, fromOpt, toOpt, pages, resultsPerPage)
  }
}