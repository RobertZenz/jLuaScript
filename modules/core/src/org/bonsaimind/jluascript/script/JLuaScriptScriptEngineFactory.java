/*
 * Copyright 2022, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.script;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.bonsaimind.jluascript.JLuaScript;
import org.bonsaimind.jluascript.lua.LuaEnvironment;

/**
 * The {@link JLuaScriptScriptEngineFactory} implements the
 * {@link ScriptEngineFactory}.
 */
public class JLuaScriptScriptEngineFactory implements ScriptEngineFactory {
	/** The supported extensions. */
	protected static final List<String> EXTENSIONS = Collections.unmodifiableList(Arrays.asList(
			"jluascript",
			"lua"));
	/** The supported mime types. */
	protected static final List<String> MIME_TYPES = Collections.unmodifiableList(Arrays.asList(
			"application/jluascript",
			"text/jluascript",
			"application/lua",
			"text/lua"));
	/** The short names of this engine. */
	protected static final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(
			"jluascript",
			"lua"));
	
	/**
	 * Creates a new instance of {@link JLuaScriptScriptEngineFactory}.
	 */
	public JLuaScriptScriptEngineFactory() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEngineName() {
		return "jLuaScript";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEngineVersion() {
		return JLuaScript.VERSION;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getExtensions() {
		return EXTENSIONS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguageName() {
		return "Lua";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguageVersion() {
		return JLuaScript.LUA_VERSION;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		StringBuilder methodCall = new StringBuilder();
		
		methodCall.append(obj).append(":").append(m).append("(");
		
		if (args != null && args.length > 0) {
			for (String arg : args) {
				methodCall.append(arg).append(",");
			}
			
			methodCall.deleteCharAt(methodCall.length() - 1);
		}
		
		methodCall.append(")");
		
		return methodCall.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getMimeTypes() {
		return MIME_TYPES;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getNames() {
		return NAMES;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOutputStatement(String toDisplay) {
		return "print(" + toDisplay + ")";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParameter(String key) {
		switch (key) {
			case ScriptEngine.ENGINE:
				return getEngineName();
			
			case ScriptEngine.ENGINE_VERSION:
				return getEngineVersion();
			
			case ScriptEngine.LANGUAGE:
				return getLanguageName();
			
			case ScriptEngine.LANGUAGE_VERSION:
				return getLanguageVersion();
			
			case ScriptEngine.NAME:
				return getNames().get(0);
			
			case "THREADING":
				return null;
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProgram(String... statements) {
		StringBuilder program = new StringBuilder();
		
		if (statements != null && statements.length > 0) {
			for (String statement : statements) {
				program.append(statement).append("\n");
			}
		}
		
		return program.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScriptEngine getScriptEngine() {
		return new JLuaScriptScriptEngine(this, new LuaEnvironment());
	}
}
