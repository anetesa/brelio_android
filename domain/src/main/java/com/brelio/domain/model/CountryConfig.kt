package com.brelio.domain.model

data class CountryConfig(
    val currency: String,
    val currencyDisplay: String,
    val locale: String,
    val vatRate: Double,
    val phonePrefix: String,
    val stripeCountry: String,
) {
    companion object {
        private val configs = mapOf(
            "PL" to CountryConfig("pln", "PLN", "pl-PL", 0.23, "+48", "PL"),
            "CZ" to CountryConfig("czk", "CZK", "cs-CZ", 0.21, "+420", "CZ"),
            "SK" to CountryConfig("eur", "EUR", "sk-SK", 0.20, "+421", "SK"),
            "HU" to CountryConfig("huf", "HUF", "hu-HU", 0.27, "+36", "HU"),
        )

        fun forCountry(code: String): CountryConfig = configs[code.uppercase()] ?: configs["PL"]!!
    }
}
