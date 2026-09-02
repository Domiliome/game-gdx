"""Generate SimpleGame project documentation as PDF (Cyrillic via Windows TTF)."""
from pathlib import Path

from fpdf import FPDF

ROOT = Path(__file__).resolve().parent.parent
OUT = Path(__file__).resolve().parent / "SimpleGame-documentation.pdf"
FONTS = Path(r"C:\Windows\Fonts")


class Doc(FPDF):
    def header(self):
        if self.page_no() == 1:
            return
        self.set_font("Body", "", 9)
        self.set_text_color(90, 90, 90)
        self.cell(0, 8, "SimpleGame — документация проекта", align="L")
        self.cell(0, 8, "LibGDX / Java 17", align="R", new_x="LMARGIN", new_y="NEXT")
        self.set_draw_color(200, 200, 200)
        self.line(self.l_margin, self.get_y(), self.w - self.r_margin, self.get_y())
        self.ln(4)
        self.set_text_color(20, 20, 20)

    def footer(self):
        self.set_y(-14)
        self.set_font("Body", "", 9)
        self.set_text_color(120, 120, 120)
        self.cell(0, 8, str(self.page_no()), align="C")


def add_fonts(pdf: Doc) -> None:
    regular = FONTS / "arial.ttf"
    bold = FONTS / "arialbd.ttf"
    italic = FONTS / "ariali.ttf"
    if not regular.exists():
        regular = FONTS / "segoeui.ttf"
        bold = FONTS / "segoeuib.ttf"
        italic = FONTS / "segoeuii.ttf"
    pdf.add_font("Body", "", str(regular))
    pdf.add_font("Body", "B", str(bold if bold.exists() else regular))
    pdf.add_font("Body", "I", str(italic if italic.exists() else regular))


def h1(pdf: Doc, text: str) -> None:
    pdf.ln(2)
    pdf.set_font("Body", "B", 16)
    pdf.set_text_color(25, 25, 25)
    pdf.multi_cell(0, 9, text)
    pdf.set_draw_color(40, 90, 160)
    y = pdf.get_y()
    pdf.line(pdf.l_margin, y, pdf.l_margin + 42, y)
    pdf.ln(4)


def h2(pdf: Doc, text: str) -> None:
    pdf.ln(2)
    pdf.set_font("Body", "B", 13)
    pdf.set_text_color(35, 35, 35)
    pdf.multi_cell(0, 8, text)
    pdf.ln(1)


def para(pdf: Doc, text: str) -> None:
    pdf.set_font("Body", "", 11)
    pdf.set_text_color(30, 30, 30)
    pdf.multi_cell(0, 6.2, text)
    pdf.ln(1.5)


def bullet(pdf: Doc, text: str) -> None:
    pdf.set_font("Body", "", 11)
    pdf.set_text_color(30, 30, 30)
    x = pdf.l_margin
    pdf.set_x(x)
    pdf.cell(6, 6.2, "•")
    pdf.multi_cell(pdf.w - pdf.r_margin - x - 6, 6.2, text)
    pdf.ln(0.4)


def code(pdf: Doc, text: str) -> None:
    pdf.set_fill_color(245, 245, 247)
    pdf.set_font("Body", "", 9.5)
    pdf.set_text_color(40, 40, 40)
    pdf.multi_cell(0, 5.4, text, fill=True)
    pdf.ln(2)


def table(pdf: Doc, headers: list[str], rows: list[list[str]], col_widths: list[float] | None = None) -> None:
    usable = pdf.w - pdf.l_margin - pdf.r_margin
    if col_widths is None:
        col_widths = [usable / len(headers)] * len(headers)
    pdf.set_font("Body", "B", 9.5)
    pdf.set_fill_color(35, 70, 120)
    pdf.set_text_color(255, 255, 255)
    for w, h in zip(col_widths, headers):
        pdf.cell(w, 7.2, h, border=0, fill=True)
    pdf.ln()
    pdf.set_text_color(30, 30, 30)
    pdf.set_font("Body", "", 9.5)
    fill = False
    for row in rows:
        pdf.set_fill_color(240, 244, 248) if fill else pdf.set_fill_color(255, 255, 255)
        line_h = 6.4
        # wrap-aware row height
        heights = []
        for w, cell in zip(col_widths, row):
            heights.append(max(line_h, pdf.get_string_width(cell) / max(w - 2, 1) * line_h + line_h))
        row_h = min(max(heights), 18)
        y0 = pdf.get_y()
        if y0 + row_h > pdf.h - pdf.b_margin:
            pdf.add_page()
            y0 = pdf.get_y()
        x = pdf.l_margin
        for w, cell in zip(col_widths, row):
            pdf.set_xy(x, y0)
            pdf.multi_cell(w, line_h, cell, border=0, fill=True)
            x += w
        pdf.set_xy(pdf.l_margin, y0 + row_h)
        fill = not fill
    pdf.ln(3)


