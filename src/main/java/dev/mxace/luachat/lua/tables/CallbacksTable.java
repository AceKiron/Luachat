package dev.mxace.luachat.lua.tables;

import org.luaj.vm2.LuaTable;

public class CallbacksTable extends LuaTable {
    public CallbacksTable(String name) {
        super();

        set("chat", new ChatCallbacksTable(name));
    }
}
