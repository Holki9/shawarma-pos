package com.holki9.shawarmapos.model

object DefaultMenu {
    val items: List<MenuItem> = listOf(
        // Основные блюда (списывают лаваш или булочки со склада)
        MenuItem("sh_classic", "Шаурма классическая", 220.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),
        MenuItem("sh_shrimp", "Шаурма с креветками", 320.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),
        MenuItem("sh_pita", "Шаурма в пите", 230.0, MenuGroup.MAIN, StockType.NONE, "Пита"),
        MenuItem("doner_big", "Дёнер Большой", 280.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_small", "Дёнер Маленький", 210.0, MenuGroup.MAIN, StockType.BUN_SMALL, "Булка Мал."),

        // Добавки
        MenuItem("add_cheese", "+ Сыр", 35.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_cheese2", "+ Двойной сыр", 65.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_jalapeno", "+ Халапеньо", 35.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_mushrooms", "+ Грибы", 35.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_fries_in", "+ Картошка", 35.0, MenuGroup.ADDON, StockType.NONE),

        // Закуски и Напитки
        MenuItem("sn_fries", "Картошка фри", 130.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_rustic", "Картошка по-деревенски", 150.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_nuggets", "Наггетсы", 160.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_cheesesticks", "Сырные палочки", 180.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_rings", "Луковые кольца", 150.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_ayran", "Айран", 80.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_tea", "Чай", 50.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_coffee", "Кофе", 100.0, MenuGroup.SNACK, StockType.NONE)
    )
}
