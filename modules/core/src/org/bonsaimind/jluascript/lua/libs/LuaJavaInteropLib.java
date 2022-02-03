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

package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.libs.functions.extensions.IteratorIPairsFunction;
import org.bonsaimind.jluascript.lua.libs.functions.extensions.IteratorPairsFunction;
import org.bonsaimind.jluascript.lua.libs.functions.interop.FileLoadingFunction;
import org.bonsaimind.jluascript.lua.libs.functions.interop.InstanceofFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link LuaJavaInteropLib} is a {@link TwoArgFunction} extension which
 * provides interoperability methods and extends existing ones to make it easier
 * to write Lua code interacting with Java objects.
 */
public class LuaJavaInteropLib extends TwoArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The {@link Globals} to use. */
	protected Globals globals = null;
	
	/**
	 * Creates a new instance of {@link ClassImportLib}.
	 *
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @param globals The {@link Globals} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code classLoader} or
	 *         {@code coercer} is {@code null}.
	 */
	public LuaJavaInteropLib(Coercer coercer, Globals globals) {
		super();
		
		Verifier.notNull("coercer", coercer);
		Verifier.notNull("globals", globals);
		
		this.coercer = coercer;
		this.globals = globals;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("instanceof", new InstanceofFunction());
		environment.set("ipairs", new IteratorIPairsFunction(environment.get("ipairs"), coercer));
		environment.set("loadFile", new FileLoadingFunction(globals));
		environment.set("pairs", new IteratorPairsFunction(environment.get("pairs"), coercer));
		
		return environment;
	}
}
