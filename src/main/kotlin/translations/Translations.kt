package translations

object Translations{
    val translations : MutableMap<String,String> = HashMap()
    init {
        translations.put("-pay_for_currency", "Zapłata za zakup waluty")
        translations.put("-fee", "Prowizja od transakcji")
        translations.put("+currency_transaction", "Zakup waluty")
    }
}