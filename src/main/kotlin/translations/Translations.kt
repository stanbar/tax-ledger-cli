package translations

object Translations {
    val translations: MutableMap<String, String> = HashMap(5)

    init {
        translations.put("+income", "Otrzymanie środków")
        translations.put("-pay_for_currency", "Zakup")
        translations.put("-fee", "Prowizja")
        translations.put("+currency_transaction", "Sprzedaż")
        translations.put("-withdraw", "Wypłata środków")
    }

    fun translate(operationType: String) = translations[operationType]
}