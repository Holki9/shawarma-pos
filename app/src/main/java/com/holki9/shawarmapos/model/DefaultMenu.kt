package com.holki9.shawarmapos.model

object DefaultMenu {
    const val CURRENT_VERSION = 2

    val items: List<MenuItem> = listOf(
        // === ШАУРМА И ДЁНЕРЫ (ОСНОВНЫЕ БЛЮДА) ===
        MenuItem("sh_lavash_white", "Шаурма в обычном лаваше", 350.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),
        MenuItem("sh_lavash_cheese", "Шаурма в сырном лаваше", 350.0, MenuGroup.MAIN, StockType.LAVASH, "Сырный"),
        MenuItem("sh_xl", "Шаурма XL", 450.0, MenuGroup.MAIN, StockType.LAVASH, "XL"),
        MenuItem("sh_pita", "Шаурма в пите", 360.0, MenuGroup.MAIN, StockType.NONE, "Пита"),
        MenuItem("sh_shrimp", "Шаурма с креветкой", 450.0, MenuGroup.MAIN, StockType.LAVASH, "Креветка"),
        MenuItem("sh_cheese", "Шаурма + сыр", 400.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),
        MenuItem("sh_cheese_mush", "Шаурма + сыр + грибы", 450.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),
        MenuItem("sh_combo_all", "Шаурма + сыр + грибы + халапеньо", 500.0, MenuGroup.MAIN, StockType.LAVASH, "Лаваш"),

        // Дёнеры
        MenuItem("doner_big", "Донер большой", 360.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_cheese", "Донер большой с сыром", 410.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_mush", "Донер большой с грибами", 410.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_jalapeno", "Донер большой с халапеньо", 410.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_cheese_mush", "Донер большой + сыр + грибы", 460.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_jal_cheese", "Донер большой + халапеньо + сыр", 460.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_jal_mush", "Донер большой + халапеньо + грибы", 460.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_big_all", "Донер большой + сыр + халапеньо + грибы", 510.0, MenuGroup.MAIN, StockType.BUN_BIG, "Булка Бол."),
        MenuItem("doner_small", "Донер маленький", 180.0, MenuGroup.MAIN, StockType.BUN_SMALL, "Булка Мал."),

        // === ДОБАВКИ ===
        MenuItem("add_cheese", "+ Сыр", 50.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_mushrooms", "+ Грибы", 50.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_jalapeno", "+ Халапеньо", 50.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_fries_100", "+ Картошка фри 100г", 100.0, MenuGroup.ADDON, StockType.NONE),
        MenuItem("add_fries_150", "+ Картошка фри 150г", 150.0, MenuGroup.ADDON, StockType.NONE),

        // === ЗАКУСКИ И НАПИТКИ ===
        MenuItem("sn_nuggets", "Наггетсы (от 4 шт.)", 100.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_cheesesticks", "Сырные палочки (от 4 шт.)", 100.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_rings", "Луковые кольца (от 4 шт.)", 100.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("sn_rustic", "Картошка по-деревенски 100г", 100.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_tea", "Чай", 60.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_coffee", "Кофе", 70.0, MenuGroup.SNACK, StockType.NONE),
        MenuItem("dr_ayran", "Айран 0.5 л", 120.0, MenuGroup.SNACK, StockType.NONE)
    )
}
