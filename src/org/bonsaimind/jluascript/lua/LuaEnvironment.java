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
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.libs.ClassImportLib;
import org.bonsaimind.jluascript.lua.libs.DefaultImportsLib;
import org.bonsaimind.jluascript.lua.libs.JarLoaderLib;
import org.bonsaimind.jluascript.lua.libs.LuaJavaInteropLib;
import org.bonsaimind.jluascript.lua.libs.ProcessLib;
import org.bonsaimind.jluascript.lua.libs.StringExtendingLib;
import org.bonsaimind.jluascript.lua.libs.UnixLib;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.bonsaimind.jluascript.support.ShebangSkippingInputStream;
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

/**
 * The {@link LuaEnvironment} is the main class which builds and maintains the
 * environment in which the Lua scripts can be executed.
 * <p>
 * This class is not thread-safe. If you need to sue this from different
 * threads, you must synchronize access to it yourself.
 */
public class LuaEnvironment {
	/** The {@link DynamicClassLoader} which is being used. */
	protected DynamicClassLoader classLoader = null;
	/** The Lua environment. */
	protected Globals environment = null;
	
	/**
	 * Creates a new instance of {@link LuaEnvironment}.
	 */
	public LuaEnvironment() {
		this(new DynamicClassLoader(LuaEnvironment.class.getClassLoader()));
	}
	
	/**
	 * Creates a new instance of {@link LuaEnvironment}.
	 * 
	 * @param classLoader The {@link DynamicClassLoader} to use for loading
	 *        classes, cannot be {@code null}.
	 * @throws IllegalStateException If {@code classLoader} is {@code null}.
	 */
	public LuaEnvironment(DynamicClassLoader classLoader) {
		super();
		
		if (classLoader == null) {
			throw new IllegalArgumentException("classLoader cannot be null.");
		}
		
		this.classLoader = classLoader;
		
		environment = new Globals();
		
		// Install the compilers.
		LoadState.install(environment);
		LuaC.install(environment);
		LuaJC.install(environment);
		
		// Load the libraries.
		loadDefaultLibraries();
	}
	
	/**
	 * Adds the given {@link Object} as Lua variable with the given name to the
	 * environment.
	 * 
	 * @param luaVariableName The name of the Lua variable to add, cannot be
	 *        {@code null} or empty.
	 * @param value The {@link Object} to add, can be {@code null}.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given Lua variable name is
	 *         {@code null} or empty.
	 */
	public LuaEnvironment addToEnvironment(String luaVariableName, Object value) {
		if (luaVariableName == null) {
			throw new IllegalArgumentException("luaVariableName cannot be null.");
		}
		
		if (luaVariableName.isEmpty()) {
			throw new IllegalArgumentException("luaVariableName cannot be empty.");
		}
		
		environment.set(luaVariableName, LuaUtil.coerceAsLuaValue(value));
		
		return this;
	}
	
	/**
	 * Executes the given {@link File} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param file The {@link File} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <TYPE> TYPE execute(File file, List<String> args) throws ScriptExecutionException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		return execute(file.toPath(), args);
	}
	
	/**
	 * Executes the given {@link Path} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param file The {@link Path} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <TYPE> TYPE execute(Path file, List<String> args) throws ScriptExecutionException {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		
		if (!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("<" + file.toString() + "> is not a file.");
		}
		
		if (!Files.isReadable(file)) {
			throw new IllegalArgumentException("<" + file.toString() + "> is not readable.");
		}
		
		Path absoluteFile = file.toAbsolutePath().normalize();
		
		updateEnvironmentVariables(
				args,
				absoluteFile.getParent().toString(),
				absoluteFile.toString());
		
		try (InputStream fileStream = Files.newInputStream(file)) {
			LuaValue loadedScript = environment.load(
					new ShebangSkippingInputStream(fileStream, StandardCharsets.UTF_8),
					"@" + file.toString(),
					"bt",
					environment);
			
			return (TYPE)LuaUtil.coerceAsJavaObject(loadedScript.call());
		} catch (Exception e) {
			ScriptExecutionException exception = new ScriptExecutionException("Failed to execute script <" + absoluteFile.toString() + ">.", e);
			exception.setStackTrace(extractLuaStacktrace(absoluteFile, getRootCause(e).getStackTrace()));
			
			throw exception;
		}
	}
	
	/**
	 * Executes the given {@link String} with the given arguments.
	 * <p>
	 * This method does not perform automatic Shebang stripping, any leading
	 * Shebang must be stripped manually before handing in the input.
	 * 
	 * @param script The {@link String} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <TYPE> TYPE execute(String script, List<String> args) throws ScriptExecutionException {
		if (script == null) {
			throw new IllegalArgumentException("script cannot be null.");
		}
		
		updateEnvironmentVariables(args, "", "");
		
		try {
			LuaValue loadedScript = environment.load(
					new StringReader(script),
					"@script",
					environment);
			
			return (TYPE)LuaUtil.coerceAsJavaObject(loadedScript.call());
		} catch (Exception e) {
			throw new ScriptExecutionException("Failed to execute script.", e);
		}
	}
	
	/**
	 * Gets the current Lua environment.
	 * 
	 * @return The current Lua environment.
	 */
	public Globals getEnvironment() {
		return environment;
	}
	
