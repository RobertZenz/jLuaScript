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

package org.bonsaimind.jluascript.lua.libs.functions.extensions;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;

/**
 * The {@link IteratorIPairsFunction} is an {@link AbstractIteratorFunction}
 * extension which is a {@code pairs} replacement and allows to iterate over
 * Java objects directly.
 */
public class IteratorPairsFunction extends AbstractIteratorFunction {
	/**
	 * Creates a new instance of {@link IteratorIPairsFunction}.
	 *
	 * @param originalPairsFunction The {@link LuaValue original pairs function}
	 *        to replace, {@code null} if there is none.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@link Coercer} is
	 *         {@code null}.
	 */
	public IteratorPairsFunction(LuaValue originalPairsFunction, Coercer coercer) {
		super(originalPairsFunction, Verifier.notNull("coercer", coercer));
	}
}
