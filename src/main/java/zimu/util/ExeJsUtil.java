package zimu.util;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ExeJsUtil {
	
	/**
	 * 执行js并返回结果
	 * @param jsStr
	 * @return
	 */
	public static String getJsVal(String jsStr) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try {
			Compilable compilable = (Compilable) engine;
			CompiledScript JSFunction = compilable.compile(jsStr);
			Object result = JSFunction.eval();
			return result != null ? result.toString() : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
