package dev.mxace.luachat.lua.functions;

import dev.mxace.luachat.Luachat;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class PrintFunction extends OneArgFunction {
    private final String name;

    public PrintFunction(String name) {
        super();

        this.name = name;
    }

    @Override
    public LuaValue call(LuaValue luaValue) {
        if (luaValue.isnil()) Luachat.LOGGER.info("[" + name + "] nil");
        else Luachat.LOGGER.info("[" + name + "] " + luaValue.checkjstring());
        return LuaValue.NIL;
    }
}
