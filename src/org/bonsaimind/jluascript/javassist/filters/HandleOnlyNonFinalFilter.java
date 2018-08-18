
package org.bonsaimind.jluascript.javassist.filters;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.util.proxy.MethodFilter;

public class HandleOnlyNonFinalFilter implements MethodFilter {
	public static final HandleOnlyNonFinalFilter INSTANCE = new HandleOnlyNonFinalFilter();
	
	public HandleOnlyNonFinalFilter() {
		super();
	}
	
	@Override
	public boolean isHandled(Method method) {
		return !Modifier.isFinal(method.getModifiers());
	}
}
