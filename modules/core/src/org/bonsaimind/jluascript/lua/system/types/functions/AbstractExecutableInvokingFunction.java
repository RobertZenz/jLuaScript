/*
 * Copyright 2020, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.lua.system.types.functions;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.reflection.LuaFunctionInvokingInvocationHandler;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * The {@link AbstractExecutableInvokingFunction} is an {@link VarArgFunction}
 * provides the base for invoking a Java {@link Executable} from Lua with
 * variable arguments.
 * 
 * @param <EXECUTABLE_TYPE> The type of the {@link Executable} that is being
 *        executed.
 */
public abstract class AbstractExecutableInvokingFunction<EXECUTABLE_TYPE extends Executable> extends VarArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The {@link List} of {@link Executable}s to invoke. */
	protected List<EXECUTABLE_TYPE> executables = null;
	
	/**
	 * Creates a new instance of {@link AbstractExecutableInvokingFunction}.
	 *
	 * @param executables The {@link List} of {@link Executable}s to invoke,
	 *        cannot be {@code null} or empty.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code executables} are
	 *         {@code null} or empty or the {@code coercer} is {@code null}.
	 */
	public AbstractExecutableInvokingFunction(List<EXECUTABLE_TYPE> executables, Coercer coercer) {
		super();
		
		Verifier.notNullOrEmpty("executables", executables);
		Verifier.notNull("coercer", coercer);
		
		this.executables = executables;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Varargs invoke(Varargs args) {
		List<Object> parameters = coerceToJavaParameters(args);
		EXECUTABLE_TYPE executable = findMatchingExecutable(parameters);
		
		if (executable == null) {
			StringBuilder candidates = new StringBuilder();
			
			for (EXECUTABLE_TYPE candidateExecutable : executables) {
				candidates.append("        ")
						.append(getMethodSignature(candidateExecutable))
						.append("\n");
			}
			
			throw new LuaError(String.format("No matching method found for <%s>, possible candidates are: \n%s",
					getRequestedMethodSignature(parameters),
					candidates.toString()));
		}
		
		for (int index = 0; index < parameters.size(); index++) {
			Object parameter = parameters.get(index);
			
			if (parameter instanceof LuaFunctionParameter) {
				Class<?> parameterType = executable.getParameterTypes()[Math.min(index, executable.getParameterTypes().length - 1)];
				
				parameters.set(index, Proxy.newProxyInstance(
						getClass().getClassLoader(),
						new Class<?>[] { parameterType },
						new LuaFunctionInvokingInvocationHandler(((LuaFunctionParameter)parameter).getLuaFunction(), coercer)));
			}
		}
		
		if (hasVargArgsParameter(executable)) {
			foldVarargs(executable, parameters);
		}
		
		try {
			return coercer.coerceJavaToLua(execute(executable, parameters));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
	
	/**
	 * Coerces the given {@link Varargs} into a {@link List} of Java
	 * {@link Object}s.
	 * 
	 * @param args The {@link Varargs} to coerce, cannot be {@code null}.
	 * @return The {@link List} of coerced {@link Object}s, never {@code null}
	 *         but may be empty.
	 * @throws IllegalArgumentException If the given {@code args} is
	 *         {@code null}.
	 */
	protected List<Object> coerceToJavaParameters(Varargs args) {
		Verifier.notNull("args", args);
		
		List<Object> javaParameters = new ArrayList<>();
		
		for (int index = 1; index <= args.narg(); index++) {
			LuaValue arg = args.arg(index);
			
			if (arg.isfunction()) {
				javaParameters.add(new LuaFunctionParameter(arg));
			} else {
				javaParameters.add(coercer.coerceLuaToJava(arg));
			}
		}
		
		return javaParameters;
	}
	
	/**
	 * Executes the given {@link Executable} with the given parameters.
	 * 
	 * @param executable The {@link Executable}, cannot be {@code null}.
	 * @param parameters The {@link List} of parameters, cannot be {@code null}
	 *        but may be empty.
	 * @return The return value of the {@link Executable}s execution.
	 * @throws Exception Any thrown by the given {@link Executable}.
	 */
	protected abstract Object execute(EXECUTABLE_TYPE executable, List<Object> parameters) throws Exception;
	
	/**
	 * Findes the {@link Executable} which matches the given parameters.
	 * 
	 * @param parameters The parameters that must match, cannot be {@code null}
	 *        but may be empty.
	 * @return The matching {@link Executable}, or {@code null} if none matched.
	 * @throws IllegalArgumentException If the given {@code parameters} are
	 *         {@code null}.
	 */
	protected EXECUTABLE_TYPE findMatchingExecutable(List<Object> parameters) {
		Verifier.notNull("parameters", parameters);
		
		for (EXECUTABLE_TYPE executable : executables) {
			if (isMatching(executable, parameters)) {
				return executable;
			}
		}
		
		return null;
	}
	
	/**
	 * Folds any vararg paremters into an array at the end of the given
	 * {@code parameters}.
	 * 
	 * @param executable The {@link Executable} which is going to be executed,
	 *        cannot be {@code null}.
	 * @param parameters The parameters to use, cannot be {@code null} but may
	 *        be empty.
	 * @throws IllegalArgumentException If the given {@code executable} or
	 *         {@code parameters} is {@code null}.
	 */
	protected void foldVarargs(EXECUTABLE_TYPE executable, List<Object> parameters) {
		Verifier.notNull("executable", executable);
		Verifier.notNull("parameters", parameters);
		
		Parameter varArgParameter = executable.getParameters()[executable.getParameterCount() - 1];
		
		if (parameters.size() >= executable.getParameterCount()) {
			if (parameters.size() == executable.getParameterCount()
					&& parameters.get(parameters.size() - 1).getClass().isArray()) {
				// Keep as is.
			} else {
				Object[] varargs = (Object[])Array.newInstance(
						varArgParameter.getType().getComponentType(),
						parameters.size() - executable.getParameterCount() + 1);
				
				for (int index = 0; index < varargs.length; index++) {
					varargs[index] = parameters.remove(executable.getParameterCount() - 1);
				}
				
				parameters.add(varargs);
			}
		} else {
			parameters.add(Array.newInstance(
					varArgParameter.getType().getComponentType(),
					0));
		}
	}
	
	/**
	 * Builds a {@link String} of the method signature of the given
	 * {@link Executable} for debugging/logging purposes.
	 * 
	 * @param executable The {@link Executable} from which to build the
	 *        signature, cannot be {@code null}.
	 * @return The method siganture.
	 * @throws IllegalArgumentException If the given {@code executable} is
	 *         {@code null}.
	 */
	protected String getMethodSignature(EXECUTABLE_TYPE executable) {
		Verifier.notNull("executable", executable);
		
		StringBuilder signature = new StringBuilder();
		
		signature
				.append("(")
				.append(executable.getDeclaringClass().getPackage().getName())
				.append(")")
				.append(executable.getDeclaringClass().getSimpleName());
		
		if (Modifier.isStatic(executable.getModifiers()) || executable instanceof Constructor<?>) {
			signature.append(".");
		} else {
			signature.append(":");
		}
		
		if (executable instanceof Constructor<?>) {
			signature.append("new");
		} else {
			signature.append(executable.getName());
		}
		
		signature.append("(");
		
		for (Parameter parameter : executable.getParameters()) {
			signature.append(parameter.getParameterizedType().getTypeName());
			signature.append(", ");
		}
		
		if (executable.getParameters().length > 0) {
			signature.delete(signature.length() - 2, signature.length());
		}
		
		signature.append(")");
		
		return signature.toString();
	}
	
	/**
	 * Builds a {@link String} of the signature of the given parameters for
	 * debugging/logging purposes.
	 * 
	 * @param parameters The parameters from which to build the signature,
	 *        cannot be {@code null} but may be empty.
	 * @return The method siganture.
	 * @throws IllegalArgumentException If the given {@code parameters} is
	 *         {@code null}.
	 */
	protected String getRequestedMethodSignature(List<Object> parameters) {
		Verifier.notNull("parameters", parameters);
		
		StringBuilder methodSignature = new StringBuilder();
		
		for (Object parameter : parameters) {
			if (parameter != null) {
				methodSignature.append(parameter.getClass().getName())
						.append(", ");
			} else {
				methodSignature.append("nil, ");
			}
		}
		
		if (!parameters.isEmpty()) {
			methodSignature.delete(methodSignature.length() - 2, methodSignature.length());
		}
		
		return methodSignature.toString();
	}
	
	/**
	 * Tests if the given {@link Executable} has a varargs parameter.
	 * 
	 * @param executable The {@link Executable} to test, cannot be {@code null}.
	 * @return {@code true} if the given {@link Executable} has a varargs
	 *         parameter.
	 * @throws IllegalArgumentException If the given {@code executable} is
	 *         {@code null}.
	 */
	protected boolean hasVargArgsParameter(Executable executable) {
		Verifier.notNull("executable", executable);
		
		return executable.getParameterCount() > 0
				&& executable.getParameters()[executable.getParameterCount() - 1].isVarArgs();
	}
	
	/**
	 * Tests if given {@link Class} is a functional interface.
	 * 
	 * @param clazz The {@link Class} to test, cannot be {@code null}.
	 * @return {@code true} if the given {@link Class} is a functional
	 *         interface.
	 * @throws IllegalArgumentException If the given {@code clazz} is
	 *         {@code null}.
	 */
	protected boolean isFunctionalInterface(Class<?> clazz) {
		Verifier.notNull("clazz", clazz);
		
		if (!clazz.isInterface()) {
			return false;
		}
		
		for (Method method : clazz.getMethods()) {
			if (!method.isDefault() && !Modifier.isAbstract(method.getModifiers())) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Tests if the given {@link Class}es can be considered matching.
	 * 
	 * @param expectedClass The {@link Class} that is being expected, cannot be
	 *        {@code null}.
	 * @param clazz The {@link Class} that is given, cannot be {@code null}.
	 * @return {@code true} if the two given {@link Class}es can be considered
	 *         the same.
	 * @throws IllegalArgumentException If the given {@code expectedClass} or
	 *         {@code clazz} is {@code null}.
	 */
	protected boolean isMatching(Class<?> expectedClass, Class<?> clazz) {
		Verifier.notNull("expectedClass", expectedClass);
		Verifier.notNull("clazz", clazz);
		
		return expectedClass.isAssignableFrom(clazz)
				|| (expectedClass == byte.class && clazz == Byte.class)
				|| (expectedClass == short.class && (clazz == Byte.class
						|| clazz == Short.class))
				|| (expectedClass == int.class && (clazz == Byte.class
						|| clazz == Short.class
						|| clazz == Integer.class))
				|| (expectedClass == long.class && (clazz == Byte.class
						|| clazz == Short.class
						|| clazz == Integer.class
						|| clazz == Long.class))
				|| (expectedClass == float.class && clazz == Float.class)
				|| (expectedClass == double.class && (clazz == Float.class || clazz == Double.class));
	}
	
	/**
	 * Tests if the given parameters match the given {@link Executable}.
	 * 
	 * @param executable The {@link Executable} that is wanted, cannot be
	 *        {@code null}.
	 * @param parameters The parameters that should match, cannot be
	 *        {@code null} but may be empty.
	 * @return {@code true} if the given parameters match the given
	 *         {@link Executable}.
	 * @throws IllegalArgumentException If the given {@code executable} or
	 *         {@code parameters} is {@code null}.
	 */
	protected boolean isMatching(Executable executable, List<Object> parameters) {
		Verifier.notNull("executable", executable);
		Verifier.notNull("parameters", parameters);
		
		Parameter[] methodParameters = executable.getParameters();
		
		if (parameters.size() != methodParameters.length
				&& (methodParameters.length <= 0
						|| !methodParameters[methodParameters.length - 1].isVarArgs())) {
			return false;
		}
		
		for (int parameterIndex = 0; parameterIndex < methodParameters.length; parameterIndex++) {
			Parameter methodParameter = methodParameters[parameterIndex];
			
			if (methodParameter.isVarArgs()) {
				// Test if the remaining parameters are matching.
				Class<?> methodParameterType = methodParameter.getType();
				
				if (parameterIndex < parameters.size()) {
					Object parameter = parameters.get(parameterIndex);
					
					if (parameter.getClass().isArray()) {
						if (!methodParameterType.isAssignableFrom(parameter.getClass())) {
							return false;
						}
					} else {
						Class<?> methodParameterClass = methodParameterType.getComponentType();
						
						for (; parameterIndex < parameters.size(); parameterIndex++) {
							parameter = parameters.get(parameterIndex);
							
							if (!methodParameterClass.isAssignableFrom(parameter.getClass())) {
								return false;
							}
						}
					}
				}
			} else {
				if (parameterIndex >= parameters.size()) {
					return false;
				}
				
				Object parameter = parameters.get(parameterIndex);
				
				if (parameter != null) {
					if (parameter instanceof LuaFunctionParameter) {
						if (!isFunctionalInterface(methodParameter.getType())) {
							return false;
						}
					} else if (!isMatching(methodParameter.getType(), parameter.getClass())) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * A simple wrapper around a {@link LuaValue} that is a function.
	 */
	protected static class LuaFunctionParameter {
		/** The function. */
		protected LuaValue luaFunction = null;
		
		/**
		 * Creates a new instance of {@link LuaFunctionParameter}.
		 *
		 * @param luaFunction The {@link LuaValue Lua function}, cannot be
		 *        {@code null}.
		 * @throws IllegalArgumentException If the given {@code luaFunction} is
		 *         {@code null}.
		 */
		public LuaFunctionParameter(LuaValue luaFunction) {
			super();
			
			Verifier.notNull("luaFunction", luaFunction);
			
			this.luaFunction = luaFunction;
		}
		
		/**
		 * Gets the {@link LuaValue Lua function}.
		 * 
		 * @return the {@link LuaValue Lua function}, cannot be {@code null}.
		 */
		public LuaValue getLuaFunction() {
			return luaFunction;
		}
	}
}
