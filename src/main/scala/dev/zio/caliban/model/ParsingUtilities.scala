package dev.zio.caliban.model

import com.typesafe.scalalogging.Logger

object ParsingUtilities {

  def extractSingleValue[T](
      key: String,
      map: Map[String, Set[String]],
      safeExtraction: String => Option[T],
      required: Boolean
  )(implicit
      logger: Logger
  ): Option[T] = {
    (map.get(key), required) match {
      case (Some(value), _) => value.toSeq match {
          case Seq(value) => safeExtraction(value)
          case Seq() =>
            logger.warn(s"Expected a value but got an empty Set for key $key")
            None
          case s =>
            logger.warn(s"Expected a single value in the set but got ${s.toString}, rejecting request")
            None
        }
      case (None, true) =>
        logger.warn(s"Expected a single value for $key but unable to find one in the supplied map of ${map.toString}")
        None
      case _ => None
    }
  }
}