	/**
	 * Imports the given {@link Class} into the environment.
	 * 
	 * @param clazz The {@link Class} to import.
	 * @return This instance.
	 */
	public LuaEnvironment importClass(Class<?> clazz) {
		LuaValue coercedStaticClass = LuaUtil.coerceStaticIstance(clazz);
		
		LuaUtil.addStaticInstanceDirect(environment, clazz, coercedStaticClass);
		LuaUtil.addStaticInstancePackage(environment, clazz, coercedStaticClass);
		
		return this;
	}
	
	/**
	 * Imports the given {@link Class} with the given name into the environment.
	 * 
	 * @param clazz The {@link Class} to import.
	 * @return This instance.
	 */
	public LuaEnvironment importClass(String className) throws ClassNotFoundException {
		importClass(classLoader.loadClass(className));
		
		return this;
	}
	
	/**
	 * Loads the given library into the environment.
	 * 
	 * @param library The library to load, cannot be {@code null}.
	 * @return This instance.
	 * @throws IllegalArgumentException If {@code library} is {@code null}.
	 */
	public LuaEnvironment loadLibrary(LuaValue library) {
		if (library == null) {
			throw new IllegalArgumentException("library cannot be null.");
		}
		
		environment.load(library);
		
		return this;
	}
	
	/**
	 * Removes the Lua variable with the given name from the environment.
	 * 
	 * @param luaVariableName The name of the Lua variable to remove, cannot be
	 *        {@code null} or empty.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given Lua variable name is
	 *         {@code null} or empty.
	 */
	public LuaEnvironment removeFromEnvironment(String luaVariableName) {
		if (luaVariableName == null) {
			throw new IllegalArgumentException("luaVariableName cannot be null.");
		}
		
		if (luaVariableName.isEmpty()) {
			throw new IllegalArgumentException("luaVariableName cannot be empty.");
		}
		
		environment.set(luaVariableName, (LuaValue)null);
		
		return this;
	}
	
	/**
	 * Extracts the Lua stacktrace from the given {@link StackTraceElement}s.
	 * 
	 * @param file The {@link Path file} which has been executed, required for
	 *        the path.
	 * @param stackTrace The {@link StackTraceElement}s to extract the
	 *        information from.
	 * @return The Lua stacktrace.
	 */
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
	
	/**
	 * Gets the root cause of the given {@link Throwable exception}.
	 * 
	 * @param exception The {@link Throwable exception} to start at.
	 * @return The root cause.
	 */
	protected Throwable getRootCause(Throwable exception) {
		Throwable currentException = exception;
		
		while (currentException.getCause() != null) {
			currentException = currentException.getCause();
		}
		
		return currentException;
	}
	
	/**
	 * Loads the default libraries.
	 * <p>
	 * Overriding classes can override this method to customize the available
	 * libraries in the scope. This method is called in the constructor,
	 * {@link #environment} is guaranteed to exist and
	 * {@link #loadLibrary(LuaValue)} can be safely used for loading the
	 * libraries.
	 */
	protected void loadDefaultLibraries() {
		loadLibrary(new PackageLib());
		
		loadLibrary(new Bit32Lib());
		loadLibrary(new CoroutineLib());
		loadLibrary(new OsLib());
		loadLibrary(new StringLib());
		loadLibrary(new TableLib());
		
		loadLibrary(new JseBaseLib());
		loadLibrary(new JseIoLib());
		loadLibrary(new JseMathLib());
		loadLibrary(new JseOsLib());
		
		loadLibrary(new ClassImportLib(classLoader));
		loadLibrary(new DefaultImportsLib());
		loadLibrary(new JarLoaderLib(classLoader));
		loadLibrary(new LuaJavaInteropLib());
		loadLibrary(new ProcessLib());
		loadLibrary(new StringExtendingLib());
		loadLibrary(new UnixLib());
	}
	
	/**
	 * Updates the environment variables with the given values.
	 * 
	 * @param args The arguments to use, can be {@code null}.
	 * @param scriptDirectory The current script directory, can be empty if
	 *        there is none.
	 * @param scriptFile The current script file, can be empty if there is non.
	 */
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
