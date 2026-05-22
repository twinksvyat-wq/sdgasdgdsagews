package kronex.fun.display.screens.clickgui.components.implement.module;

import kronex.fun.features.impl.misc.actions.action.ClickFriend;
import kronex.fun.features.impl.misc.actions.action.ClickPearl;
import kronex.fun.features.module.Module;
import kronex.fun.other.common.localization.Language;

import kronex.fun.features.impl.combat.*;
import kronex.fun.features.impl.misc.*;
import kronex.fun.features.impl.movement.*;
import kronex.fun.features.impl.player.*;
import kronex.fun.features.impl.pve.*;
import kronex.fun.features.impl.render.*;

import kronex.fun.features.impl.render.Interface;
import kronex.fun.features.impl.misc.AuctionHelper;
import kronex.fun.features.impl.combat.AutoArmor;
import kronex.fun.features.impl.combat.NoInteract;
import kronex.fun.features.impl.combat.ShiftTap;
import kronex.fun.features.impl.combat.TapeMouse;
import kronex.fun.features.impl.combat.TargetPearl;

public class ModuleDescriptions {
    public static Language language = Language.RUS;

    public static void setLanguage(Language lang) {
        language = lang;
    }

    public static String getDescription(Module module) {
        if (language == Language.RUS) {
            if (module instanceof ServerHelper)
                return "Помогает взаимодействовать с сервером, предоставляя полезные функции.";
            if (module instanceof ItemScroller)
                return "Настраивает поведение предметов для удобства.";
            if (module instanceof Interface)
                return "Отображает дополнительную информацию на экране.";
            if (module instanceof AuctionHelper)
                return "Помогает управлять аукционами на сервере.";
            if (module instanceof ProjectilePrediction)
                return "Предсказывает траекторию полета снарядов.";
            if (module instanceof AntiAFK)
                return "Выполняет какое-то действие чтобы игрока не кикнуло за афк.";
            if (module instanceof Strafe)
                return "Помогает игроку при ходьбе.";
            if (module instanceof TargetStrafe)
                return "Крутится вокруг таргета в aura.";
            if (module instanceof Jesus)
                return "Дает возможность игроку ходить по воде.";
            if (module instanceof XRay)
                return "Позволяет видеть сквозь блоки для поиска ресурсов.";
            if (module instanceof TriggerBot)
                return "Бьет сущность если игрок смотрит на нее.";
            if (module instanceof Aura)
                return "Автоматически атакует ближайших врагов.";
            if (module instanceof AutoSwap)
                return "Автоматически меняет предметы в руке.";
            if (module instanceof AutoPilot)
                return "Наводит камеру игрока на ценный предмет сервера ReallyWorld";
            if (module instanceof NoFriendDamage)
                return "Предотвращает урон по союзникам.";
            if (module instanceof SelfDestruct)
                return "Скрывает чит с игры, находится в разработке.";
            if (module instanceof HitBoxModule)
                return "Изменяет размер хитбоксов сущностей.";
            if (module instanceof AntiBot)
                return "Обнаруживает и игнорирует ботов на сервере.";
            if (module instanceof AutoCrystal)
                return "Автоматизирует размещение и уничтожение кристаллов.";
            if (module instanceof AutoSprint)
                return "Автоматически включает спринт при движении.";
            if (module instanceof Velocity)
                return "Уменьшает отбрасывание от атак.";
            if (module instanceof InventoryMove)
                return "Позволяет двигаться при открытом интерфейсе.";
            if (module instanceof Blink)
                return "Создает иллюзию телепортации для других игроков.";
            if (module instanceof Fly)
                return "Позволяет летать в режиме выживания.";
            if (module instanceof AutoTotem)
                return "Автоматически экипирует тотем при низком здоровье.";
            if (module instanceof FreeCam)
                return "Предоставляет инструменты для отладки камеры.";
            if (module instanceof ChestStealer)
                return "Быстро забирает предметы из контейнеров.";
            if (module instanceof AutoTpAccept)
                return "Автоматически принимает запросы на телепортацию.";
            if (module instanceof AutoLeave)
                return "Автоматически покидает сервер при угрозе.";
            if (module instanceof TargetPearl)
                return "Автоматически бросает жемчужины в цель.";
            if (module instanceof AutoArmor)
                return "Автоматически надевает лучшую броню.";
            if (module instanceof NoInteract)
                return "Блокирует взаимодействие с объектами.";
            if (module instanceof ServerRPSpoofer)
                return "Подделывает данные для серверов.";
            if (module instanceof ShiftTap)
                return "Автоматически приседает при ударе.";
            if (module instanceof ClickPearl)
                return "Автоматически использует жемчужины при нажатии.";
            if (module instanceof ClickFriend)
                return "Добавляет игроков в список друзей при нажатии.";
            if (module instanceof WindJump)
                return "Усиливает прыжки с учетом ветра.";
            if (module instanceof AirStuck)
                return "Замораживает игрока в воздухе.";
            if (module instanceof ElytraMotion)
                return "Улучшает управление и скорость полета на элитрах.";
            if (module instanceof ElytraHelper)
                return "Улучшает управление элитрами.";
            if (module instanceof Joiner)
                return "Облегчает процесс входа на сервер.";
            if (module instanceof TabParser)
                return "Парсит список игроков для получения информации.";
            if (module instanceof ElytraTarget)
                return "Помогает при полете на элитрах.";
            if (module instanceof TapeMouse)
                return "Автоматически кликает мышкой для выполнения действий.";
            if (module instanceof IRC)
                return "Позволяет общаться в приватном чате чита.";
            if (module instanceof BoatNoClip)
                return "Позволяет лодке проходить сквозь стены.";
            if (module instanceof LongJump)
                return "Позволяет прыгать на большие расстояния.";
            if (module instanceof NoClip)
                return "Позволяет проходить сквозь стены.";
            if (module instanceof NoWeb)
                return "Позволяет быстро передвигаться сквозь паутину.";
            if (module instanceof Speed)
                return "Увеличивает скорость передвижения игрока.";
            if (module instanceof Spider)
                return "Позволяет лазать по стенам как паук.";
            if (module instanceof SuperFireWork)
                return "Улучшает полет с фейерверками.";
            if (module instanceof AutoBuy)
                return "Автоматически покупает предметы в магазине.";
            if (module instanceof AutoRespawn)
                return "Автоматически возрождается после смерти.";
            if (module instanceof AutoTool)
                return "Автоматически выбирает лучший инструмент.";
            if (module instanceof AutoUse)
                return "Автоматически использует предметы.";
            if (module instanceof FastBreak)
                return "Позволяет ломать блоки быстрее.";
            if (module instanceof FreeLook)
                return "Позволяет вращать камеру вокруг персонажа.";
            if (module instanceof NameProtect)
                return "Скрывает имена игроков.";
            if (module instanceof NoDelay)
                return "Убирает задержку для действий.";
            if (module instanceof NoEntityTrace)
                return "Позволяет бить сквозь сущностей.";
            if (module instanceof NoPush)
                return "Убирает отталкивание от игроков и блоков.";
            if (module instanceof Arrows)
                return "Показывает стрелки, указывающие на игроков.";
            if (module instanceof AspectRatio)
                return "Изменяет соотношение сторон экрана.";
            if (module instanceof BetterMinecraft)
                return "Улучшает визуальные эффекты Minecraft.";
            if (module instanceof BlockESP)
                return "Подсвечивает определенные блоки.";
            if (module instanceof BlockOverlay)
                return "Изменяет очертание выделенного блока.";
            if (module instanceof CameraSettings)
                return "Настройки камеры и вида.";
            if (module instanceof CrossHair)
                return "Изменяет перекрестие прицела.";
            if (module instanceof Esp)
                return "Подсвечивает игроков и сущностей через стены.";
            if (module instanceof JumpCircle)
                return "Рисует круги при прыжках.";
            if (module instanceof KillEffect)
                return "Эффекты при убийстве игрока.";
            if (module instanceof Removals)
                return "Убирает рендер определенных элементов.";
            if (module instanceof SantaHatModule)
                return "Добавляет новогоднюю шапку.";
            if (module instanceof SeeInvisible)
                return "Позволяет видеть невидимых игроков.";
            if (module instanceof SwingAnimation)
                return "Изменяет анимацию удара.";
            if (module instanceof ViewModel)
                return "Настройка отображения предметов в руке.";
            if (module instanceof WorldTweaks)
                return "Изменяет время и погоду в мире.";
            if (module instanceof EmotionsModule)
                return "Позволяет использовать игровые эмоции.";
            if (module instanceof CreeperFarm)
                return "Автоматически фармит порох с криперов.";
            if (module instanceof FTBaseFinder)
                return "Помогает искать базы на FunTime.";
            if (module instanceof FarmBuilder)
                return "Автоматически строит фермы в мире.";
            if (module instanceof KTLeave)
                return "Автоматически выходит при проверке на FunTime.";
            return "Описание модуля отсутствует.";
        } else {
            if (module instanceof ServerHelper)
                return "Helps interact with the server by providing useful functions.";
            if (module instanceof ItemScroller)
                return "Configures item behavior for convenience.";
            if (module instanceof Interface)
                return "Displays additional information on the screen.";
            if (module instanceof AuctionHelper)
                return "Helps manage auctions on the server.";
            if (module instanceof ProjectilePrediction)
                return "Predicts the trajectory of projectiles.";
            if (module instanceof AntiAFK)
                return "Performs an action to prevent being kicked for AFK.";
            if (module instanceof Strafe)
                return "Helps the player while walking.";
            if (module instanceof TargetStrafe)
                return "Circles around the target in aura.";
            if (module instanceof Jesus)
                return "Allows the player to walk on water.";
            if (module instanceof XRay)
                return "Allows seeing through blocks to find resources.";
            if (module instanceof TriggerBot)
                return "Hits an entity if the player looks at it.";
            if (module instanceof Aura)
                return "Automatically attacks nearby enemies.";
            if (module instanceof AutoSwap)
                return "Automatically swaps items in hand.";
            if (module instanceof AutoPilot)
                return "Aims the player's camera at a valuable item on the ReallyWorld server.";
            if (module instanceof NoFriendDamage)
                return "Prevents damage to allies.";
            if (module instanceof SelfDestruct)
                return "Hides the cheat from the game (Work in Progress).";
            if (module instanceof HitBoxModule)
                return "Changes the size of entity hitboxes.";
            if (module instanceof AntiBot)
                return "Detects and ignores bots on the server.";
            if (module instanceof AutoCrystal)
                return "Automates crystal placement and destruction.";
            if (module instanceof AutoSprint)
                return "Automatically enables sprint when moving.";
            if (module instanceof Velocity)
                return "Reduces knockback from attacks.";
            if (module instanceof InventoryMove)
                return "Allows movement while the inventory is open.";
            if (module instanceof Blink)
                return "Creates a teleportation illusion for other players.";
            if (module instanceof Fly)
                return "Allows flying in survival mode.";
            if (module instanceof AutoTotem)
                return "Automatically equips a totem at low health.";
            if (module instanceof FreeCam)
                return "Provides tools for camera debugging.";
            if (module instanceof ChestStealer)
                return "Quickly takes items from containers.";
            if (module instanceof AutoTpAccept)
                return "Automatically accepts teleport requests.";
            if (module instanceof AutoLeave)
                return "Automatically leaves the server when threatened.";
            if (module instanceof TargetPearl)
                return "Automatically throws pearls at the target.";
            if (module instanceof AutoArmor)
                return "Automatically equips the best armor.";
            if (module instanceof NoInteract)
                return "Blocks interaction with objects.";
            if (module instanceof ServerRPSpoofer)
                return "Spoofs data for servers.";
            if (module instanceof ShiftTap)
                return "Automatically sneaks when hitting.";
            if (module instanceof ClickPearl)
                return "Automatically uses pearls on click.";
            if (module instanceof ClickFriend)
                return "Adds players to the friends list on click.";
            if (module instanceof WindJump)
                return "Boosts jumps considering wind direction.";
            if (module instanceof AirStuck)
                return "Freezes the player in the air, preventing movement.";
            if (module instanceof ElytraMotion)
                return "Improves elytra flight control and speed.";
            if (module instanceof ElytraHelper)
                return "Improves elytra control.";
            if (module instanceof Joiner)
                return "Facilitates the server join process.";
            if (module instanceof TabParser)
                return "Parses the tab list for information.";
            if (module instanceof ElytraTarget)
                return "Assists in elytra flight.";
            if (module instanceof TapeMouse)
                return "Automatically clicks the mouse to perform actions.";
            if (module instanceof IRC)
                return "Allows chatting in the cheat's private chat.";
            if (module instanceof BoatNoClip)
                return "Allows the boat to pass through walls.";
            if (module instanceof LongJump)
                return "Allows jumping long distances.";
            if (module instanceof NoClip)
                return "Allows passing through walls.";
            if (module instanceof NoWeb)
                return "Allows moving quickly through webs.";
            if (module instanceof Speed)
                return "Increases user movement speed.";
            if (module instanceof Spider)
                return "Allows climbing walls like a spider.";
            if (module instanceof SuperFireWork)
                return "Improves flight with fireworks.";
            if (module instanceof AutoBuy)
                return "Automatically buys items from the shop.";
            if (module instanceof AutoRespawn)
                return "Automatically respawns after death.";
            if (module instanceof AutoTool)
                return "Automatically selects the best tool.";
            if (module instanceof AutoUse)
                return "Automatically uses items.";
            if (module instanceof FastBreak)
                return "Allows breaking blocks faster.";
            if (module instanceof FreeLook)
                return "Allows rotating the camera around the character.";
            if (module instanceof NameProtect)
                return "Hides players' names.";
            if (module instanceof NoDelay)
                return "Removes delay for actions.";
            if (module instanceof NoEntityTrace)
                return "Allows hitting through entities.";
            if (module instanceof NoPush)
                return "Removes pushback from players and blocks.";
            if (module instanceof Arrows)
                return "Shows arrows pointing to players.";
            if (module instanceof AspectRatio)
                return "Changes the aspect ratio of the screen.";
            if (module instanceof BetterMinecraft)
                return "Improves Minecraft visual effects.";
            if (module instanceof BlockESP)
                return "Highlights specific blocks.";
            if (module instanceof BlockOverlay)
                return "Changes block outline.";
            if (module instanceof CameraSettings)
                return "Camera and view settings.";
            if (module instanceof CrossHair)
                return "Changes the crosshair.";
            if (module instanceof Esp)
                return "Highlights players and entities through walls.";
            if (module instanceof JumpCircle)
                return "Draws circles when jumping.";
            if (module instanceof KillEffect)
                return "Effects upon killing a player.";
            if (module instanceof Removals)
                return "Removes rendering of specific elements.";
            if (module instanceof SantaHatModule)
                return "Adds a Christmas hat.";
            if (module instanceof SeeInvisible)
                return "Allows seeing invisible players.";
            if (module instanceof SwingAnimation)
                return "Changes swing animation.";
            if (module instanceof ViewModel)
                return "Configures item rendering in hand.";
            if (module instanceof WorldTweaks)
                return "Changes time and weather in the world.";
            if (module instanceof EmotionsModule)
                return "Allows using in-game emotions.";
            if (module instanceof CreeperFarm)
                return "Automatically farms gunpowder from creepers.";
            if (module instanceof FTBaseFinder)
                return "Helps locate bases on FunTime.";
            if (module instanceof FarmBuilder)
                return "Automatically builds farms in the world.";
            if (module instanceof KTLeave)
                return "Automatically leaves during staff checks on FunTime.";
            return "Module description is missing.";
        }
    }
}