package dev.mxace.luachat.mixins;

import dev.mxace.luachat.ChatInstance;
import dev.mxace.luachat.lua.tables.TextStyleTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class, priority = 1050)
public class MixinChat {
    private boolean isCustomMessage = false;

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null || isCustomMessage) return;

        LuaTable messageTable = new LuaTable();
        LuaTable styledMessageTable = new LuaTable();

        if (message.getSiblings().isEmpty()) {
            LuaTable styledMessagePartTable = new LuaTable();
            styledMessagePartTable.set("text", message.getString());
            styledMessagePartTable.set("style", new TextStyleTable(message.getStyle()));

            styledMessageTable.set(1, styledMessagePartTable);
        } else {
            for (Text component : message.getSiblings()) {
                LuaTable styledMessagePartTable = new LuaTable();
                styledMessagePartTable.set("text", component.getString());
                styledMessagePartTable.set("style", new TextStyleTable(component.getStyle()));

                styledMessageTable.set(1 + styledMessageTable.length(), styledMessagePartTable);
            }
        }

        messageTable.set("plain", LuaString.valueOf(message.getContent().toString()));
        messageTable.set("styled", styledMessageTable);

        for (LuaFunction func : ChatInstance.callbacks) {
            messageTable = func.call(messageTable).checktable();
        }

        ci.cancel();

        isCustomMessage = true;

        LuaTable customMessageTable = messageTable.get("styled").checktable();
        MutableText customMessage = Text.empty();

        for (int i = 1; i <= customMessageTable.length(); i++) {
            TextStyleTable styleTable = (TextStyleTable) customMessageTable.get(i).get("style").checktable();

            customMessage = customMessage.append(
                Text.empty().append(customMessageTable.get(i).get("text").checkjstring()).fillStyle(Style.EMPTY
                    .withColor(styleTable.get("color").checkint())
                    .withBold(styleTable.get("bold").checkboolean())
                    .withObfuscated(styleTable.get("obfuscated").checkboolean())
                    .withUnderline(styleTable.get("underlined").checkboolean())
                    .withItalic(styleTable.get("italic").checkboolean())
                    .withStrikethrough(styleTable.get("strikethrough").checkboolean())
                )
            );
        }

        MinecraftClient.getInstance().player.sendMessage(customMessage);

        isCustomMessage = false;
    }
}
