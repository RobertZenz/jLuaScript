
package org.bonsaimind.jluascript.lua.functions;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class ErrorThrowingFunction extends VarArgFunction {
	protected String message = null;
	
	public ErrorThrowingFunction(String message) {
		super();
		
		this.message = message;
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		throw new LuaError(message);
	}
}
