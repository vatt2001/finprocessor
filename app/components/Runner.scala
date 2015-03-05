package components

import components.importer.{ParsedRow, DataParser}

object Runner {
	def run() {
		val data =
			"""
			  |1.10; 400; ЕИ; телефон Химки; сЯД
			  |1.10; 370; ЕИ; интернет Химки 10; сПСБ
			  |1.10; 2000; коммуналка/электроэнергия; сАБ
			  |1.10; 10810; коммуналка/квартплата; Перово 07-09; сАБ
			  |1.10; 3610; ЕИ; квартплата Химки 08; сАБ;; 2995
			  |3.10; 760; продукты
			  |3.10; -80000; ЗП; нал за 09 (еще должны ~2.5К); кЗП2
			  |4.10; 420; здоровье/лекарства; гомеопатия
			  |4.10; 630; отдых; с Дашей в граблях
			  |5.10; -10000; -;; кЗП; кЗП2; 10000; 120000
			  |5.10; -5000; -;;; кЗП2; 5350; 115000
			  |5.10; -40000; -; инвестиции от ЗП за 08-09; кБ3; кЗП2; 152000; 75000
			  |5.10; -55500; -; на дачу от ЗП за 09; кК; кЗП2; 60000; 19500
			  |6.10; 525; транспорт; маршрутка на 5 дней
			  |6.10; 580; еда; конфеты и бананы
			  |6.10; -10205; ЗП; безнал часть за 09; сПСБ;; 10210
			  |6.10; 615; продукты
			  |6.10; 1000; машина/другое; два штрафа за превышение на 20-40 км/ч; сАБ
			  |7.10; -8000; -; на оплату отдыха в санатории;; кЗП2
			  |7.10; 8300; отдых; в ДО Подмосковье в ноябре
			  |8.10; 710; продукты; творог
			  |9.10; 150; подарки; цветы Даше
			  |11.10; 300; отдых; в граблях с Дашей
			""".stripMargin

    val parser = new DataParser;

    data.split("\n").filter(!_.trim.isEmpty).foreach{ rowString =>
      parser.parseRow(rowString) match {
        case ParsedRow(transaction, srcBalance, dstBalance) =>
          if (transaction.isDefined) transaction.get.dao.insert(transaction.get.withNewId)
          if (srcBalance.isDefined) srcBalance.get.dao.insert(srcBalance.get.withNewId)
          if (dstBalance.isDefined) dstBalance.get.dao.insert(dstBalance.get.withNewId)
      }
    }
	}
}
