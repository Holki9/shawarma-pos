package com.holki9.shawarmapos.model

enum class MenuGroup {
    MAIN,
    ADDON,
    SNACK
}

enum class StockType {
    NONE,
    LAVASH,
    BUN_BIG,
    BUN_SMALL
}

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val group: MenuGroup,
    val stockType: StockType = StockType.NONE,
    val badge: String? = null
)
