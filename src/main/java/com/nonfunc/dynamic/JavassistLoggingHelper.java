package com.nonfunc.dynamic;

/*
 * #%L
 * em
 * %%
 * Copyright (C) 2016 nonfunc.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class JavassistLoggingHelper implements LoggingHelper {
		
	static LoggingHelper impl = null;
	
	@Override
	public String stringify(Object object) throws StringifyException {
		return javassistToString(object);
	}
	
	private static Class<? extends LoggingHelper> buildImpl(Class clazz) throws StringifyException {
		
		ClassPool classPool = ClassPool.getDefault();
		CtClass ctClass = classPool.makeClass(clazz.getName() + "JavassistLoggingGeneratorImpl");

		try {
			ctClass.addInterface(classPool.getCtClass(LoggingHelper.class.getName()));
			CtMethod ctMethod = createStringifyMethod(ctClass, clazz);
			ctClass.addMethod(ctMethod);
			
			return ctClass.toClass();
		} catch (CannotCompileException | NotFoundException e) {
			throw new StringifyException(e);
		}
	}

	private static CtMethod createStringifyMethod(CtClass newClass, Class clazz) throws CannotCompileException {
		StringBuilder sb = new StringBuilder("public String stringify(Object object) {");
		sb.append("StringBuilder sb = new StringBuilder(\""+ clazz.getSimpleName() +" [\");");
		
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();

			sb.append("sb.append(\"" + fieldName + "=\" + ((" + clazz.getName() + ") object)." + fieldName + ");");

			if (i != fields.length - 1) {
				sb.append("sb.append(\", \");");
			}
		}
		sb.append("sb.append(\"]\");");
		sb.append("return sb.toString();}");
		
		return CtNewMethod.make(sb.toString(), newClass);
	}		
	
	private static String javassistToString(Object object) throws StringifyException {
		if (impl == null) {
			Class clazz = buildImpl(object.getClass());
			try {
				impl = (LoggingHelper) clazz.newInstance();				
			} catch (InstantiationException | IllegalAccessException 
					| SecurityException | IllegalArgumentException e) {
				throw new StringifyException(e);
			}		
		}
		return impl.stringify(object);		
	}
}
