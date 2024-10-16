package dev.mxace.luachat.lua.tables;

import dev.mxace.luachat.lua.functions.callbacks.chat.AddChatCallbackFunction;
import dev.mxace.luachat.lua.functions.callbacks.chat.CountChatCallbacksFunction;

import org.luaj.vm2.LuaTable;

public class ChatCallbacksTable extends LuaTable {
    public ChatCallbacksTable(String name) {
        super();

        set("add", new AddChatCallbackFunction(name));
        set("count", new CountChatCallbacksFunction(name));
    }
}
