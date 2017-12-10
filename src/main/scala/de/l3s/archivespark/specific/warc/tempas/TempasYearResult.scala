package de.l3s.archivespark.specific.warc.tempas

import scala.util.Try

case class TempasYearResult (url: String, year: Int)

object TempasYearResult {
  def resultsFromTsv(str: String): Seq[TempasYearResult] = {
    val split = str.split("\t")
    val url = split.head
    val years = split.drop(1).flatMap(year => Try{year.toInt}.toOption)
    if (url.nonEmpty && years.nonEmpty) {
      years.map(year => TempasYearResult(url, year))
    } else Seq.empty
  }
}