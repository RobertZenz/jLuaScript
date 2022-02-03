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

package org.bonsaimind.jluascript.lua.libs.functions.interop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bonsaimind.jluascript.lua.libs.functions.AbstractPathAcceptingFunction;
import org.bonsaimind.jluascript.support.ShebangSkippingInputStream;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;

/**
 * The {@link FileLoadingFunction} is an {@link AbstractPathAcceptingFunction}
 * extension which allows to load and execute Lua files from paths.
 */
public class FileLoadingFunction extends AbstractPathAcceptingFunction {
	/** The {@link Globals} to load files into. */
	protected Globals globals = null;
	
	/**
	 * Creates a new instance of {@link FileLoadingFunction}.
	 *
	 * @param globals The {@link Globals} to load files into, cannot be
	 *        {@code null}.
	 * @throws IllegalArgumentException If the given {@code globals} is
	 *         {@code null}.
	 */
	public FileLoadingFunction(Globals globals) {
		super();
		
		Verifier.notNull("globals", globals);
		
		this.globals = globals;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Varargs performAction(Path path) {
		try (InputStream fileStream = Files.newInputStream(path)) {
			return globals.load(
					new ShebangSkippingInputStream(fileStream, StandardCharsets.UTF_8),
					"@" + path.getFileName().toString(),
					"bt",
					globals).invoke();
		} catch (IOException e) {
			throw new LuaError(e);
		}
	}
}
