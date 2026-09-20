package com.example.ebookstore.domain.model

/**
 * Represents a country available for shipping.
 *
 * @param code           ISO 3166-1 alpha-2 country code, e.g. "IN".
 * @param name           Display name, e.g. "India".
 * @param currencyCode   ISO 4217 currency code, e.g. "INR".
 * @param currencySymbol Symbol to display before prices, e.g. "₹".
 * @param phonePrefix    International dialling prefix without "+", e.g. "91".
 * @param postalPattern  Regex describing a valid postal / ZIP code for this country.
 *                       Use ".*" for countries with no fixed format.
 * @param postalLabel    Human-readable label for the postal-code field, e.g. "PIN Code".
 * @param phoneDigits    Expected digit count for a local phone number (0 = no restriction).
 */
data class CountryData(
    val code: String,
    val name: String,
    val currencyCode: String,
    val currencySymbol: String,
    val phonePrefix: String,
    val postalPattern: String,
    val postalLabel: String = "Postal Code",
    val phoneDigits: Int = 0,
) {
    fun formatPrice(amount: Double): String =
        "$currencySymbol${"%.2f".format(amount)}"

    fun isValidPostal(value: String): Boolean =
        postalPattern == ".*" || value.matches(Regex(postalPattern))

    fun isValidPhone(value: String): Boolean =
        if (phoneDigits > 0) value.matches(Regex("\\d{$phoneDigits}"))
        else value.matches(Regex("\\d{6,15}"))
}

/**
 * Catalogue of countries available for shipping.
 *
 * Each entry includes the currency symbol used to render prices in the
 * Checkout, Payment, and Order Confirmation screens.
 *
 * Add entries here to extend global coverage — no other files need changing.
 */
object CountryCatalogue {

    val all: List<CountryData> = listOf(
        CountryData("IN",  "India",          "INR", "₹",  "91",  "\\d{6}",               "PIN Code",      10),
        CountryData("US",  "United States",  "USD", "$",  "1",   "\\d{5}(-\\d{4})?",     "ZIP Code",       10),
        CountryData("GB",  "United Kingdom", "GBP", "£",  "44",  "[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}", "Postcode", 10),
        CountryData("EU",  "Euro Zone",      "EUR", "€",  "",    ".*",                    "Postal Code",    0),
        CountryData("AU",  "Australia",      "AUD", "A$", "61",  "\\d{4}",               "Postcode",       9),
        CountryData("CA",  "Canada",         "CAD", "C$", "1",   "[A-Z]\\d[A-Z] ?\\d[A-Z]\\d", "Postal Code", 10),
        CountryData("AE",  "UAE",            "AED", "د.إ","971", ".*",                    "Postal Code",    9),
        CountryData("SG",  "Singapore",      "SGD", "S$", "65",  "\\d{6}",               "Postal Code",    8),
        CountryData("JP",  "Japan",          "JPY", "¥",  "81",  "\\d{3}-?\\d{4}",       "Postal Code",    10),
        CountryData("DE",  "Germany",        "EUR", "€",  "49",  "\\d{5}",               "Postleitzahl",   10),
        CountryData("FR",  "France",         "EUR", "€",  "33",  "\\d{5}",               "Code Postal",    9),
        CountryData("BR",  "Brazil",         "BRL", "R$", "55",  "\\d{5}-?\\d{3}",       "CEP",            10),
        CountryData("MX",  "Mexico",         "MXN", "MX$","52",  "\\d{5}",               "Código Postal",  10),
        CountryData("ZA",  "South Africa",   "ZAR", "R",  "27",  "\\d{4}",               "Postal Code",    9),
        CountryData("NG",  "Nigeria",        "NGN", "₦",  "234", "\\d{6}",               "Postal Code",    10),
        CountryData("KR",  "South Korea",    "KRW", "₩",  "82",  "\\d{5}",               "Postal Code",    10),
        CountryData("ID",  "Indonesia",      "IDR", "Rp", "62",  "\\d{5}",               "Kode Pos",       10),
        CountryData("PH",  "Philippines",    "PHP", "₱",  "63",  "\\d{4}",               "Postal Code",    10),
        CountryData("NZ",  "New Zealand",    "NZD", "NZ$","64",  "\\d{4}",               "Postcode",       9),
        CountryData("CH",  "Switzerland",    "CHF", "Fr", "41",  "\\d{4}",               "PLZ",            9),
    ).sortedBy { it.name }

    /** Default country shown when the form is first opened. */
    val default: CountryData = all.first { it.code == "IN" }

    /** Lookup by ISO code; falls back to [default]. */
    fun byCode(code: String): CountryData = all.firstOrNull { it.code == code } ?: default

