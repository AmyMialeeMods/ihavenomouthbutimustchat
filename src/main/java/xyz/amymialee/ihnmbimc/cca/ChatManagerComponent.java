package xyz.amymialee.ihnmbimc.cca;

import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import xyz.amymialee.ihnmbimc.IHaveNoMouthButIMustChat;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class ChatManagerComponent implements Component {
    public static final ComponentKey<ChatManagerComponent> KEY = ComponentRegistry.getOrCreate(IHaveNoMouthButIMustChat.id("chatmanager"), ChatManagerComponent.class);
    private static final File IHNMBIMC_WHITELIST_FILE = FabricLoader.getInstance().getConfigDir().resolve("ihnmbimc_whitelist.json").toFile();
    private static final File IHNMBIMC_BANS_FILE = FabricLoader.getInstance().getConfigDir().resolve("ihnmbimc_banlist.json").toFile();
    private final Whitelist whitelist = new Whitelist(IHNMBIMC_WHITELIST_FILE);
    private final Whitelist banlist = new Whitelist(IHNMBIMC_BANS_FILE);
    private final HashMap<GameProfile, Long> timeouts = new HashMap<>();

    public boolean isWhitelisted(GameProfile profile) {
        return this.whitelist.isAllowed(profile);
    }

    public boolean isBanned(GameProfile profile) {
        return this.banlist.isAllowed(profile);
    }

    public boolean isTimedOut(GameProfile profile) {
        if (!this.timeouts.containsKey(profile)) return false;
        long timeout = this.timeouts.get(profile);
        if (System.currentTimeMillis() > timeout) {
            this.timeouts.remove(profile);
            return false;
        }
        return true;
    }

    public String[] getWhitelistedNames() {
        return this.whitelist.getNames();
    }

    public String[] getBannedNames() {
        return this.banlist.getNames();
    }

    public String[] getTimeoutNames() {
        for (var key : this.timeouts.keySet().toArray(new GameProfile[0])) {
            var timeout = this.timeouts.get(key);
            if (System.currentTimeMillis() > timeout) this.timeouts.remove(key);
        }
        return this.timeouts.entrySet().stream().map(entry -> {
            var profile = entry.getKey();
            var timeout = entry.getValue();
            var remaining = timeout - System.currentTimeMillis();
            return String.format("%s (%02d:%02d:%02d)", profile.getName(), (remaining / 3600000), (remaining % 3600000) / 60000, (remaining % 60000) / 1000);
        }).toArray(String[]::new);
    }

    public String getTimeoutString(GameProfile profile) {
        if (!this.timeouts.containsKey(profile)) return "null";
        var timeout = this.timeouts.get(profile);
        var remaining = timeout - System.currentTimeMillis();
        return String.format("%02d:%02d:%02d", remaining / 3600000, (remaining % 3600000) / 60000, (remaining % 60000) / 1000);
    }

    public void addToWhitelist(GameProfile profile) {
        this.whitelist.add(new WhitelistEntry(profile));
    }

    public void removeFromWhitelist(GameProfile profile) {
        this.whitelist.remove(new WhitelistEntry(profile));
    }

    public void addToBanlist(GameProfile profile) {
        this.banlist.add(new WhitelistEntry(profile));
    }

    public void removeFromBanlist(GameProfile profile) {
        this.banlist.remove(new WhitelistEntry(profile));
    }

    public void setTimeout(GameProfile profile, long timeout) {
        this.timeouts.put(profile, System.currentTimeMillis() + timeout);
    }

    public void removeTimeout(GameProfile profile) {
        this.timeouts.remove(profile);
    }

    public void loadWhitelist() {
        try {
            this.whitelist.load();
        } catch (Exception exception) {
            IHaveNoMouthButIMustChat.LOGGER.warn("Failed to load chat whitelist: ", exception);
        }
    }

    public void loadBanlist() {
        try {
            this.banlist.load();
        } catch (Exception exception) {
            IHaveNoMouthButIMustChat.LOGGER.warn("Failed to load chat banlist: ", exception);
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.timeouts.clear();
        var timeoutsTag = tag.getCompoundOrEmpty("timeouts");
        for (var key : timeoutsTag.getKeys()) {
            var profileTag = timeoutsTag.getCompoundOrEmpty(key);
            if (!profileTag.isEmpty()) continue;
            var profile = new GameProfile(UUID.fromString(""), profileTag.getString("name", ""));
            var timeout = profileTag.getLong("timeout", 0);
            this.timeouts.put(profile, timeout);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var timeoutsTag = new NbtCompound();
        for (var entry : this.timeouts.entrySet()) {
            var profileTag = new NbtCompound();
            profileTag.putString("name", entry.getKey().getName());
            profileTag.putLong("timeout", entry.getValue());
            timeoutsTag.put(entry.getKey().getId().toString(), profileTag);
        }
        tag.put("timeouts", timeoutsTag);
    }
}