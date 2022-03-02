package com.appdev.eateryblueandroid.util

fun formatLocation(location: String?): String {
    if (location?.contains("Dining Office") == true) return "Cornell Dining Office"
    else if (location?.contains("Gates Hall Snack") == true) return "Gates Hall Vending Machine"
    else if (location?.contains("Duffield Snack") == true) return "Duffield Hall Vending Machine"
    else if (location?.contains("Uris Snack") == true) return "Uris Hall Vending Machine"
    else if (location?.contains("Statler Terrace") == true) return "Terrace Restaurant"
    else if (location?.contains("Big Red Barn") == true) return "Big Red Barn"
    else if (location?.contains("Duffield") == true) return "Mattin's Cafe"
    else if (location?.contains("Okenshield's") == true) return "Okenshield's"
    else if (location?.contains("Trillium") == true) return "Trillium"
    else if (location?.contains("Statler Macs") == true) return "Mac's Cafe"
    else if (location?.contains("Robert Purcell") == true) return "Robert Purcell Marketplace Eatery"
    else if (location?.contains("Jansens Market") == true) return "Jansen's Marketplace"
    else if (location?.contains("NorthStar") == true) return "North Star Dining Room"
    else return location ?: ""
}

fun removeSpecialCharacters(sequence: String): String {
    val validChars = setOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )
    return String(sequence.map {
        it.lowercaseChar()
    }.filter { it in validChars }.toCharArray())
}