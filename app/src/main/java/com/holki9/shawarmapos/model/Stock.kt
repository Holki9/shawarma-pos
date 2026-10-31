package com.holki9.shawarmapos.model

data class Stock(
    val lavashWhite: Int = 50,
    val lavashOrange: Int = 0,
    val bunBig: Int = 20,
    val bunSmall: Int = 40
) {
    val totalLavash: Int get() = lavashWhite + lavashOrange
    val totalBuns: Int get() = bunBig + bunSmall
}
