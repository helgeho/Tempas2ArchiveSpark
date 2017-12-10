package de.l3s.archivespark.specific.warc.tempas

import de.l3s.archivespark.dataspecs.DataEnrichRoot
import de.l3s.archivespark.enrich.RootEnrichFunc
import de.l3s.archivespark.enrich.dataloads.{ByteContentLoad, TextLoad}
import de.l3s.archivespark.http.{HttpClient, HttpRecord}
import de.l3s.archivespark.specific.warc.enrichfunctions.HttpPayload
import de.l3s.archivespark.specific.warc.tempas.util.Time14

class TempasWaybackRecord(result: TempasYearResult) extends DataEnrichRoot[TempasYearResult, HttpRecord](result) with ByteContentLoad {
  val WaybackUrl = "http://web.archive.org/web/$timestampid_/$url"

  def waybackUrl(timestamp: String, url: String) = {
    WaybackUrl.replace("$timestamp", timestamp).replace("$url", url)
  }

  override def access[R >: Null](action: HttpRecord => R): R = {
    val timestamp = Time14.midTime14FromPrefix(result.year.toString)
    HttpClient.get(waybackUrl(timestamp, result.url)) match {
      case Some(record) => action(record)
      case None => null
    }
  }

  override def defaultEnrichFunction(field: String): Option[RootEnrichFunc[_]] = {
    field match {
      case ByteContentLoad.Field => Some(HttpPayload)
      case _ => None
    }
  }
}

object TempasWaybackRecord {
  implicit def recordToTempasYearResult(record: TempasWaybackRecord): TempasYearResult = record.get
}