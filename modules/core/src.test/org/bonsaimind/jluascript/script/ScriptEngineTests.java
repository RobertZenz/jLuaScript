
package org.bonsaimind.jluascript.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptEngineTests {
	@Test
	public void test() throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("jluascript");
		
		Assertions.assertEquals(Integer.valueOf(1), engine.eval("return 1"));
	}
}
