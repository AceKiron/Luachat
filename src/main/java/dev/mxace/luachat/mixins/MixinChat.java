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
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class, priority = 1050)
public class MixinChat {
    @Unique
    private boolean isCustomMessage = false;

    @Unique
    private void addSibling(LuaTable styledMessageTable, Text sibling) {
        Style style = sibling.getStyle();

        String[] plainSiblingParts = sibling.getString().split("§");

        System.out.println("addSibling:" + sibling.getString());

        for (int i = 0; i < plainSiblingParts.length; i++) {
            String plainSiblingPart = plainSiblingParts[i];

            if (i > 0) {
                char c = plainSiblingPart.charAt(0);

                if (c == '0') style = style.withColor(Formatting.BLACK);
                else if (c == '1') style = style.withColor(Formatting.DARK_BLUE);
                else if (c == '2') style = style.withColor(Formatting.DARK_GREEN);
                else if (c == '3') style = style.withColor(Formatting.DARK_AQUA);
                else if (c == '4') style = style.withColor(Formatting.DARK_RED);
                else if (c == '5') style = style.withColor(Formatting.DARK_PURPLE);
                else if (c == '6') style = style.withColor(Formatting.GOLD);
                else if (c == '7') style = style.withColor(Formatting.GRAY);
                else if (c == '8') style = style.withColor(Formatting.DARK_GRAY);
                else if (c == '9') style = style.withColor(Formatting.BLUE);
                else if (c == 'a') style = style.withColor(Formatting.GREEN);
                else if (c == 'b') style = style.withColor(Formatting.AQUA);
                else if (c == 'c') style = style.withColor(Formatting.RED);
                else if (c == 'd') style = style.withColor(Formatting.LIGHT_PURPLE);
                else if (c == 'e') style = style.withColor(Formatting.YELLOW);
                else if (c == 'f') style = style.withColor(Formatting.WHITE);

                else if (c == 'k') style = style.withObfuscated(true);
                else if (c == 'l') style = style.withBold(true);
                else if (c == 'm') style = style.withStrikethrough(true);
                else if (c == 'n') style = style.withUnderline(true);
                else if (c == 'o') style = style.withItalic(true);
                else if (c == 'r') style = Style.EMPTY;

                if (plainSiblingPart.length() > 1) {
                    // Contains actual text
                    LuaTable styledMessagePartTable = new LuaTable();
                    styledMessagePartTable.set("text", plainSiblingPart.substring(1));
                    styledMessagePartTable.set("style", new TextStyleTable(style));

                    styledMessageTable.set(1 + styledMessageTable.length(), styledMessagePartTable);
                }
            } else {
                // Contains actual text
                LuaTable styledMessagePartTable = new LuaTable();
                styledMessagePartTable.set("text", plainSiblingPart);
                styledMessagePartTable.set("style", new TextStyleTable(style));

                styledMessageTable.set(1 + styledMessageTable.length(), styledMessagePartTable);
            }
        }
    }

    @Unique
    private void recursiveFunctionWithoutProperName(Text component, LuaTable styledMessageTable, int depth) {
        System.out.println("A:" + component.getString());

        if (component.getSiblings().isEmpty()) {
            System.out.println("B");
            addSibling(styledMessageTable, component);
        } else {
            System.out.println("C:" + component.getSiblings().size());

            for (Text subcomponent : component.getSiblings()) {
                System.out.println("D:" + subcomponent.getString());
                recursiveFunctionWithoutProperName(subcomponent, styledMessageTable, depth + 1);
            }
        }
    }

    @Inject(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null || isCustomMessage) return;

//        ci.cancel();

        LuaTable messageTable = new LuaTable();
        LuaTable styledMessageTable = new LuaTable();

        recursiveFunctionWithoutProperName(message, styledMessageTable, 0);

//        messageTable.set("plain", LuaString.valueOf(message.getString().replaceAll("§.", "")));
        messageTable.set("plain", LuaString.valueOf(message.getString()));
        messageTable.set("styled", styledMessageTable);

        for (LuaFunction func : ChatInstance.callbacks) {
            messageTable = func.call(messageTable).checktable();
        }

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

        MinecraftClient.getInstance().player.sendMessage(Text.empty().append("CUSTOM ").append(customMessage));

        isCustomMessage = false;
    }
}
