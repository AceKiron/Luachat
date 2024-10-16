package dev.mxace.luachat;

import dev.mxace.luachat.lua.functions.PrintFunction;
import dev.mxace.luachat.lua.tables.CallbacksTable;
import dev.mxace.luachat.lua.tables.NetworkTable;
import dev.mxace.luachat.lua.tables.PlayerTable;
import dev.mxace.luachat.utils.IOUtils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;
import org.luaj.vm2.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Luachat implements ClientModInitializer {
    private static final String MOD_ID = "luachat";
    private static final String MOD_NAME = "LuaChat";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private Globals server_globals;

    @Override
    public void onInitializeClient() {
        server_globals = new Globals();

        server_globals.load(new JseBaseLib());
        server_globals.load(new PackageLib());
        server_globals.load(new JseStringLib());
        server_globals.load(new JseMathLib());

        LoadState.install(server_globals);
        LuaC.install(server_globals);

        reloadScripts();
    }

    public static Path getLuachatDirectory() {
        return IOUtils.createDirIfNeeded(FabricLoader.getInstance().getGameDir().resolve(MOD_ID));
    }

    public void reloadScripts() {
        ChatInstance.callbacks.clear();

        try {
            runScriptInSandbox(getLuachatDirectory().resolve("index.lua"));
        } catch (ParseException ex) {
            System.out.println("parse failed: " + ex.getMessage() + "\n"
                    + "Token Image: '" + ex.currentToken.image + "'\n"
                    + "Location: " + ex.currentToken.beginLine + ":" + ex.currentToken.beginColumn
                    + "-" + ex.currentToken.endLine + "," + ex.currentToken.endColumn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private LuaValue runScriptInSandbox(Path path) throws ParseException, IOException {
        Globals user_globals = new Globals();

        user_globals.load(new JseBaseLib());
        user_globals.load(new PackageLib());
        user_globals.load(new Bit32Lib());
        user_globals.load(new TableLib());
        user_globals.load(new JseStringLib());
        user_globals.load(new JseMathLib());

        user_globals.set("require", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                try {
                    return runScriptInSandbox(path.getParent().resolve(luaValue.checkjstring() + ".lua"));
                }  catch (ParseException ex) {
                    System.out.println("parse failed: " + ex.getMessage() + "\n"
                            + "Token Image: '" + ex.currentToken.image + "'\n"
                            + "Location: " + ex.currentToken.beginLine + ":" + ex.currentToken.beginColumn
                            + "-" + ex.currentToken.endLine + "," + ex.currentToken.endColumn);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                return LuaValue.NIL;
            }
        });

        user_globals.set("print", new PrintFunction(path.getFileName().toString()));
        user_globals.set("callbacks", new CallbacksTable(path.getFileName().toString()));
        user_globals.set("network", new NetworkTable(path.getFileName().toString()));
        user_globals.set("player", new PlayerTable(path.getFileName().toString()));

        LuaValue chunk = server_globals.load(IOUtils.readFile(path), "main", user_globals);

        return chunk.call();
    }
}
