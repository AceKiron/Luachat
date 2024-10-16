package dev.mxace.luachat.lua.functions.callbacks.chat;

import dev.mxace.luachat.ChatInstance;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class CountChatCallbacksFunction extends ZeroArgFunction {
    private final String name;

    public CountChatCallbacksFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call() {
        return LuaInteger.valueOf(ChatInstance.callbacks.size());
    }
}
