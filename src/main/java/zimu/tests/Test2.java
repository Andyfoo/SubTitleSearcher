package zimu.tests;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Test2 {

	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try {
			String script = "function getUrl(){var url='adsf';return url;} getUrl()";
			Compilable compilable = (Compilable) engine;
			CompiledScript JSFunction = compilable.compile(script);
			Object result = JSFunction.eval();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
