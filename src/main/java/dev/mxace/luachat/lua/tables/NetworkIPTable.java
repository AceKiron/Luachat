package dev.mxace.luachat.lua.tables;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class NetworkIPTable extends LuaTable {
    public NetworkIPTable(String address, int port) {
        super();

        set("address", LuaString.valueOf(address));
        set("port", LuaInteger.valueOf(port));
    }
}
