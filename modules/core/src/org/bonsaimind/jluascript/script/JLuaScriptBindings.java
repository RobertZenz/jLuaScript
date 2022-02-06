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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

public class JLuaScriptBindings implements Bindings {
	/**
	 * Creates a new instance of {@link JLuaScriptBindings}.
	 */
	public JLuaScriptBindings() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object put(String name, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
}
