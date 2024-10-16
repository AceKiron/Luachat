package dev.mxace.luachat;

import org.luaj.vm2.LuaFunction;

import java.util.ArrayList;
import java.util.List;

public class ChatInstance {
    public static List<LuaFunction> callbacks = new ArrayList<LuaFunction>();
}
