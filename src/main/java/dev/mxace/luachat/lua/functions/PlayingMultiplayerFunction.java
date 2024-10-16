package dev.mxace.luachat.lua.functions;

import net.minecraft.client.MinecraftClient;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class PlayingMultiplayerFunction extends ZeroArgFunction {
    private final String name;

    public PlayingMultiplayerFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call() {
        return MinecraftClient.getInstance().getCurrentServerEntry() == null ? LuaBoolean.FALSE : LuaBoolean.TRUE;
    }
}
