package xyz.amymialee.ihnmbimc;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.ihnmbimc.cca.ChatManagerComponent;
import xyz.amymialee.ihnmbimc.util.ChatManagerCommand;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

import java.util.function.Consumer;

public class IHaveNoMouthButIMustChat implements ModInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "ihnmbimc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final MValueCategory IHNMBIMC_CATEGORY = new MValueCategory(id("ihnmbimc"), Items.NAME_TAG.getDefaultStack(), Identifier.ofVanilla("textures/block/stone_bricks.png"));
    public static final MValue<Boolean> CHAT_DISABLED = new MValue.MValueBoolean(IHNMBIMC_CATEGORY, id("chat_disabled"), (a) -> Items.BARRIER.getDefaultStack(), false);
    public static final MValue<Boolean> CHAT_WHITELIST = new MValue.MValueBoolean(IHNMBIMC_CATEGORY, id("chat_whitelist"), (a) -> Items.PAPER.getDefaultStack(), false);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("ihavenomouthbutimustchat").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("chat")
                        .then(CommandManager.literal("on").executes(ChatManagerCommand::executeChatOn))
                        .then(CommandManager.literal("off").executes(ChatManagerCommand::executeChatOff))
                )
                .then(CommandManager.literal("whitelist")
                        .then(CommandManager.literal("on").executes(ChatManagerCommand::executeWhitelistOn))
                        .then(CommandManager.literal("off").executes(ChatManagerCommand::executeWhitelistOff))
                        .then(CommandManager.literal("list").executes(ChatManagerCommand::executeWhitelistList))
                        .then(CommandManager.literal("add").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestWhitelistAdd).executes(ChatManagerCommand::executeWhitelistAdd)))
                        .then(CommandManager.literal("remove").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestWhitelistRemove).executes(ChatManagerCommand::executeWhitelistRemove)))
                        .then(CommandManager.literal("reload").executes(ChatManagerCommand::executeWhitelistReload))
                )
                .then(CommandManager.literal("banlist")
                        .then(CommandManager.literal("list").executes(ChatManagerCommand::executeBanlistList))
                        .then(CommandManager.literal("ban").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestBanlistAdd).executes(ChatManagerCommand::executeBanlistAdd)))
                        .then(CommandManager.literal("pardon").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestBanlistRemove).executes(ChatManagerCommand::executeBanlistRemove)))
                        .then(CommandManager.literal("reload").executes(ChatManagerCommand::executeBanlistReload))
                )
                .then(CommandManager.literal("timeout")
                        .then(CommandManager.literal("query").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestTimeoutQuery).executes(ChatManagerCommand::executeTimeoutQuery)))
                        .then(CommandManager.literal("set").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestTimeoutSet)
                                .then(CommandManager.argument("duration", LongArgumentType.longArg())
                                        .executes((ctx) -> ChatManagerCommand.executeTimeoutSet(ctx, ChatManagerCommand.TimeUnit.MINUTES))
                                        .then(CommandManager.literal("seconds").executes((ctx) -> ChatManagerCommand.executeTimeoutSet(ctx, ChatManagerCommand.TimeUnit.SECONDS)))
                                        .then(CommandManager.literal("minutes").executes((ctx) -> ChatManagerCommand.executeTimeoutSet(ctx, ChatManagerCommand.TimeUnit.MINUTES)))
                                        .then(CommandManager.literal("hours").executes((ctx) -> ChatManagerCommand.executeTimeoutSet(ctx, ChatManagerCommand.TimeUnit.HOURS)))
                                        .then(CommandManager.literal("days").executes((ctx) -> ChatManagerCommand.executeTimeoutSet(ctx, ChatManagerCommand.TimeUnit.DAYS))))))
                        .then(CommandManager.literal("pardon").then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).suggests(ChatManagerCommand::suggestTimeoutQuery).executes(ChatManagerCommand::executeTimeoutPardon)))
                        .then(CommandManager.literal("list").executes(ChatManagerCommand::executeTimeoutList))
                )
        )));
    }

    @Override
    public void registerScoreboardComponentFactories(@NotNull ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(ChatManagerComponent.KEY, (scoreboard, server) -> new ChatManagerComponent());
    }

    public static void cancelChat(PlayerEntity player, Consumer<Text> consumer, CallbackInfo ci) {
        if (CHAT_DISABLED.get()) {
            ci.cancel();
            consumer.accept(localize("chat.%s.chat.disabled").formatted(Formatting.RED));
            return;
        }
        if (player == null) return;
        var gameProfile = player.getGameProfile();
        var chatManager = ChatManagerComponent.KEY.get(player.getScoreboard());
        if (chatManager.isBanned(gameProfile)) {
            ci.cancel();
            consumer.accept(localize("chat.%s.chat.banned").formatted(Formatting.RED));
        } else if (chatManager.isTimedOut(gameProfile)) {
            ci.cancel();
            consumer.accept(localize("chat.%s.chat.timeout".formatted(chatManager.getTimeoutString(gameProfile))).formatted(Formatting.RED));
        } else if (CHAT_WHITELIST.get() && !chatManager.isWhitelisted(gameProfile)) {
            ci.cancel();
            consumer.accept(localize("chat.%s.chat.whitelisted").formatted(Formatting.RED));
        }
    }

    public static @NotNull MutableText localize(@NotNull String key, Object... args) {
        return Localization.text(Text.translatable(key.formatted(MOD_ID), args), ServerTranslations.INSTANCE.systemTarget).copy();
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}