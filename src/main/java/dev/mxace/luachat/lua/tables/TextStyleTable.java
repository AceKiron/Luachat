package dev.mxace.luachat.lua.tables;

import net.minecraft.text.Style;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class TextStyleTable extends LuaTable {
    public TextStyleTable(Style style) {
        super();

        set("color", LuaString.valueOf(style.getColor() == null ? 0xFFFFFF : style.getColor().getRgb()));
        set("bold", LuaBoolean.valueOf(style.isBold()));
        set("obfuscated", LuaBoolean.valueOf(style.isObfuscated()));
        set("underlined", LuaBoolean.valueOf(style.isUnderlined()));
        set("italic", LuaBoolean.valueOf(style.isItalic()));
        set("strikethrough", LuaBoolean.valueOf(style.isStrikethrough()));
    }
}
