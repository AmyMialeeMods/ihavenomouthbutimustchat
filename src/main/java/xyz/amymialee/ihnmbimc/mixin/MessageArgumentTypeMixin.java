package xyz.amymialee.ihnmbimc.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.ihnmbimc.IHaveNoMouthButIMustChat;
import xyz.amymialee.ihnmbimc.cca.ChatManagerComponent;
import xyz.amymialee.mialib.util.runnables.TriConsumer;

import java.util.function.Consumer;

@Mixin(MessageArgumentType.class)
public class MessageArgumentTypeMixin {
    @Inject(method = "getSignedMessage", at = @At("HEAD"), cancellable = true)
    private static void ihnmbimc$onCommandMessage(@NotNull CommandContext<ServerCommandSource> context, String name, Consumer<SignedMessage> callback, CallbackInfo ci) {
        IHaveNoMouthButIMustChat.cancelChat(context.getSource().getPlayer(), (text) -> context.getSource().sendFeedback(() -> text, false), ci);
    }
}