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

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;

/**
 * The {@link JLuaScriptScriptEngine} implements the {@link ScriptEngine}.
 */
public class JLuaScriptScriptEngine implements ScriptEngine {
	/** The {@link ScriptEngineFactory} from which this instance was created. */
	protected ScriptEngineFactory factory = null;
	
	/** The underlying {@link LuaEnvironment}. */
	protected LuaEnvironment luaEnvironment = null;
	
	/**
	 * Creates a new instance of {@link JLuaScriptScriptEngine}.
	 *
	 * @param factory The {@link ScriptEngineFactory}.
	 * @param luaEnvironment The {@link LuaEnvironment}.
	 */
	public JLuaScriptScriptEngine(ScriptEngineFactory factory, LuaEnvironment luaEnvironment) {
		super();
		
		this.factory = factory;
		this.luaEnvironment = luaEnvironment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bindings createBindings() {
		// TODO Auto-generated method stub
		return new SimpleBindings();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(Reader reader) throws ScriptException {
		try {
			return luaEnvironment.execute(reader, null);
		} catch (ScriptExecutionException e) {
			throw new ScriptException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(Reader reader, Bindings n) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(String script) throws ScriptException {
		try {
			return luaEnvironment.execute(script, null);
		} catch (ScriptExecutionException e) {
			throw new ScriptException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(String script, Bindings n) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String key) {
		return luaEnvironment.getFromEnvironment(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bindings getBindings(int scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScriptContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScriptEngineFactory getFactory() {
		return factory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(String key, Object value) {
		luaEnvironment.addToEnvironment(key, value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBindings(Bindings bindings, int scope) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContext(ScriptContext context) {
		// TODO Auto-generated method stub
		
	}
}
