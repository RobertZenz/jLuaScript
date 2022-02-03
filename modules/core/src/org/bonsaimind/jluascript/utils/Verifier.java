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

package org.bonsaimind.jluascript.utils;

import java.util.Collection;

/**
 * The {@link Verifier} is a static utility class for verifying parameters and
 * arguments.
 */
public final class Verifier {
	/**
	 * No instance required.
	 */
	private Verifier() {
	}
	
	/**
	 * Verifies that the given {@link Object value} is not {@code null},
	 * otherwise throws an {@link IllegalArgumentException} with the name in the
	 * exception message.
	 * 
	 * @param <VALUE_TYPE> The type of the value that is being checked.
	 * @param name The name of the given {@link Object value} which should be
	 *        included in the exception message. Should not be {@code null}.
	 * @param value The {@link Object value} to check.
	 * @return The checked {@code value}.
	 * @throws IllegalArgumentException If the given {@link Object value} is
	 *         {@code null}.
	 */
	public static final <VALUE_TYPE> VALUE_TYPE notNull(String name, VALUE_TYPE value) {
		if (value == null) {
			throw new IllegalArgumentException(String.format("\"%s\" is null.",
					name));
		}
		
		return value;
	}
	
	/**
	 * Verifies that the given {@link Collection value} is not {@code null} or
	 * {@link Collection#isEmpty() empty}, otherwise throws an
	 * {@link IllegalArgumentException} with the name in the exception message.
	 * 
	 * @param <COLLECTION_TYPE> The type of the {@link Collection}.
	 * @param <VALUE_TYPE> The type of the value that is being held in the
	 *        {@link Collection}.
	 * @param name The name of the given {@link Collection value} which should
	 *        be included in the exception message. Should not be {@code null}.
	 * @param value The {@link Collection value} to check.
	 * @return The checked {@code value}.
	 * @throws IllegalArgumentException If the given {@link Collection value} is
	 *         {@code null} or {@link Collection#isEmpty() empty}.
	 */
	public static final <COLLECTION_TYPE extends Collection<VALUE_TYPE>, VALUE_TYPE> COLLECTION_TYPE notNullOrEmpty(String name, COLLECTION_TYPE value) {
		if (value == null) {
			throw new IllegalArgumentException(String.format("\"%s\" is null.",
					name));
		}
		
		if (value.isEmpty()) {
			throw new IllegalArgumentException(String.format("\"%s\" is empty.",
					name));
		}
		
		return value;
	}
	
	/**
	 * Verifies that the given {@link String value} is not {@code null} or
	 * {@link String#isEmpty() empty}, otherwise throws an
	 * {@link IllegalArgumentException} with the name in the exception message.
	 * 
	 * @param name The name of the given {@link String value} which should be
	 *        included in the exception message. Should not be {@code null}.
	 * @param value The {@link String value} to check.
	 * @return The checked {@code value}.
	 * @throws IllegalArgumentException If the given {@link String value} is
	 *         {@code null} or {@link String#isEmpty() empty}.
	 */
	public static final String notNullOrEmpty(String name, String value) {
		if (value == null) {
			throw new IllegalArgumentException(String.format("\"%s\" is null.",
					name));
		}
		
		if (value.isEmpty()) {
			throw new IllegalArgumentException(String.format("\"%s\" is empty.",
					name));
		}
		
		return value;
	}
}
