
package org.bonsaimind.jluascript.lua.system.types.functions;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.reflection.LuaFunctionInvokingInvocationHandler;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public abstract class AbstractExecutableInvokingFunction<EXECUTABLE extends Executable> extends VarArgFunction {
	protected Class<?> clazz = null;
	protected Coercer coercer = null;
	protected String executableName = null;
	protected List<EXECUTABLE> executables = null;
	
	public AbstractExecutableInvokingFunction(Class<?> clazz, List<EXECUTABLE> executables, String executableName, Coercer coercer) {
		super();
		
		this.clazz = clazz;
		this.executables = executables;
		this.executableName = executableName;
		this.coercer = coercer;
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		List<Object> parameters = coerceToJavaParameters(args);
		EXECUTABLE executable = findMatchingExecutable(parameters);
		
		if (executable == null) {
			StringBuilder candidates = new StringBuilder();
			
			for (EXECUTABLE candidateExecutable : executables) {
				candidates.append("        ")
						.append(getMethodSignature(candidateExecutable))
						.append("\n");
			}
			
			throw new LuaError("No matching method found for <"
					+ getRequestedMethodSignature(parameters)
					+ ">, possible candidates are: \n"
					+ candidates);
		}
		
		for (int index = 0; index < parameters.size(); index++) {
			Object parameter = parameters.get(index);
			
			if (parameter instanceof FunctionWrapper) {
				Class<?> parameterType = executable.getParameterTypes()[Math.min(index, executable.getParameterTypes().length - 1)];
				
				parameters.set(index, Proxy.newProxyInstance(
						getClass().getClassLoader(),
						new Class<?>[] { parameterType },
						new LuaFunctionInvokingInvocationHandler(((FunctionWrapper)parameter).getLuaFunction(), coercer)));
			}
		}
		
		if (hasVargArgsParameter(executable)) {
			Parameter varArgParameter = executable.getParameters()[executable.getParameterCount() - 1];
			
			if (parameters.size() >= executable.getParameterCount()) {
				Object[] varargs = (Object[])Array.newInstance(
						varArgParameter.getType().getComponentType(),
						parameters.size() - executable.getParameterCount() + 1);
				
				for (int index = 0; index < varargs.length; index++) {
					varargs[index] = parameters.remove(executable.getParameterCount() - 1);
				}
				
				parameters.add(varargs);
			} else {
				parameters.add(Array.newInstance(
						varArgParameter.getType().getComponentType(),
						0));
			}
		}
		
		try {
			return coercer.coerceJavaToLua(execute(executable, parameters));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
	
	protected List<Object> coerceToJavaParameters(Varargs args) {
		List<Object> javaParameters = new ArrayList<>();
		
		for (int index = 1; index <= args.narg(); index++) {
			LuaValue arg = args.arg(index);
			
			if (arg.isfunction()) {
				javaParameters.add(new FunctionWrapper(arg));
			} else {
				javaParameters.add(coercer.coerceLuaToJava(arg));
			}
		}
		
		return javaParameters;
	}
	
	protected abstract Object execute(EXECUTABLE executable, List<Object> parameters) throws Exception;
	
	protected EXECUTABLE findMatchingExecutable(List<Object> parameters) {
		for (EXECUTABLE executable : executables) {
			if (isMatching(executable, parameters)) {
				return executable;
			}
		}
		
		return null;
	}
	
	protected String getMethodSignature(EXECUTABLE executable) {
		StringBuilder signature = new StringBuilder();
		
		signature.append(executable.getName()).append("(");
		
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
	
	protected String getRequestedMethodSignature(List<Object> parameters) {
		StringBuilder methodSignature = new StringBuilder();
		methodSignature.append(clazz.getSimpleName())
				.append(".")
				.append(executableName)
				.append("(");
		
		for (Object parameter : parameters) {
			if (parameter != null) {
				methodSignature.append(parameter.getClass().getSimpleName())
						.append(", ");
			} else {
				methodSignature.append("nil, ");
			}
		}
		
		if (!parameters.isEmpty()) {
			methodSignature.delete(methodSignature.length() - 2, methodSignature.length());
		}
		
		methodSignature.append(")");
		
		return methodSignature.toString();
	}
	
	protected boolean hasVargArgsParameter(Executable executable) {
		return executable.getParameterCount() > 0
				&& executable.getParameters()[executable.getParameterCount() - 1].isVarArgs();
	}
	
	protected boolean isFunctionalInterface(Class<?> clazz) {
		return clazz.isInterface() && clazz.getMethods().length == 1;
	}
	
	protected boolean isMatching(Class<?> expectedClass, Class<?> clazz) {
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
	
	protected boolean isMatching(Executable executable, List<Object> parameters) {
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
				Class<?> methodParameterClass = methodParameter.getType().getComponentType();
				
				for (; parameterIndex < parameters.size(); parameterIndex++) {
					Object parameter = parameters.get(parameterIndex);
					
					if (!methodParameterClass.isAssignableFrom(parameter.getClass())) {
						return false;
					}
				}
			} else {
				if (parameterIndex >= parameters.size()) {
					return false;
				}
				
				Object parameter = parameters.get(parameterIndex);
				
				if (parameter != null) {
					if (parameter instanceof FunctionWrapper) {
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
	
	protected static class FunctionWrapper {
		protected LuaValue luaFunction = null;
		
		public FunctionWrapper(LuaValue luaFunction) {
			super();
			
			this.luaFunction = luaFunction;
		}
		
		public LuaValue getLuaFunction() {
			return luaFunction;
		}
	}
}
