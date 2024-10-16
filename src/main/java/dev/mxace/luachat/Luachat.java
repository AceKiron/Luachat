package dev.mxace.luachat;

import net.fabricmc.api.ClientModInitializer;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;
import org.luaj.vm2.parser.ParseException;

public class Luachat implements ClientModInitializer {
    private Globals server_globals;

    @Override
    public void onInitializeClient() {
        System.out.println("Expected start");

        server_globals = new Globals();
        server_globals.load(new JseBaseLib());
        server_globals.load(new PackageLib());
        server_globals.load(new JseStringLib());

        server_globals.load(new JseMathLib());
        LoadState.install(server_globals);
        LuaC.install(server_globals);

        try {
            runScriptInSandbox("print('Hey')");
        } catch (ParseException ex) {
            System.out.println("parse failed: " + ex.getMessage() + "\n"
                    + "Token Image: '" + ex.currentToken.image + "'\n"
                    + "Location: " + ex.currentToken.beginLine + ":" + ex.currentToken.beginColumn
                    + "-" + ex.currentToken.endLine + "," + ex.currentToken.endColumn);
        }

        System.out.println("Expected stop");
    }

    private void runScriptInSandbox(String script) throws ParseException {
        Globals user_globals = new Globals();

        user_globals.load(new JseBaseLib());
        user_globals.load(new PackageLib());
        user_globals.load(new Bit32Lib());
        user_globals.load(new TableLib());
        user_globals.load(new JseStringLib());
        user_globals.load(new JseMathLib());

        user_globals.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                System.out.println(luaValue.checkjstring());
                return LuaValue.NIL;
            }
        });

        LuaValue chunk = server_globals.load(script, "main", user_globals);

        chunk.call();
    }
}
