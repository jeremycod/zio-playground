package com.playground.dss.omp.graphql.utils

import io.circe.parser.parse
object LegacyHelper {
  def fromLegacyOptionString(legacy: Option[String]): Map[String, String] =
    legacy.map(fromLegacyString).getOrElse(Map.empty)

  def fromLegacyString(legacy: String): Map[String, String] =
    parse(legacy).flatMap(_.as[Map[String, String]]).toOption
      .getOrElse(Map.empty)

  def findInLegacyString(legacy: Option[String], key: String): Option[String] =
    fromLegacyOptionString(legacy).get(key)

}
