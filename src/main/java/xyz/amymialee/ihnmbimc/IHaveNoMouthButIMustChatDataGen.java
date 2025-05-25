package xyz.amymialee.ihnmbimc;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.MDataGen;

import java.nio.file.Path;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class IHaveNoMouthButIMustChatDataGen extends MDataGen {
    public static Consumer<FabricLanguageProvider.TranslationBuilder> translations = (builder) -> {
        // MValues
        builder.add(IHaveNoMouthButIMustChat.IHNMBIMC_CATEGORY.getTranslationKey(), "Have No Mouth But Must Chat");
        builder.add(IHaveNoMouthButIMustChat.CHAT_DISABLED.getTranslationKey(), "Chat Disabled");
        builder.add(IHaveNoMouthButIMustChat.CHAT_DISABLED.getDescriptionTranslationKey(), "Sets the chat to disabled, so nobody can talk.");
        builder.add(IHaveNoMouthButIMustChat.CHAT_WHITELIST.getTranslationKey(), "Chat Whitelist");
        builder.add(IHaveNoMouthButIMustChat.CHAT_WHITELIST.getDescriptionTranslationKey(), "Sets the chat to whitelist, so only players in the whitelist can talk.");
        // Chat Feedback
        builder.add("chat.%s.chat.disabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat is disabled!");
        builder.add("chat.%s.chat.banned".formatted(IHaveNoMouthButIMustChat.MOD_ID), "You are banned from chat!");
        builder.add("chat.%s.chat.timeout".formatted(IHaveNoMouthButIMustChat.MOD_ID), "You are timed out for %s");
        builder.add("chat.%s.chat.whitelisted".formatted(IHaveNoMouthButIMustChat.MOD_ID), "You are not whitelisted to chat!");
        // Command Errors
        builder.add("commands.%s.chat.alreadyEnabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat is already enabled");
        builder.add("commands.%s.chat.alreadyDisabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat is already disabled");
        builder.add("commands.%s.whitelist.alreadyOn".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat whitelist is already turned on");
        builder.add("commands.%s.whitelist.alreadyOff".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat whitelist is already turned off");
        builder.add("commands.%s.whitelist.add.failed".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Player is already chat whitelisted");
        builder.add("commands.%s.whitelist.remove.failed".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Player is not chat whitelisted");
        builder.add("commands.%s.banlist.add.failed".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Player is already chat banned");
        builder.add("commands.%s.banlist.remove.failed".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Player is not chat banned");
        // Whitelist
        builder.add("commands.%s.whitelist.enabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat whitelist is now enabled");
        builder.add("commands.%s.whitelist.disabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat whitelist is now disabled");
        builder.add("commands.%s.whitelist.none".formatted(IHaveNoMouthButIMustChat.MOD_ID), "No players are chat whitelisted");
        builder.add("commands.%s.whitelist.list".formatted(IHaveNoMouthButIMustChat.MOD_ID), "There are %s chat whitelisted players: %s");
        builder.add("commands.%s.whitelist.add.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Added %s to the chat whitelist");
        builder.add("commands.%s.whitelist.remove.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Removed %s from the chat whitelist");
        builder.add("commands.%s.whitelist.reloaded".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Reloaded the chat whitelist");
        // Banlist
        builder.add("commands.%s.banlist.none".formatted(IHaveNoMouthButIMustChat.MOD_ID), "No players are chat banned");
        builder.add("commands.%s.banlist.list".formatted(IHaveNoMouthButIMustChat.MOD_ID), "There are %s chat banned players: %s");
        builder.add("commands.%s.banlist.add.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Banned %s from using chat");
        builder.add("commands.%s.banlist.remove.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Unbanned %s from using chat");
        builder.add("commands.%s.banlist.reloaded".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Reloaded the chat banlist");
        builder.add("commands.%s.chat.enabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat is now enabled");
        builder.add("commands.%s.chat.disabled".formatted(IHaveNoMouthButIMustChat.MOD_ID), "Chat is now disabled");
        // Timeouts
        builder.add("commands.%s.timeout.query".formatted(IHaveNoMouthButIMustChat.MOD_ID), "%s is timed out for %s");
        builder.add("commands.%s.timeout.set.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "%s is now timed out for %s");
        builder.add("commands.%s.timeout.pardon.success".formatted(IHaveNoMouthButIMustChat.MOD_ID), "%s is no longer timed out");
        builder.add("commands.%s.timeout.none".formatted(IHaveNoMouthButIMustChat.MOD_ID), "No players are timed out");
        builder.add("commands.%s.timeout.list".formatted(IHaveNoMouthButIMustChat.MOD_ID), "There are %s timed out players: %s");
    };

    @Override
    protected void generateTranslations(MLanguageProvider provider, FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        translations.accept(builder);
    }

    @Override
    protected void addExtraDataProviders(FabricDataGenerator.@NotNull Pack pack) {
        pack.addProvider((dataOutput, future) -> new FabricServerLanguageProvider(dataOutput, "en_us") {
            @Override
            public void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
                translations.accept(translationBuilder);
            }
        });
    }

    public abstract static class FabricServerLanguageProvider implements DataProvider {
        protected final FabricDataOutput dataOutput;
        private final String languageCode;

        protected FabricServerLanguageProvider(FabricDataOutput dataOutput, String languageCode) {
            this.dataOutput = dataOutput;
            this.languageCode = languageCode;
        }

        public abstract void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder);

        @Override
        public CompletableFuture<?> run(DataWriter writer) {
            var translationEntries = new TreeMap<String, String>();
            this.generateTranslations((String key, String value) -> {
                Objects.requireNonNull(key);
                Objects.requireNonNull(value);
                if (translationEntries.containsKey(key)) {
                    throw new RuntimeException("Existing translation key found - " + key + " - Duplicate will be ignored.");
                }
                translationEntries.put(key, value);
            });
            var langEntryJson = new JsonObject();
            for (var entry : translationEntries.entrySet()) {
                langEntryJson.addProperty(entry.getKey(), entry.getValue());
            }
            return DataProvider.writeToPath(writer, langEntryJson, this.getLangFilePath(this.languageCode));
        }

        private Path getLangFilePath(String code) {
            return this.dataOutput.getResolver(DataOutput.OutputType.DATA_PACK, "lang").resolveJson(Identifier.of(this.dataOutput.getModId(), code));
        }

        @Override
        public String getName() {
            return "Server Language (%s)".formatted(this.languageCode);
        }
    }
}