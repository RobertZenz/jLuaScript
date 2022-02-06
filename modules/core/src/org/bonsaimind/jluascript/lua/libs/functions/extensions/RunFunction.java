/*
 * Copyright 2019, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.lua.libs.functions.extensions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * The {@link RunFunction} is a {@link VarArgFunction} extension which allows to
 * run a new process and wait for it to exit.
 */
public class RunFunction extends VarArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	
	/**
	 * Creates a new instance of {@link RunFunction}.
	 *
	 * @param coercer The {@link Coercer} to use. Cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@link Coercer} is
	 *         {@code null}.
	 */
	public RunFunction(Coercer coercer) {
		super();
		
		Verifier.notNull("coercer", coercer);
		
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
					Object object = coercer.coerceLuaToJava(arg);
					
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
					.inheritIO()
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
