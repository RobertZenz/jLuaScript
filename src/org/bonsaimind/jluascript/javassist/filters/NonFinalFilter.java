
package org.bonsaimind.jluascript.javassist.filters;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.util.proxy.MethodFilter;

public class NonFinalFilter implements MethodFilter {
	public static final NonFinalFilter INSTANCE = new NonFinalFilter();
	
	public NonFinalFilter() {
		super();
	}
	
	@Override
	public boolean isHandled(Method method) {
		return !Modifier.isFinal(method.getModifiers());
	}
}
