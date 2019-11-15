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

/**
 * The {@link ScriptExecutionException} is being thrown if there is an execution
 * error in a script.
 */
public class ScriptExecutionException extends Exception {
	/**
	 * Creates a new instance of {@link ScriptExecutionException}.
	 *
	 * @param message The {@link String message}.
	 */
	public ScriptExecutionException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new instance of {@link ScriptExecutionException}.
	 *
	 * @param message The {@link String message}.
	 * @param cause The {@link Throwable cause}.
	 */
	public ScriptExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Creates a new instance of {@link ScriptExecutionException}.
	 *
	 * @param cause The {@link Throwable cause}.
	 */
	public ScriptExecutionException(Throwable cause) {
		super(cause);
	}
}
