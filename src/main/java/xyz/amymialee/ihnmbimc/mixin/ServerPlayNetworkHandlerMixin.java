package xyz.amymialee.ihnmbimc.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.ihnmbimc.IHaveNoMouthButIMustChat;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void ihnmbimc$onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        IHaveNoMouthButIMustChat.cancelChat(this.player, (text) -> this.player.sendMessage(text, false), ci);
    }
}