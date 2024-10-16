package dev.mxace.luachat.lua.tables;

import dev.mxace.luachat.lua.functions.GetIPFunction;

import dev.mxace.luachat.lua.functions.GetPlayersFunction;
import dev.mxace.luachat.lua.functions.PlayingMultiplayerFunction;
import org.luaj.vm2.LuaTable;

public class NetworkTable extends LuaTable {
    public NetworkTable(String name) {
        super();

        set("get_ip", new GetIPFunction(name));
        set("playing_multiplayer", new PlayingMultiplayerFunction(name));
        set("get_players", new GetPlayersFunction(name));
    }
}