def build() -> None:
    pdf = Doc(format="A4")
    pdf.set_auto_page_break(auto=True, margin=18)
    pdf.set_margins(18, 18, 18)
    add_fonts(pdf)

    # Cover
    pdf.add_page()
    pdf.ln(42)
    pdf.set_font("Body", "B", 28)
    pdf.set_text_color(25, 55, 110)
    pdf.multi_cell(0, 14, "SimpleGame")
    pdf.ln(2)
    pdf.set_font("Body", "", 16)
    pdf.set_text_color(50, 50, 50)
    pdf.multi_cell(0, 9, "Документация проекта")
    pdf.ln(8)
    pdf.set_draw_color(40, 90, 160)
    pdf.set_line_width(0.8)
    pdf.line(pdf.l_margin, pdf.get_y(), pdf.l_margin + 70, pdf.get_y())
    pdf.set_line_width(0.2)
    pdf.ln(10)
    pdf.set_font("Body", "", 12)
    pdf.set_text_color(60, 60, 60)
    para(pdf, "Двумерная tower defense на Java 17 и LibGDX 1.14.2. Общая логика живёт в модуле core, десктопный запуск — в lwjgl3, мобильная сборка — в android.")
    para(pdf, "Версия проекта: 1.0.0. Документ составлен по исходному коду пакета io.github.simple_game.core (сентябрь 2026).")
    pdf.ln(8)
    pdf.set_font("Body", "B", 11)
    pdf.multi_cell(0, 7, "Содержание")
    pdf.set_font("Body", "", 11)
    toc = [
        "1. Обзор и стек",
        "2. Сборка и запуск",
        "3. Архитектура слоёв",
        "4. Игровой мир и сессия",
        "5. Башни и снаряды",
        "6. Враги и волны",
        "7. Экономика, магазин, инвентарь",
        "8. Презентация: экраны, ввод, рендер",
        "9. Расширение игры",
        "10. Текущие ограничения",
    ]
    for item in toc:
        pdf.cell(0, 7, item, new_x="LMARGIN", new_y="NEXT")

    # 1
    pdf.add_page()
    h1(pdf, "1. Обзор и стек")
    para(pdf, "SimpleGame — tower defense с фиксированной сеткой 10×16 клеток. Игрок ставит башни с дороги, переживает 20 волн и может экипировать предметы, выпавшие с врагов. Инвентарь общий для сессий: Main хранит InventoryManager, а каждая партия создаёт свой GameLoop.")
    h2(pdf, "Модули Gradle")
    table(
        pdf,
        ["Модуль", "Роль"],
        [
            ["core", "Вся игровая логика, модель, сервисы и UI"],
            ["lwjgl3", "Десктоп: Lwjgl3Launcher, окно LWJGL3"],
            ["android", "Android: applicationId io.github.simple_game, minSdk 21, targetSdk 34"],
        ],
        [40, 132],
    )
    h2(pdf, "Технологии")
    table(
        pdf,
        ["Компонент", "Значение"],
        [
            ["Язык", "Java 17"],
            ["Движок", "LibGDX 1.14.2"],
            ["Сборка", "Gradle 9.5.1 (wrapper), Android Gradle Plugin 8.9.3"],
            ["LWJGL", "3.4.1"],
            ["Версия приложения", "1.0.0 / versionName 1.0"],
        ],
        [48, 124],
    )
    para(pdf, "Ресурсы лежат в корневом каталоге assets/: тайлы карты, спрайты башен и врагов, карточки магазина, иконки UI и предметов. Список файлов при сборке пишется в assets/assets.txt задачей generateAssetList.")

    # 2
    h1(pdf, "2. Сборка и запуск")
    para(pdf, "Команды выполняются из корня репозитория. На Windows используйте gradlew.bat.")
    code(pdf, "gradlew.bat lwjgl3:run\n"
              "gradlew.bat :core:compileJava\n"
              "gradlew.bat android:installDebug android:run\n"
              "gradlew.bat clean assembleDebug")
    bullet(pdf, "Десктопный main-класс: io.github.simple_game.lwjgl3.Lwjgl3Launcher. Рабочая директория run — папка assets.")
    bullet(pdf, "Логический размер UI — 480×800 (GameViewport). Игровое поле — 480×768 (10×16 клеток по 48 px). Камера мира — FitViewport, UI — ExtendViewport.")
    bullet(pdf, "В gradle.properties логирование Gradle выставлено в quiet, демон отключён.")

    # 3
    h1(pdf, "3. Архитектура слоёв")
    para(pdf, "Зависимости направлены только вниз: Main → presentation → service → model. Модель не импортирует GameLoop, CurrencyManager и GameViewport. Текстуры и анимации загружает презентация.")
    h2(pdf, "Main")
    para(pdf, "io.github.simple_game.core.Main наследует com.badlogic.gdx.Game. При create() создаёт глобальный InventoryManager и открывает MainMenuScreen. Запасной GameLoop больше не строится.")
    h2(pdf, "model")
    bullet(pdf, "CombatWorld — узкий контракт для башен: списки башен, врагов и экипированных предметов. Реализует GameLoop.")
    bullet(pdf, "entity: Entity, враги, башни, снаряды, предметы, GameGrid.")
    bullet(pdf, "movement: RoadPath (вейпоинты + ID вариантов прямой дороги), PathGenerator, WalkMovement, PathType.")
    h2(pdf, "service")
    bullet(pdf, "GameLoop — фасад сессии: сетка, волны, экономика, магазин, сущности, пауза, победа.")
    bullet(pdf, "EntityManager, WaveManager, EnemyFactory, CurrencyManager, ShopService, InventoryManager.")
    h2(pdf, "presentation")
    bullet(pdf, "screen: MainMenuScreen, GameScreen, InventoryScreen.")
    bullet(pdf, "input: CameraGestureService, InteractionService, DragAndDropManager.")
    bullet(pdf, "ui: статус-бар, магазин, апгрейд, окна победы/поражения, инвентарь.")
    bullet(pdf, "view/renderer: GameRenderer, WorldSpriteRenderer, EntityRenderer, HealthBarRenderer, DebugGridRenderer, EnemySprites, TowerSprites.")
    para(pdf, "Башни получают CombatWorld, а не весь цикл. Проверка клетки: GameGrid.isCellBuildable(x, y, towers). Золото и жизни начисляются только в EntityManager при удалении врага: health <= 0 — награда и лут, иначе −1 жизнь.")

    # 4
    h1(pdf, "4. Игровой мир и сессия")
    h2(pdf, "Сетка")
    para(pdf, "GameGrid.CELL_SIZE = 48, COLS = 10, ROWS = 16. Мир 480×768. snap() центрирует координату в клетке. На дорогу и ближе чем 1.1 клетки от другой башни строить нельзя.")
    h2(pdf, "Дорога")
    para(pdf, "При создании GameLoop PathGenerator.generate выбирает случайный PathType: STRAIGHT_FEW_TURNS, MANY_TURNS или WITH_LOOPS. Путь осе-выровненный, параллельные участки разделены минимум одной клеткой земли. Для прямых сегментов: 30% стандартный тайл, 70% один из трёх альтернативных (ID 1–3 на RoadPath). Рендерер подставляет PNG.")
    h2(pdf, "Старт сессии")
    table(
        pdf,
        ["Параметр", "Значение"],
        [
            ["Стартовое золото", "10 000"],
            ["Жизни базы", "20"],
            ["Условие победы", "Волна 20 очищена, врагов на карте нет"],
            ["Пауза", "GameLoop.isPaused — update не идёт"],
        ],
        [55, 117],
    )
    para(pdf, "Каждый кадр GameLoop.update вызывает WaveManager и EntityManager. После смерти врага EntityManager либо добавляет золото и считает лут, либо списывает жизнь, если враг дошёл до конца (onReachedEnd, здоровье ещё больше нуля).")

    # 5
    h1(pdf, "5. Башни и снаряды")
    para(pdf, "Новый тип: класс-наследник Tower + константа в enum TowerType (цена, idle-текстура, скорость снаряда, фабрика). Карточка магазина: card/<имя>_card.png, анимация появления: towers/<имя>_init.png. После постановки башня ~0.48 с не стреляет (окно спавна); кадры рисует TowerSprites.")
    table(
        pdf,
        ["Тип", "Цена", "Урон", "Радиус", "КД, с", "Снаряд"],
        [
            ["ARCHER", "100", "15", "150", "0.6", "Arrow, 400, крит 20% ×2"],
            ["CANNON", "250", "50", "120", "2.0", "CannonBall, 250, взрыв R=70"],
            ["MAGIC", "200", "10", "130", "1.2", "MagicSphere, 320"],
            ["POISON", "175", "5", "140", "1.5", "PoisonBolt, 280 + DoT 8/4с"],
            ["TESLA", "150", "12", "200*", "0.4", "Нет снаряда, луч между парой"],
        ],
        [28, 22, 22, 24, 22, 54],
    )
    para(pdf, "*У Tesla радиус — поиск партнёра. Одна колонна не бьёт; две в радиусе связи образуют молнию (ширина линии 10). Урон на линии наносит только «первичная» из пары, чтобы не бить дважды.")
    h2(pdf, "Улучшения")
    para(pdf, "Максимум 5 уровней. Стоимость апгрейда: (int)(type.cost * 0.5 * currentLevel). Лучник: урон ×1.15, радиус ×1.05, КД ×0.90 за уровень, крит +5% за уровень. Пушка и магия масштабируют свои базы. Яд: урон и DPS × уровень, радиус +10 за уровень, длительность яда +0.5 с.")
    h2(pdf, "Поиск цели и снаряды")
    para(pdf, "Башня держит первую цель в радиусе. Снаряды самонаводятся каждый кадр; если цель умерла, летят в последнюю известную точку. Попадание вызывает takeDamage. Яд вешает applyPoison. Пушка бьёт всех в радиусе 70 от точки взрыва.")

    # 6
    h1(pdf, "6. Враги и волны")
    table(
        pdf,
        ["Класс", "Тир", "HP", "Скорость", "Золото", "Спрайт"],
        [
            ["FastGoblin", "TIER_1_LIGHT (вес 1)", "50", "130", "15", "enemies/goblin.png"],
            ["NormalZombie", "TIER_2_NORMAL (вес 2)", "100", "70", "25", "enemies/zombie.png"],
            ["HeavyOrc", "TIER_3_HEAVY (вес 4)", "300", "40", "60", "enemies/orc.png"],
        ],
        [32, 42, 18, 24, 22, 34],
    )
    para(pdf, "EnemyFactory масштабирует волной: HP × (1 + (wave−1)×0.10), скорость × (1 + min(0.4, (wave−1)×0.03)). Движение — WalkMovement по вейпоинтам RoadPath, независимо от FPS (deltaTime). Есть замедление и яд. Поворот спрайта считается по смещению за кадр; анимацию бега кэширует EnemySprites.")
    h2(pdf, "Волны")
    para(pdf, "Старт следующей волны — кнопка на верхней панели (WaveManager.startNextWave). Пока волна активна, повторный старт игнорируется. Бюджет: 4 + номер_волны×3. С волны 3 с шансом 40% берётся NORMAL, с волны 5 с шансом 20% — HEAVY, иначе LIGHT. Очередь перемешивается, спавн раз в 1.0 с. Волна кончается, когда очередь пуста и на карте нет врагов.")

    # 7
    h1(pdf, "7. Экономика, магазин, инвентарь")
    h2(pdf, "Магазин")
    para(pdf, "ShopService держит 3 случайных разных TowerType. Обновление после покупки или за 20 золота (REFRESH_COST). Покупка: DragAndDropManager проверяет золото, snap клетки и isCellBuildable, затем type.create(..., gameLoop) и spendGold.")
    h2(pdf, "Предметы")
    para(pdf, "Инвентарь живёт в Main и переживает рестарт партии. Три слота экипировки, три слота кузницы, рюкзак. Экипированные предметы каждый кадр вызывают applyEffect на башнях (через CombatWorld.getEquippedItems).")
    table(
        pdf,
        ["Предмет", "Дроп", "Эффект"],
        [
            ["Rusty Arrow", "15%, TIER_1", "Лучник: радиус +40"],
            ["Mana Crystal", "8%, TIER_2", "Маг: КД ×0.8"],
            ["Heavy Core", "10%, TIER_3", "Пушка: урон +25"],
            ["Spring Arrow", "не дропается", "Лучник: КД ×0.75; крафт из 3× Rusty Arrow"],
        ],
        [36, 40, 96],
    )
    para(pdf, "С каждого убитого врага InventoryManager.calculateLootDrop проходит таблицу лута и кладёт в рюкзак первый предмет подходящего тира, если random() <= dropChance (не больше одного предмета за смерть). Кузница: три предмета; Spring Arrow узнаёт рецепт из трёх SharpArrow. Шанс крафта в коде сейчас 100%.")

    # 8
    h1(pdf, "8. Презентация: экраны, ввод, рендер")
    h2(pdf, "Экраны")
    bullet(pdf, "MainMenuScreen — старт партии, переход в инвентарь и выход.")
    bullet(pdf, "GameScreen — создаёт GameLoop(globalInventory), FitViewport мира, ExtendViewport UI, мультиплексор ввода (Stage → жесты камеры → взаимодействие). При ресайзе вычитает высоту статус-бара и магазина, чтобы HUD не перекрывал поле.")
    bullet(pdf, "InventoryScreen — рюкзак, экипировка, кузница; Scene2D DragAndDrop.")
    h2(pdf, "Ввод")
    bullet(pdf, "CameraGestureService — pan, pinch-zoom, инерция, камера ограничена размером мира.")
    bullet(pdf, "InteractionService — выбор башни по тапу, прокси к DragAndDropManager.")
    bullet(pdf, "DragAndDropManager — превью постановки, проверка клетки и золота.")
    h2(pdf, "Рендер")
    para(pdf, "GameRenderer рисует слоями: фон и дорога, башни (idle или init-анимация), отладочная сетка и радиусы при драге, враги и снаряды, HP-бары, призрак перетаскиваемой башни. UI — GameInterface (TopStatusBar, TowerControlPanel, ShopPanel, GameOverWindow, VictoryWindow). Колбэки экрана: открыть инвентарь, рестарт, выход в меню — виджеты не знают Main.")

    # 9
    h1(pdf, "9. Расширение игры")
    h2(pdf, "Новая башня")
    bullet(pdf, "Класс в model.entity.tower, снаряд при необходимости в projectile.")
    bullet(pdf, "Константа TowerType: цена, towers/<id>.png, скорость снаряда, ссылка на конструктор.")
    bullet(pdf, "Ассеты: idle, towers/<id>_init.png, card/<id>_card.png.")
    h2(pdf, "Новый враг")
    bullet(pdf, "Подкласс Enemy + тир в EnemyTier (вес бюджета) + ветка в EnemyFactory.")
    bullet(pdf, "Спрайт-лист 32 px в assets/enemies/, путь в getSpritePath().")
    h2(pdf, "Новый предмет")
    bullet(pdf, "Подкласс Item: имя, описание, шанс, тир дропа, applyEffect, clonePrototype.")
    bullet(pdf, "Зарегистрировать экземпляр в lootTable InventoryManager. Для крафта переопределить checkRecipe.")
    bullet(pdf, "Иконка: items/<имя_в_нижнем_регистре_с_подчёркиваниями>.png.")

    # 10
    h1(pdf, "10. Текущие ограничения")
    bullet(pdf, "GameLoop остаётся широким фасадом: выбранная башня, пауза, победа, магазин, сетка и списки сущностей на одном типе. Рендереры и панели принимают весь объект.")
    bullet(pdf, "RoadPath хранит целочисленные ID вариантов прямой дороги — это визуальные данные в модели; PNG подставляет WorldSpriteRenderer.")
    bullet(pdf, "Стартовые 10 000 золота выглядят как отладочный баланс, не как финальная экономика.")
    bullet(pdf, "Крафт Spring Arrow всегда успешен (порог 1.00f).")
    bullet(pdf, "Модель использует LibGDX Vector2 и Array — это математика движка, не инверсия слоёв.")
    bullet(pdf, "Сохранения прогресса нет: инвентарь живёт, пока жив процесс приложения.")
    pdf.ln(6)
    pdf.set_font("Body", "I", 10)
    pdf.set_text_color(90, 90, 90)
    pdf.multi_cell(0, 6, "Источник правды — код в core/src/main/java. Если баланс или пакеты изменятся, пересоберите PDF: python docs/generate_documentation.py")

    OUT.parent.mkdir(parents=True, exist_ok=True)
    pdf.output(str(OUT))
    print(OUT)


if __name__ == "__main__":
    build()
