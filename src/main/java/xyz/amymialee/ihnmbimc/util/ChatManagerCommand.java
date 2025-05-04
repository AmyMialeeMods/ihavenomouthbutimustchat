package xyz.amymialee.ihnmbimc.util;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.ihnmbimc.IHaveNoMouthButIMustChat;
import xyz.amymialee.ihnmbimc.cca.ChatManagerComponent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ChatManagerCommand {
    public static int executeWhitelistOn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (IHaveNoMouthButIMustChat.CHAT_WHITELIST.get()) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.alreadyOn")).create();
        IHaveNoMouthButIMustChat.CHAT_WHITELIST.setValue(true);
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.enabled"), true);
        return 1;
    }

    public static int executeWhitelistOff(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!IHaveNoMouthButIMustChat.CHAT_WHITELIST.get()) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.alreadyOff")).create();
        IHaveNoMouthButIMustChat.CHAT_WHITELIST.setValue(false);
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.disabled"), true);
        return 1;
    }

    public static int executeWhitelistList(@NotNull CommandContext<ServerCommandSource> context) {
        var strings = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).getWhitelistedNames();
        if (strings.length == 0) {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.none"), false);
        } else {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.list", strings.length, String.join(", ", strings)), false);
        }
        return strings.length;
    }

    public static CompletableFuture<Suggestions> suggestWhitelistAdd(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().filter(player -> !ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).isWhitelisted(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
    }

    public static int executeWhitelistAdd(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (chatManager.isWhitelisted(gameProfile)) continue;
            chatManager.addToWhitelist(gameProfile);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.add.success", Text.literal(gameProfile.getName())), true);
            i++;
        }
        if (i == 0) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.add.failed")).create();
        return i;
    }

    public static CompletableFuture<Suggestions> suggestWhitelistRemove(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().filter(player -> ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).isWhitelisted(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
    }

    public static int executeWhitelistRemove(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (!chatManager.isWhitelisted(gameProfile)) continue;
            chatManager.removeFromWhitelist(gameProfile);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.remove.success", Text.literal(gameProfile.getName())), true);
            i++;
        }
        if (i == 0) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.remove.failed")).create();
        return i;
    }

    public static int executeWhitelistReload(@NotNull CommandContext<ServerCommandSource> context) {
        ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).loadWhitelist();
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.whitelist.reloaded"), true);
        return 1;
    }

    public static int executeBanlistList(@NotNull CommandContext<ServerCommandSource> context) {
        var strings = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).getBannedNames();
        if (strings.length == 0) {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.none"), false);
        } else {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.list", strings.length, String.join(", ", strings)), false);
        }
        return strings.length;
    }

    public static CompletableFuture<Suggestions> suggestBanlistAdd(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().filter(player -> !ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).isBanned(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
    }

    public static int executeBanlistAdd(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (chatManager.isBanned(gameProfile)) continue;
            chatManager.addToBanlist(gameProfile);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.add.success", Text.literal(gameProfile.getName())), true);
            i++;
        }
        if (i == 0) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.add.failed")).create();
        return i;
    }

    public static CompletableFuture<Suggestions> suggestBanlistRemove(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().filter(player -> ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).isBanned(player.getGameProfile())).map(player -> player.getGameProfile().getName()), builder);
    }

    public static int executeBanlistRemove(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (!chatManager.isBanned(gameProfile)) continue;
            chatManager.removeFromBanlist(gameProfile);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.remove.success", Text.literal(gameProfile.getName())), true);
            i++;
        }
        if (i == 0) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.remove.failed")).create();
        return i;
    }

    public static int executeBanlistReload(@NotNull CommandContext<ServerCommandSource> context) {
        ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).loadBanlist();
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.banlist.reloaded"), true);
        return 1;
    }

    public static int executeChatOn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!IHaveNoMouthButIMustChat.CHAT_DISABLED.get()) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.chat.alreadyEnabled")).create();
        IHaveNoMouthButIMustChat.CHAT_DISABLED.setValue(false);
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.chat.enabled"), true);
        return 1;
    }

    public static int executeChatOff(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (IHaveNoMouthButIMustChat.CHAT_DISABLED.get()) throw new SimpleCommandExceptionType(IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.chat.alreadyDisabled")).create();
        IHaveNoMouthButIMustChat.CHAT_DISABLED.setValue(true);
        context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.chat.disabled"), true);
        return 1;
    }

    public static CompletableFuture<Suggestions> suggestTimeoutQuery(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().filter(player -> !ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).isTimedOut(player.getGameProfile())).map(player -> player.getGameProfile().getName()), suggestionsBuilder);
    }

    public static int executeTimeoutQuery(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (!chatManager.isTimedOut(gameProfile)) continue;
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.timeout.query", Text.literal(gameProfile.getName()), chatManager.getTimeoutString(gameProfile)), true);
            i++;
        }
        return i;
    }

    public static CompletableFuture<Suggestions> suggestTimeoutSet(@NotNull CommandContext<ServerCommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(context.getSource().getServer().getPlayerManager().getPlayerList().stream().map(player -> player.getGameProfile().getName()), suggestionsBuilder);
    }

    public static int executeTimeoutSet(@NotNull CommandContext<ServerCommandSource> context, @NotNull TimeUnit timeUnit) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        var time = timeUnit.multiplier.apply(LongArgumentType.getLong(context, "duration"));
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            chatManager.setTimeout(gameProfile, time);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.timeout.set.success", Text.literal(gameProfile.getName()), chatManager.getTimeoutString(gameProfile)), true);
            i++;
        }
        return i;
    }

    public static int executeTimeoutPardon(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var chatManager = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard());
        var i = 0;
        for (var gameProfile : GameProfileArgumentType.getProfileArgument(context, "target")) {
            if (!chatManager.isTimedOut(gameProfile)) continue;
            chatManager.removeTimeout(gameProfile);
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.timeout.pardon.success", Text.literal(gameProfile.getName())), true);
            i++;
        }
        return i;
    }

    public static int executeTimeoutList(@NotNull CommandContext<ServerCommandSource> context) {
        var strings = ChatManagerComponent.KEY.get(context.getSource().getServer().getScoreboard()).getTimeoutNames();
        if (strings.length == 0) {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.timeout.none"), false);
        } else {
            context.getSource().sendFeedback(() -> IHaveNoMouthButIMustChat.localize(context.getSource().getPlayer(), "commands.%s.timeout.list", strings.length, String.join(", ", strings)), false);
        }
        return strings.length;
    }

    public enum TimeUnit {
        SECONDS((i) -> i * 1000L),
        MINUTES((i) -> i * 1000L * 60L),
        HOURS((i) -> i * 1000L * 60L * 60L),
        DAYS((i) -> i * 1000L * 60L * 60L * 24L);

        public final Function<Long, Long> multiplier;

        TimeUnit(final Function<Long, Long> multiplier) {
            this.multiplier = multiplier;
        }
    }
}