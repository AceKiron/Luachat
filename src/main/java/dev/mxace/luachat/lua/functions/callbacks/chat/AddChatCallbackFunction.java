package dev.mxace.luachat.lua.functions.callbacks.chat;

import dev.mxace.luachat.ChatInstance;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class AddChatCallbackFunction extends OneArgFunction {
    private final String name;

    public AddChatCallbackFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call(LuaValue luaValue) {
        ChatInstance.callbacks.add(luaValue.checkfunction());
        return LuaBoolean.TRUE;
    }
}
