
package org.bonsaimind.jluascript.script;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;

public class JLuaScriptScriptContext implements ScriptContext {
	/**
	 * Creates a new instance of {@link JLuaScriptScriptContext}.
	 */
	public JLuaScriptScriptContext() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAttribute(String name, int scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getAttributesScope(String name) {
		// TODO Auto-generated method stub
		return 0;
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
	public Writer getErrorWriter() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getReader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getScopes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Writer getWriter() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object removeAttribute(String name, int scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(String name, Object value, int scope) {
		// TODO Auto-generated method stub
		
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
	public void setErrorWriter(Writer writer) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReader(Reader reader) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWriter(Writer writer) {
		// TODO Auto-generated method stub
		
	}
}
