package dev.mxace.luachat.lua.tables;

import net.minecraft.client.MinecraftClient;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class PlayerTable extends LuaTable {
    public PlayerTable(String name) {
        super();

        set("username", LuaString.valueOf(MinecraftClient.getInstance().getSession().getUsername()));
    }
}
