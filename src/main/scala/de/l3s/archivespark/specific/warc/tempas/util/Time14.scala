package de.l3s.archivespark.specific.warc.tempas.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoUnit, TemporalUnit}

import scala.util.Try

object Time14 {
  val Formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

  def timeFrom14(time14: String) = LocalDateTime.parse(time14, Formatter)
  def timeTo14(time: LocalDateTime) = time.format(Formatter)

  def pad(i: Int, len: Int) = i.toString.reverse.padTo(len, '0').reverse

  def prefixInt(str: String, n: Int, default: Int = 0): (String, String, Boolean) = {
    val (prefix, remaining) = (str.take(n), str.drop(n))
    var defaulted = false
    val int = Try {prefix.toInt}.toOption match {
      case Some(x) => x
      case None =>
        defaulted = true
        default
    }
    (pad(int, n), remaining, defaulted)
  }

  def midTime14FromPrefix(prefix: String): String = {
    if (prefix.length >= 14) return prefix.take(14)
    val (year, r1, d1) = prefixInt(prefix, 4)
    val (month, r2, d2) = prefixInt(r1, 2, if (d1) 1 else 7)
    val (day, r3, d3) = prefixInt(r2, 2, if (d2) 1 else 16)
    val (hours, r4, d4) = prefixInt(r3, 2, if (d3) 0 else 13)
    val (minutes, r5, d5) = prefixInt(r4, 2, if (d4) 0 else 31)
    val (seconds, _, _) = prefixInt(r5, 2, if (d5) 0 else 31)
    year + month + day + hours + minutes + seconds
  }

  def minMaxTimeFrom14Prefix(prefix: String, tolerance: Long = 0, toleranceUnit: TemporalUnit = null): (LocalDateTime, LocalDateTime) = {
    val unit = temporalUnit(prefix)
    val time = timeFrom14(midTime14FromPrefix(prefix))
    val min = unit match {
      case ChronoUnit.YEARS => time.withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
      case ChronoUnit.MONTHS => time.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
      case ChronoUnit.DAYS => time.withHour(0).withMinute(0).withSecond(0)
      case ChronoUnit.HOURS => time.withMinute(0).withSecond(0)
      case ChronoUnit.MINUTES => time.withSecond(0)
      case _ => return (time, time)
    }
    val max = min.plus(1, unit)
    val tolUnit = if (toleranceUnit == null) unit else toleranceUnit
    (min.minus(tolerance, tolUnit), max.plus(tolerance, tolUnit))
  }

  def timeTo14prefix(time: LocalDateTime, unit: TemporalUnit): String = unit match {
    case ChronoUnit.YEARS => pad(time.getYear, 4)
    case ChronoUnit.MONTHS => timeTo14prefix(time, ChronoUnit.YEARS) + pad(time.getMonthValue, 2)
    case ChronoUnit.DAYS => timeTo14prefix(time, ChronoUnit.MONTHS) + pad(time.getDayOfMonth, 2)
    case ChronoUnit.HOURS => timeTo14prefix(time, ChronoUnit.DAYS) + pad(time.getHour, 2)
    case ChronoUnit.MINUTES => timeTo14prefix(time, ChronoUnit.HOURS) + pad(time.getMinute, 2)
    case _ => timeTo14prefix(time, ChronoUnit.MINUTES) + pad(time.getSecond, 2)
  }

  def betweenTimes(time: LocalDateTime, min: LocalDateTime, max: LocalDateTime, unit: TemporalUnit): Boolean = unit match {
    case ChronoUnit.YEARS => time.getYear >= min.getYear && time.getYear <= max.getYear
    case ChronoUnit.MONTHS => betweenTimes(time, min, max, ChronoUnit.YEARS) && time.getMonthValue >= min.getMonthValue && time.getMonthValue <= max.getMonthValue
    case ChronoUnit.DAYS => betweenTimes(time, min, max, ChronoUnit.MONTHS) && time.getDayOfMonth >= min.getDayOfMonth && time.getDayOfMonth <= max.getDayOfMonth
    case ChronoUnit.HOURS => betweenTimes(time, min, max, ChronoUnit.DAYS) && time.getHour >= min.getHour && time.getHour <= max.getHour
    case ChronoUnit.MINUTES => betweenTimes(time, min, max, ChronoUnit.HOURS) && time.getMinute >= min.getMinute && time.getMinute <= max.getMinute
    case _ => betweenTimes(time, min, max, ChronoUnit.MINUTES) && time.getSecond >= min.getSecond && time.getSecond <= max.getSecond
  }

  def temporalUnit(time14prefix: String): TemporalUnit = time14prefix.length match {
    case l if l > 12 => ChronoUnit.SECONDS
    case l if l > 10 => ChronoUnit.MINUTES
    case l if l > 8 => ChronoUnit.HOURS
    case l if l > 6 => ChronoUnit.DAYS
    case l if l > 4 => ChronoUnit.MONTHS
    case _ => ChronoUnit.YEARS
  }
}