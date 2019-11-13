/*
 * Copyright 2018, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jluascript.lua;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.libs.JLuaScriptLib;
import org.bonsaimind.jluascript.lua.libs.StringExtendingLib;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.OsLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseIoLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;
import org.luaj.vm2.luajc.LuaJC;

public class LuaEnvironment {
	protected DynamicClassLoader classLoader = null;
	protected Globals environment = null;
	
	public LuaEnvironment() {
		super();
		
		classLoader = new DynamicClassLoader(getClass().getClassLoader());
		
		environment = new Globals();
		
		LoadState.install(environment);
		LuaC.install(environment);
		LuaJC.install(environment);
		
		environment.load(new PackageLib());
		
		environment.load(new Bit32Lib());
		environment.load(new CoroutineLib());
		environment.load(new OsLib());
		environment.load(new StringLib());
		environment.load(new TableLib());
		
		environment.load(new JseBaseLib());
		environment.load(new JseIoLib());
		environment.load(new JseMathLib());
		environment.load(new JseOsLib());
		
		environment.load(new JLuaScriptLib(classLoader));
		environment.load(new StringExtendingLib());
	}
	
	public void addDefaultImport(Class<?> clazz) {
		LuaValue coercedStaticClass = LuaUtil.coerceStaticIstance(clazz);
		
		LuaUtil.addStaticInstanceDirect(environment, clazz, coercedStaticClass);
		LuaUtil.addStaticInstancePackage(environment, clazz, coercedStaticClass);
	}
	
	public void addDefaultImport(String className) throws ClassNotFoundException {
		addDefaultImport(classLoader.loadClass(className));
	}
	
	public void execute(File file, List<String> args) throws ScriptExecutionException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		execute(file.toPath(), args);
	}
	
	public void execute(Path file, List<String> args) throws ScriptExecutionException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		if (!Files.isRegularFile(file)) {
			throw new IllegalArgumentException(file.toString() + " is not a file.");
		}
		
		Path absoluteFile = file.toAbsolutePath().normalize();
		
		updateEnvironmentVariables(
				args,
				absoluteFile.getParent().toString(),
				absoluteFile.toString());
		
		try {
			environment.loadfile(absoluteFile.toString()).call();
		} catch (Exception e) {
			ScriptExecutionException exception = new ScriptExecutionException("Failed to execute script: " + absoluteFile.toString(), e);
			exception.setStackTrace(extractLuaStacktrace(absoluteFile, e.getStackTrace()));
			
			throw exception;
		}
	}
	
	public void execute(String script, List<String> args) throws ScriptExecutionException {
		updateEnvironmentVariables(args, "", "");
		
		try {
			environment.load(script).call();
		} catch (Exception e) {
			new ScriptExecutionException("Failed to execute script.", e);
		}
	}
	
	public Globals getEnvironment() {
		return environment;
	}
	
	protected StackTraceElement[] extractLuaStacktrace(Path file, StackTraceElement[] stackTrace) {
		if (stackTrace == null || stackTrace.length == 0) {
			return new StackTraceElement[0];
		}
		
		List<StackTraceElement> luaStackTrace = new ArrayList<>();
		
		String pathAsString = file.getParent().toString();
		
		for (StackTraceElement stackTraceElement : stackTrace) {
			String fileName = stackTraceElement.getFileName();
			
			if (fileName != null && fileName.startsWith(pathAsString)) {
				fileName = fileName.substring(pathAsString.length());
				fileName = fileName.replaceAll("/+", "/");
				
				String methodName = stackTraceElement.getMethodName();
				
				int dollarIndex = stackTraceElement.getClassName().indexOf('$');
				
				if (dollarIndex >= 0) {
					methodName = stackTraceElement.getClassName().substring(dollarIndex + 1);
				}
				
				StackTraceElement luaStackTraceElement = new StackTraceElement(
						"script",
						methodName,
						file.toString(),
						stackTraceElement.getLineNumber());
				
				luaStackTrace.add(luaStackTraceElement);
			}
		}
		
		return luaStackTrace.toArray(new StackTraceElement[luaStackTrace.size()]);
	}
	
	protected void updateEnvironmentVariables(List<String> args, String scriptDirectory, String scriptFile) {
		LuaTable argsTable = new LuaTable();
		
		if (args != null && !args.isEmpty()) {
			for (String arg : args) {
				argsTable.set(argsTable.length() + 1, LuaValue.valueOf(arg));
			}
		}
		
		environment.set("ARGS", argsTable);
		
		environment.set("CWD", System.getProperty("user.dir"));
		environment.set("DIR", System.getProperty("user.dir"));
		environment.set("HOME", System.getProperty("user.home"));
		environment.set("SCRIPT_DIR", scriptDirectory);
		environment.set("SCRIPT_FILE", scriptFile);
		environment.set("DIR", System.getProperty("user.dir"));
		environment.set("WORKING_DIR", System.getProperty("user.dir"));
	}
	
}
