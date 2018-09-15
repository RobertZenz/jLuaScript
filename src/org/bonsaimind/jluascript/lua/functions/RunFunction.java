
package org.bonsaimind.jluascript.lua.functions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class RunFunction extends VarArgFunction {
	public RunFunction() {
		super();
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		if (args.narg() == 0) {
			return LuaValue.NIL;
		}
		
		List<String> command = new ArrayList<>();
		
		for (int index = 1; index <= args.narg(); index++) {
			LuaValue arg = args.arg(index);
			
			if (!arg.isnil()) {
				if (arg.isboolean()
						|| arg.isnumber()
						|| arg.isstring()) {
					command.add(arg.tojstring());
				} else if (arg.isuserdata()) {
					Object object = LuaUtil.coerceAsJavaObject(arg);
					
					if (object instanceof File) {
						File file = (File)object;
						file = file.getAbsoluteFile();
						
						command.add(file.getAbsolutePath());
					} else if (object instanceof Path) {
						Path path = (Path)object;
						path = path.toAbsolutePath();
						
						command.add(path.toString());
					}
				}
			}
		}
		
		Process process = null;
		
		try {
			process = new ProcessBuilder(command)
					.start();
		} catch (IOException e) {
			throw new LuaError(e);
		}
		
		try {
			return LuaValue.valueOf(process.waitFor());
		} catch (InterruptedException e) {
			throw new LuaError(e);
		}
	}
}
