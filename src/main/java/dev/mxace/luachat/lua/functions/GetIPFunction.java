package dev.mxace.luachat.lua.functions;

import dev.mxace.luachat.lua.tables.NetworkIPTable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class GetIPFunction extends ZeroArgFunction {
    private final String name;

    public GetIPFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call() {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (serverInfo == null) return LuaValue.NIL;

        String[] splitAddress = serverInfo.address.split(":");
        if (splitAddress.length == 1) return new NetworkIPTable(splitAddress[0], 25565);
        else return new NetworkIPTable(splitAddress[0], Integer.parseInt(splitAddress[1]));
    }
}
