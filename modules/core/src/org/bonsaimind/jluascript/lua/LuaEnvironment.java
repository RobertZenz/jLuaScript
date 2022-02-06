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
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.libs.ClassImportLib;
import org.bonsaimind.jluascript.lua.libs.DefaultImportsLib;
import org.bonsaimind.jluascript.lua.libs.FileSystemLib;
import org.bonsaimind.jluascript.lua.libs.JarLoaderLib;
import org.bonsaimind.jluascript.lua.libs.LuaJavaInteropLib;
import org.bonsaimind.jluascript.lua.libs.ProcessLib;
import org.bonsaimind.jluascript.lua.libs.StringExtendingLib;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.coercers.DefaultCoercer;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.bonsaimind.jluascript.support.ShebangSkippingInputStream;
import org.bonsaimind.jluascript.support.ShebangSkippingReader;
import org.bonsaimind.jluascript.utils.Verifier;
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
 * This class is not thread-safe. If you need to use this from different
 * threads, you must synchronize access to it yourself.
 * <p>
 * Extending classes can override the {@link #loadDefaultLibraries()} method if
 * they need to customize the libraries which are being loaded into the
 * environment.
 */
public class LuaEnvironment {
	/** The {@link DynamicClassLoader} which is being used. */
	protected DynamicClassLoader classLoader = null;
	/** The {@link Coercer} that is being used. */
	protected Coercer coercer = null;
	/** The Lua environment. */
	protected Globals environment = null;
	
	/**
	 * Creates a new instance of {@link LuaEnvironment}.
	 */
	public LuaEnvironment() {
		this(new DynamicClassLoader(LuaEnvironment.class.getClassLoader()), new DefaultCoercer());
	}
	
	/**
	 * Creates a new instance of {@link LuaEnvironment}.
	 * 
	 * @param classLoader The {@link DynamicClassLoader} to use for loading
	 *        classes, cannot be {@code null}.
	 * @throws IllegalStateException If {@code classLoader} is {@code null}.
	 */
	public LuaEnvironment(DynamicClassLoader classLoader) {
		this(classLoader, new DefaultCoercer());
	}
	
	/**
	 * Creates a new instance of {@link LuaEnvironment}.
	 * 
	 * @param classLoader The {@link DynamicClassLoader} to use for loading
	 *        classes, cannot be {@code null}.
	 * @param coercer The {@link Coercer} that will be used, cannot be
	 *        {@code null}.
	 * @throws IllegalStateException If {@code classLoader} or {@code coercer}
	 *         is {@code null}.
	 */
	public LuaEnvironment(DynamicClassLoader classLoader, Coercer coercer) {
		super();
		
		Verifier.notNull("classLoader", classLoader);
		Verifier.notNull("coercer", coercer);
		
		this.classLoader = classLoader;
		this.coercer = coercer;
		
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
		Verifier.notNullOrEmpty("luaVariableName", luaVariableName);
		
		if (value != null && value instanceof Class<?>) {
			environment.set(luaVariableName, coercer.coerceClassToStaticLuaInstance((Class<?>)value));
		} else {
			environment.set(luaVariableName, coercer.coerceJavaToLua(value));
		}
		
		return this;
	}
	
	/**
	 * Executes the given {@link File} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param <RETURN_TYPE> The type of the returned value. Note that an
	 *        explicit cast is being performed on the value returned by the
	 *        script, which my lead to a {@link ClassCastException}.
	 * @param file The {@link File} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @return The return value of the executed script. Note that an explicit
	 *         cast to the expected type is performed, therefor a
	 *         {@link ClassCastException} might be thrown if that cast is not
	 *         possile.
	 * @throws ClassCastException If the returned value from the script cannot
	 *         be cast to the expected value.
	 * @throws IllegalArgumentException If the given {@link File} is
	 *         {@code null}.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <RETURN_TYPE> RETURN_TYPE execute(File file, List<? extends Object> args) throws ScriptExecutionException {
		Verifier.notNull("file", file);
		
		return execute(file.toPath(), args);
	}
	
	/**
	 * Executes the given {@link Path} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param <RETURN_TYPE> The type of the returned value. Note that an
	 *        explicit cast is being performed on the value returned by the
	 *        script, which my lead to a {@link ClassCastException}.
	 * @param file The {@link Path} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @return The return value of the executed script. Note that an explicit
	 *         cast to the expected type is performed, therefor a
	 *         {@link ClassCastException} might be thrown if that cast is not
	 *         possile.
	 * @throws ClassCastException If the returned value from the script cannot
	 *         be cast to the expected value.
	 * @throws IllegalArgumentException If the given {@link Path} is
	 *         {@code null}.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <RETURN_TYPE> RETURN_TYPE execute(Path file, List<? extends Object> args) throws ScriptExecutionException {
		Verifier.notNull("file", file);
		
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
			
			return (RETURN_TYPE)coercer.coerceLuaToJava(loadedScript.call());
		} catch (Exception e) {
			ScriptExecutionException exception = new ScriptExecutionException("Failed to execute script <" + file.toString() + ">.", e);
			exception.setStackTrace(extractLuaStacktrace(getRootCause(e).getStackTrace()));
			
			throw exception;
		}
	}
	
	/**
	 * Executes the given {@link Reader} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param <RETURN_TYPE> The type of the returned value. Note that an
	 *        explicit cast is being performed on the value returned by the
	 *        script, which my lead to a {@link ClassCastException}.
	 * @param script The {@link String} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @return The return value of the executed script. Note that an explicit
	 *         cast to the expected type is performed, therefor a
	 *         {@link ClassCastException} might be thrown if that cast is not
	 *         possile.
	 * @throws ClassCastException If the returned value from the script cannot
	 *         be cast to the expected value.
	 * @throws IllegalArgumentException If the given {@link Reader script} is
	 *         {@code null}.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <RETURN_TYPE> RETURN_TYPE execute(Reader script, List<Object> args) throws ScriptExecutionException {
		Verifier.notNull("script", script);
		
		updateEnvironmentVariables(args, "", "");
		
		try {
			LuaValue loadedScript = environment.load(
					new ShebangSkippingReader(script),
					"@script",
					environment);
			
			return (RETURN_TYPE)coercer.coerceLuaToJava(loadedScript.call());
		} catch (Exception e) {
			ScriptExecutionException exception = new ScriptExecutionException("Failed to execute script.", e);
			exception.setStackTrace(extractLuaStacktrace(getRootCause(e).getStackTrace()));
			
			throw exception;
		}
	}
	
	/**
	 * Executes the given {@link String} with the given arguments.
	 * <p>
	 * This method does perform automatic Shebang stripping on the input.
	 * 
	 * @param <RETURN_TYPE> The type of the returned value. Note that an
	 *        explicit cast is being performed on the value returned by the
	 *        script, which my lead to a {@link ClassCastException}.
	 * @param script The {@link String} to execute, cannot be {@code null}.
	 * @param args The arguments to use, can be {@code null} for none.
	 * @return The return value of the executed script. Note that an explicit
	 *         cast to the expected type is performed, therefor a
	 *         {@link ClassCastException} might be thrown if that cast is not
	 *         possile.
	 * @throws ClassCastException If the returned value from the script cannot
	 *         be cast to the expected value.
	 * @throws IllegalArgumentException If the given {@link String script} is
	 *         {@code null}.
	 * @throws ScriptExecutionException If there was an error when executing the
	 *         given script.
	 */
	public <RETURN_TYPE> RETURN_TYPE execute(String script, List<Object> args) throws ScriptExecutionException {
		Verifier.notNull("script", script);
		
		return execute(new StringReader(script), args);
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
	 * Gets the {@link Object} from the Lua environment with the given name.
	 * Returns {@code null} if there is no variable with that name, or if it is
	 * {@code nil}.
	 * <p>
	 * To check whether a variable exists, use {@link #getEnvironment()} and
	 * access the Lua environment directly.
	 * 
	 * @param <VARIABLE_TYPE> The type of the returned value. Note that an
	 *        explicit cast is being performed on the value returned by the
	 *        script, which my lead to an {@link ClassCastException}.
	 * @param luaVariableName The name of the Lua variable, cannot be
	 *        {@code null} or empty.
	 * @return The value with the given name, {@code null} if there is none or
	 *         is {@code nil}.
	 * @throws ClassCastException If the returned value from the script cannot
	 *         be cast to the expected value.
	 * @throws IllegalArgumentException If the given Lua variable name is
	 *         {@code null} or empty.
	 */
	public <VARIABLE_TYPE> VARIABLE_TYPE getFromEnvironment(String luaVariableName) {
		Verifier.notNullOrEmpty("luaVariableName", luaVariableName);
		
		return (VARIABLE_TYPE)coercer.coerceLuaToJava(environment.get(luaVariableName));
	}
	
	/**
	 * Imports the given {@link Class} into the environment.
	 * 
	 * @param clazz The {@link Class} to import, cannot be {@code null}.
	 * @return This instance.
	 * @throws IllegalArgumentException If the given {@code clazz} is
	 *         {@code null}.
	 */
	public LuaEnvironment importClass(Class<?> clazz) {
		Verifier.notNull("clazz", clazz);
		
		LuaValue coercedStaticClass = coercer.coerceClassToStaticLuaInstance(clazz);
		
		environment.set(clazz.getSimpleName(), coercedStaticClass);
		LuaUtil.addClassByPackage(environment, clazz, coercedStaticClass);
		
		return this;
	}
	
	/**
	 * Imports the given {@link Class} with the given name into the environment.
	 * 
	 * @param className The {@link Class} to import, cannot be {@code null} or
	 *        empty.
	 * @return This instance.
	 * @throws ClassNotFoundException If the given class could not be loaded by
	 *         that name.
	 * @throws IllegalArgumentException If the given {@code className} is
	 *         {@code null} or empty.
	 */
	public LuaEnvironment importClass(String className) throws ClassNotFoundException {
		Verifier.notNull("className", className);
		
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
		Verifier.notNull("library", library);
		
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
		Verifier.notNullOrEmpty("luaVariableName", luaVariableName);
		
		environment.set(luaVariableName, (LuaValue)null);
		
		return this;
	}
	
	/**
	 * Extracts the Lua stacktrace from the given {@link StackTraceElement}s.
	 * 
	 * @param stackTrace The {@link StackTraceElement}s to extract the
	 *        information from.
	 * @return The Lua stacktrace.
	 */
	protected StackTraceElement[] extractLuaStacktrace(StackTraceElement[] stackTrace) {
		if (stackTrace == null || stackTrace.length == 0) {
			return new StackTraceElement[0];
		}
		
		List<StackTraceElement> luaStackTrace = new ArrayList<>();
		
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (isLuaStackTraceElement(stackTraceElement)) {
				StackTraceElement luaStackTraceElement = new StackTraceElement(
						getLuaScriptName(stackTraceElement),
						getLuaMethodName(stackTraceElement),
						getLuaScriptFileName(stackTraceElement),
						stackTraceElement.getLineNumber());
				
				luaStackTrace.add(luaStackTraceElement);
			}
		}
		
		return luaStackTrace.toArray(new StackTraceElement[luaStackTrace.size()]);
	}
	
	/**
	 * Extracts the Lua method name from the given {@link StackTraceElement}.
	 * 
	 * @param stackTraceElement The {@link StackTraceElement} to use.
	 * @return The name of the method.
	 */
	protected String getLuaMethodName(StackTraceElement stackTraceElement) {
		String className = stackTraceElement.getClassName();
		String methodName = stackTraceElement.getMethodName();
		
		int dollarIndex = className.indexOf('$');
		
		if (dollarIndex >= 0) {
			methodName = className.substring(dollarIndex + 1);
		}
		
		return methodName;
	}
	
	/**
	 * Extracts the Lua script filename from the given
	 * {@link StackTraceElement}.
	 * 
	 * @param stackTraceElement The {@link StackTraceElement} to use.
	 * @return The name of the Lua script filename.
	 */
	protected String getLuaScriptFileName(StackTraceElement stackTraceElement) {
		String fileName = stackTraceElement.getFileName();
		
		if (fileName.endsWith("/jluascript.lua")) {
			fileName = fileName.substring(0, fileName.length() - "/jluascript.lua".length()) + ".jluascript";
		}
		
		if (fileName.startsWith("//")) {
			fileName = "./" + fileName.substring(2);
		}
		
		return fileName;
	}
	
	/**
	 * Extracts the Lua script name from the given {@link StackTraceElement}.
	 * 
	 * @param stackTraceElement The {@link StackTraceElement} to use.
	 * @return The name of the script (without end extensions).
	 */
	protected String getLuaScriptName(StackTraceElement stackTraceElement) {
		String className = getLuaScriptFileName(stackTraceElement);
		
		int lastSlashIndex = className.lastIndexOf("/");
		
		if (lastSlashIndex >= 0) {
			className = className.substring(lastSlashIndex + 1);
		}
		
		int lastDotIndex = className.lastIndexOf(".");
		
		if (lastDotIndex >= 0) {
			className = className.substring(0, lastDotIndex);
		}
		
		return className;
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
	 * Checks whether the given {@link StackTraceElement} is part of the
	 * stacktrace from inside the Lua script.
	 * 
	 * @param stackTraceElement The {@link StackTraceElement} to check.
	 * @return {@code true} if the given {@link StackTraceElement} is part of
	 *         the stacktrace from inside the Lua script.
	 */
	protected boolean isLuaStackTraceElement(StackTraceElement stackTraceElement) {
		return stackTraceElement.getFileName() != null
				&& stackTraceElement.getFileName().endsWith(".lua");
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
		
		loadLibrary(new ClassImportLib(classLoader, coercer));
		loadLibrary(new DefaultImportsLib(coercer));
		loadLibrary(new FileSystemLib(coercer));
		loadLibrary(new JarLoaderLib(classLoader));
		loadLibrary(new LuaJavaInteropLib(coercer, environment));
		loadLibrary(new ProcessLib(coercer));
		loadLibrary(new StringExtendingLib(coercer));
	}
	
	/**
	 * Updates the environment variables with the given values.
	 * 
	 * @param args The arguments to use, can be {@code null}.
	 * @param scriptDirectory The current script directory, can be empty if
	 *        there is none.
	 * @param scriptFile The current script file, can be empty if there is non.
	 */
	protected void updateEnvironmentVariables(List<? extends Object> args, String scriptDirectory, String scriptFile) {
		LuaTable argsTable = new LuaTable();
		
		if (args != null && !args.isEmpty()) {
			for (Object arg : args) {
				argsTable.set(argsTable.length() + 1, coercer.coerceJavaToLua(arg));
			}
		}
		
		environment.set("ARGS", argsTable);
		
		environment.set("CWD", System.getProperty("user.dir"));
		environment.set("DIR", System.getProperty("user.dir"));
		environment.set("HOME", System.getProperty("user.home"));
		environment.set("SCRIPT_DIR", scriptDirectory);
		environment.set("SCRIPT_FILE", scriptFile);
		environment.set("WORKING_DIR", System.getProperty("user.dir"));
	}
}