    /**
     * Rich city metadata used by the autocomplete field.
     *
     * [display] is shown in the dropdown ("City, Country").
     * [cityName] is the bare city name written into the City field.
     * [state] is auto-filled into the State / Region field.
     * [postalCode] is a representative sample postal code auto-filled into the postal field.
     */
    data class CityInfo(
        val display: String,
        val cityName: String,
        val state: String,
        val postalCode: String,
    )

    val cityInfos: List<CityInfo> = listOf(
        // ── India ────────────────────────────────────────────────────────────
        CityInfo("Mumbai, India",        "Mumbai",        "Maharashtra",       "400001"),
        CityInfo("Delhi, India",         "Delhi",         "Delhi",             "110001"),
        CityInfo("Bengaluru, India",     "Bengaluru",     "Karnataka",         "560001"),
        CityInfo("Hyderabad, India",     "Hyderabad",     "Telangana",         "500001"),
        CityInfo("Chennai, India",       "Chennai",       "Tamil Nadu",        "600001"),
        CityInfo("Kolkata, India",       "Kolkata",       "West Bengal",       "700001"),
        CityInfo("Pune, India",          "Pune",          "Maharashtra",       "411001"),
        CityInfo("Ahmedabad, India",     "Ahmedabad",     "Gujarat",           "380001"),
        CityInfo("Jaipur, India",        "Jaipur",        "Rajasthan",         "302001"),
        CityInfo("Surat, India",         "Surat",         "Gujarat",           "395001"),
        CityInfo("Lucknow, India",       "Lucknow",       "Uttar Pradesh",     "226001"),
        CityInfo("Kanpur, India",        "Kanpur",        "Uttar Pradesh",     "208001"),
        CityInfo("Nagpur, India",        "Nagpur",        "Maharashtra",       "440001"),
        CityInfo("Bhopal, India",        "Bhopal",        "Madhya Pradesh",    "462001"),
        CityInfo("Indore, India",        "Indore",        "Madhya Pradesh",    "452001"),
        CityInfo("Visakhapatnam, India", "Visakhapatnam", "Andhra Pradesh",    "530001"),
        // ── United States ────────────────────────────────────────────────────
        CityInfo("New York, USA",        "New York",      "NY",                "10001"),
        CityInfo("Los Angeles, USA",     "Los Angeles",   "CA",                "90001"),
        CityInfo("Chicago, USA",         "Chicago",       "IL",                "60601"),
        CityInfo("Houston, USA",         "Houston",       "TX",                "77001"),
        CityInfo("Phoenix, USA",         "Phoenix",       "AZ",                "85001"),
        CityInfo("Philadelphia, USA",    "Philadelphia",  "PA",                "19101"),
        CityInfo("San Antonio, USA",     "San Antonio",   "TX",                "78201"),
        CityInfo("San Diego, USA",       "San Diego",     "CA",                "92101"),
        CityInfo("Dallas, USA",          "Dallas",        "TX",                "75201"),
        CityInfo("San Jose, USA",        "San Jose",      "CA",                "95101"),
        CityInfo("Austin, USA",          "Austin",        "TX",                "73301"),
        CityInfo("Seattle, USA",         "Seattle",       "WA",                "98101"),
        CityInfo("Boston, USA",          "Boston",        "MA",                "02101"),
        CityInfo("Denver, USA",          "Denver",        "CO",                "80201"),
        CityInfo("Atlanta, USA",         "Atlanta",       "GA",                "30301"),
        CityInfo("Miami, USA",           "Miami",         "FL",                "33101"),
        // ── United Kingdom ───────────────────────────────────────────────────
        CityInfo("London, UK",           "London",        "England",           "EC1A 1BB"),
        CityInfo("Birmingham, UK",       "Birmingham",    "England",           "B1 1BB"),
        CityInfo("Manchester, UK",       "Manchester",    "England",           "M1 1AE"),
        CityInfo("Leeds, UK",            "Leeds",         "England",           "LS1 1BA"),
        CityInfo("Glasgow, UK",          "Glasgow",       "Scotland",          "G1 1AB"),
        CityInfo("Liverpool, UK",        "Liverpool",     "England",           "L1 0AB"),
        CityInfo("Bristol, UK",          "Bristol",       "England",           "BS1 1AA"),
        CityInfo("Sheffield, UK",        "Sheffield",     "England",           "S1 1AA"),
        // ── Europe ───────────────────────────────────────────────────────────
        CityInfo("Paris, France",        "Paris",         "Île-de-France",     "75001"),
        CityInfo("Berlin, Germany",      "Berlin",        "Berlin",            "10115"),
        CityInfo("Madrid, Spain",        "Madrid",        "Community of Madrid","28001"),
        CityInfo("Rome, Italy",          "Rome",          "Lazio",             "00100"),
        CityInfo("Amsterdam, Netherlands","Amsterdam",    "North Holland",     "1011 AB"),
        CityInfo("Brussels, Belgium",    "Brussels",      "Brussels",          "1000"),
        CityInfo("Vienna, Austria",      "Vienna",        "Vienna",            "1010"),
        CityInfo("Zurich, Switzerland",  "Zurich",        "Zurich",            "8001"),
        CityInfo("Stockholm, Sweden",    "Stockholm",     "Stockholm County",  "111 20"),
        CityInfo("Oslo, Norway",         "Oslo",          "Oslo",              "0150"),
        // ── Asia-Pacific ─────────────────────────────────────────────────────
        CityInfo("Tokyo, Japan",         "Tokyo",         "Tokyo",             "100-0001"),
        CityInfo("Shanghai, China",      "Shanghai",      "Shanghai",          "200001"),
        CityInfo("Beijing, China",       "Beijing",       "Beijing",           "100000"),
        CityInfo("Seoul, South Korea",   "Seoul",         "Seoul",             "04524"),
        CityInfo("Sydney, Australia",    "Sydney",        "NSW",               "2000"),
        CityInfo("Melbourne, Australia", "Melbourne",     "VIC",               "3000"),
        CityInfo("Singapore",            "Singapore",     "Singapore",         "018989"),
        CityInfo("Hong Kong",            "Hong Kong",     "Hong Kong",         "999077"),
        CityInfo("Kuala Lumpur, Malaysia","Kuala Lumpur", "Selangor",          "50000"),
        CityInfo("Bangkok, Thailand",    "Bangkok",       "Bangkok",           "10200"),
        CityInfo("Jakarta, Indonesia",   "Jakarta",       "DKI Jakarta",       "10110"),
        CityInfo("Manila, Philippines",  "Manila",        "Metro Manila",      "1000"),
        // ── Middle East & Africa ─────────────────────────────────────────────
        CityInfo("Dubai, UAE",           "Dubai",         "Dubai",             "00000"),
        CityInfo("Abu Dhabi, UAE",       "Abu Dhabi",     "Abu Dhabi",         "00000"),
        CityInfo("Riyadh, Saudi Arabia", "Riyadh",        "Riyadh Region",     "12271"),
        CityInfo("Cairo, Egypt",         "Cairo",         "Cairo Governorate", "11511"),
        CityInfo("Lagos, Nigeria",       "Lagos",         "Lagos State",       "101001"),
        CityInfo("Nairobi, Kenya",       "Nairobi",       "Nairobi County",    "00100"),
        CityInfo("Johannesburg, South Africa","Johannesburg","Gauteng",         "2000"),
        CityInfo("Cape Town, South Africa","Cape Town",   "Western Cape",      "8001"),
        // ── Americas ─────────────────────────────────────────────────────────
        CityInfo("Toronto, Canada",      "Toronto",       "Ontario",           "M5H 2N2"),
        CityInfo("Vancouver, Canada",    "Vancouver",     "British Columbia",  "V6B 1A1"),
        CityInfo("Montreal, Canada",     "Montreal",      "Quebec",            "H2Y 1C6"),
        CityInfo("São Paulo, Brazil",    "São Paulo",     "São Paulo",         "01310-100"),
        CityInfo("Rio de Janeiro, Brazil","Rio de Janeiro","Rio de Janeiro",   "20040-020"),
        CityInfo("Mexico City, Mexico",  "Mexico City",   "CDMX",              "06600"),
        CityInfo("Buenos Aires, Argentina","Buenos Aires","Buenos Aires",      "C1001"),
    ).sortedBy { it.display }

    /** Flat list of display strings – used for text matching in the dropdown. */
    val cities: List<String> = cityInfos.map { it.display }

    /** Look up the full [CityInfo] for a display string (e.g. "Atlanta, USA"). */
    fun cityInfoFor(display: String): CityInfo? =
        cityInfos.firstOrNull { it.display.equals(display, ignoreCase = true) }

    /**
     * Sample postal codes keyed by country code.
     * Shown as a placeholder when the user picks a country but hasn't typed a city yet.
     */
    val samplePostalByCountry: Map<String, String> = mapOf(
        "IN" to "110001", "US" to "10001", "GB" to "EC1A 1BB",
        "AU" to "2000",   "CA" to "M5H 2N2", "AE" to "00000",
        "SG" to "018989", "JP" to "100-0001", "DE" to "10115",
        "FR" to "75001",  "BR" to "01310-100", "MX" to "06600",
        "ZA" to "2000",   "NG" to "101001", "KR" to "04524",
        "ID" to "10110",  "PH" to "1000",   "NZ" to "6011",
        "CH" to "8001",   "EU" to "1000",
    )
}
