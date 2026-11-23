package pe.edu.upeu

expect fun showOrderNotification(title: String, body: String)

fun orderItemsPhrase(details: String): String {
    if (details.isBlank()) return ""
    return details.split(", ").map { part ->
        val core = part.trim().substringBefore(" ($")
        val pieces = core.split("x ", limit = 2)
        if (pieces.size == 2 && pieces[0].all { it.isDigit() }) {
            "${pieces[1].trim()}, cantidad ${pieces[0]}"
        } else {
            part.trim()
        }
    }.filter { it.isNotBlank() }.joinToString(" y ")
}

fun orderItemsPhrase(items: List<Pair<String, Int>>): String {
    return items
        .filter { it.first.isNotBlank() }
        .joinToString(" y ") { (name, qty) -> "$name, cantidad $qty" }
}
