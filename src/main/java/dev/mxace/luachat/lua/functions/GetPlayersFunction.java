package dev.mxace.luachat.lua.functions;

import net.minecraft.client.MinecraftClient;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class GetPlayersFunction extends ZeroArgFunction {
    private final String name;

    public GetPlayersFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call() {
        LuaTable result = new LuaTable();

        if (MinecraftClient.getInstance().player == null) return result;
        if (MinecraftClient.getInstance().player.getServer() == null) return result;

        for (String username : MinecraftClient.getInstance().player.getServer().getPlayerNames()) {
            result.set(1 + result.length(), LuaString.valueOf(username));
        }

        return result;
    }
}
