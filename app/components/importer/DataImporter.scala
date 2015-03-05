package components.importer

class DataImporter {

  def splitData(data: String): Seq[String] = {
    data.split("\n").filter(!_.trim.isEmpty)
  }

  def parseData(data: String): Seq[ParsedRow] = {
    val parser = new DataParser

    splitData(data).map(rowString => parser.parseRow(rowString))
  }

  def importData(data: String) {

    parseData(data).foreach{ parsedRow =>
      parsedRow match {
        case ParsedRow(transaction, srcBalance, dstBalance) =>
          if (transaction.isDefined) transaction.get.dao.insert(transaction.get.withNewId)
          if (srcBalance.isDefined) srcBalance.get.dao.insert(srcBalance.get.withNewId)
          if (dstBalance.isDefined) dstBalance.get.dao.insert(dstBalance.get.withNewId)
      }
    }
  }
}
