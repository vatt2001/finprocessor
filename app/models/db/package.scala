package models

import org.joda.time.{LocalDate, DateTime}
import java.sql.{Date, Timestamp}

package object db {
	import java.util.Currency
  import slick.driver.SQLiteDriver.simple._

	implicit val currencyMapper = {
		MappedColumnType.base[Currency, String](_.toString, Currency.getInstance)
	}

  implicit def dateTimeTypeMapper =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime)
    )

  implicit def localDateTypeMapper =
    MappedColumnType.base[LocalDate, Date](
      ld => new Date(ld.toDate.getTime),
      d => new LocalDate(d.getTime)
    )
}
