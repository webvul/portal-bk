package com.kii.beehive.portal.common.utils;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptCheck {


	private final static Compilable  compilable;

	static{

		ScriptEngineManager manager = new ScriptEngineManager();

		ScriptEngine engine = manager.getEngineByName("nashorn");

		compilable = (Compilable)engine;

	}

	public static void checkJSFormat(String script) throws ScriptException {

			compilable.compile(script);

	}

}
