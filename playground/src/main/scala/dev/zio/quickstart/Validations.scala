/*

package dev.zio.quickstart
import dev.zio.quickstart.NewtypeRefinedOps.validate
import zio.prelude._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._

import java.time.LocalDateTime
import java.util.Currency
import squants.market._
import zio.prelude._
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import shapeless.HNil


type AccountNoString = String Refined AllOf[
  MaxSize[W.`12`.T] ::
    MinSize[W.`5`.T] ::
    HNil
]
case class AccountNo(value: AccountNoString)

case class AccountName(value: NonEmptyString)

sealed abstract class AccountType(val entryName: String)

final case class Account private (
                                   no: AccountNo,
                                   name: AccountName,
                                   dateOfOpen: LocalDateTime,
                                   dateOfClose: Option[LocalDateTime],
                                   accountType: AccountType,
                                   baseCurrency: Currency,
                                   tradingCurrency: Option[Currency],
                                   settlementCurrency: Option[Currency]
                                 )

object NewtypeRefinedOps {
  import io.estatico.newtype.Coercible
  import io.estatico.newtype.ops._

  final class NewtypeRefinedPartiallyApplied[A] {
    def apply[T, P](raw: T)(implicit
                            c: Coercible[Refined[T, P], A],
                            v: Validate[T, P]
    ): Validation[String, A] =
      refineV[P](raw)
        .fold(s => Validation.fail(s), v => Validation.succeed(v.coerce[A]))
  }

  def validate[A]: NewtypeRefinedPartiallyApplied[A] =
    new NewtypeRefinedPartiallyApplied[A]
}

object Validations {

  import io.estatico.newtype.Coercible
  import io.estatico.newtype.ops._
/*  private[model] def validateAccountNo(no: String): Validation[String, AccountNo] =
    validate[AccountNo](no).mapError(s =>
      s"Account No has to be at least 5 characters long: found $no(root cause $s)"
    )

  private[model] def validateAccountName(name: String): Validation[String, AccountName] =
    validate[AccountName](name)
      .mapError(s => s"Account Name cannot be blank (root cause: $s")*/

  private def validateOpenCloseDate(
                                     od: LocalDateTime,
                                     cd: Option[LocalDateTime]
                                   ): Validation[String, (LocalDateTime, Option[LocalDateTime])] =
    cd.map { c =>
      if (c isBefore od)
        Validation.fail(s"Close date [$c] cannot be earlier than open date [$od]")
      else Validation.succeed((od, cd))
    }.getOrElse { Validation.succeed((od, cd)) }

  private def validateAccountAlreadyClosed(
                                            a: Account
                                          ): Validation[String, Account] = {
    if (a.dateOfClose.isDefined)
      Validation.fail(s"Account ${a.no} is already closed")
    else Validation.succeed(a)
  }

  private def validateCloseDate(
                                 a: Account,
                                 cd: LocalDateTime
                               ): Validation[String, LocalDateTime] = {
    if (cd isBefore a.dateOfOpen)
      Validation.fail(s"Close date [$cd] cannot be earlier than open date [${a.dateOfOpen}]")
    else Validation.succeed(cd)
  }
}
 */
