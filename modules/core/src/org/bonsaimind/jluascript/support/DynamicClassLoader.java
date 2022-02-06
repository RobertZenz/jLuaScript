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

package org.bonsaimind.jluascript.support;

import java.net.URL;
import java.net.URLClassLoader;

import org.bonsaimind.jluascript.utils.Verifier;

/**
 * The {@link DynamicClassLoader} is an {@link URLClassLoader} extension which
 * allows to add jars dynamically during runtime.
 */
public class DynamicClassLoader extends URLClassLoader {
	/**
	 * Creates a new instance of {@link DynamicClassLoader}.
	 *
	 * @param parent The parent {@link ClassLoader}. Can be {@code null}.
	 */
	public DynamicClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}
	
	/**
	 * Adds the given {@link URL jar} to the classloader.
	 *
	 * @param url The {@link URL jar} to add. Cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@link URL jar} is
	 *         {@code null}.
	 */
	public void addJar(URL url) {
		Verifier.notNull("url", url);
		
		addURL(url);
	}
}
