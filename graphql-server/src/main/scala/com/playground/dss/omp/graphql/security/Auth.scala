package com.playground.dss.omp.graphql.security

import cats.implicits.{catsSyntaxEitherId, catsSyntaxOptionId}
//import com.playground.dss.omp.{AuthenticationCommon, AuthenticationFailure}
//import com.nimbusds.jose.crypto.DirectDecrypter
//import com.nimbusds.jose.jwk.OctetSequenceKey
import zio._
import zio.http._

import javax.crypto.SecretKey
case object Unauthorized extends RuntimeException("Unauthorized")

final case class SessionScope(name: String, profile: String)

trait Auth {
  type Unauthorized = Unauthorized.type

  def currentSessionScope: IO[AuthenticationFailure, SessionScope]
  def setScope(scope: Option[SessionScope]): UIO[Unit]
}

//class Authenticator(decrypter: DirectDecrypter) extends AuthenticationCommon(decrypter)
/*object Authenticator {
  def make(jwkKey: String): Authenticator = {
    val jwk = OctetSequenceKey.parse(jwkKey)
    val secretKey: SecretKey = jwk.toSecretKey
    val decrypter = new DirectDecrypter(secretKey)
    new Authenticator(decrypter)
  }
}*/
case class AuthenticationFailure(msg: String) extends Throwable
case class IdentityProvider(name: String, profile: String)
object Auth {

  private def bearerPrefix(tokenString: String): String =
    if (tokenString.startsWith("Bearer ")) tokenString.substring(7) else tokenString

  private def bearerTokenFromZioRequest(request: Request): Either[AuthenticationFailure, String] = {
    request.headers.get(Header.Authorization).map(_.renderedValue).map(bearerPrefix).fold(AuthenticationFailure(
      "No token found in request").asLeft[String])(_.asRight[AuthenticationFailure])
  }

  val http: ULayer[Auth] = ZLayer.scoped {
    FiberRef
      .make[Option[SessionScope]](None)
      .map { ref =>
        new Auth {
          def currentSessionScope: IO[AuthenticationFailure, SessionScope] =
            ref.get.flatMap {
              case Some(v) => ZIO.succeed(v)
              case None => ZIO.fail(AuthenticationFailure("Not authorized"))
            }

          def setScope(scope: Option[SessionScope]): UIO[Unit] = ref.set(scope)
        }
      }
  }
  def middleware(): HandlerAspect[Auth, Unit] =
    Middleware.customAuthZIO { req =>
      val identityProvider = if (true) Right(IdentityProvider("test@dev.zio","main"))
      else Left(AuthenticationFailure(""))
      //for {
        //header <- bearerTokenFromZioRequest(req)
        /*token <- authenticator.decryptToken(header)
        idp <- authenticator.safeAccess(token, java.lang.System.currentTimeMillis())*/
     // } yield IdentityProvider("test@dev.zio","main")
      identityProvider match {
        case Right(idp) => ZIO.serviceWithZIO[Auth](
            _.setScope(SessionScope(idp.name, idp.profile).some)).as(true)
        case Left(authenticationFailure) => ZIO.succeed(Response.text(authenticationFailure.msg)
            .status(Status.Unauthorized)).as(false)
      }

    }

}
