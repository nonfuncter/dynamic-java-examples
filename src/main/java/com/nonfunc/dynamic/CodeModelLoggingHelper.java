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

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

public class CodeModelLoggingHelper implements LoggingHelper {
		
	static LoggingHelper impl = null;
	
	@Override
	public String stringify(Object object) throws StringifyException {
		return codeModelToString(object);		
	}
	
	@SuppressWarnings("unchecked")
	private static Class<? extends LoggingHelper> buildImpl(Class<?> clazz) throws StringifyException {
		
		JCodeModel codeModel = new JCodeModel();
		
		final String packageName = clazz.getPackage().getName();
		JPackage jPackage = codeModel._package(packageName);
		
		final String newClassName =  clazz.getSimpleName() + "CodeModelToStringGeneratorImpl";
		JDefinedClass definedClass;
		try {
			definedClass = jPackage._class(newClassName);
		} catch (JClassAlreadyExistsException e) {
			throw new StringifyException(e);
		}
		
		definedClass._implements(LoggingHelper.class);
		
		createStringifyMethod(clazz, codeModel, definedClass);
		
		Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));

		try {
			codeModel.build(tempDirectory.toFile());
			return (Class<? extends LoggingHelper>) CompileUtil.compileAndLoad(tempDirectory, packageName + "." + newClassName);
		} catch (IOException | ClassNotFoundException e) {
			throw new StringifyException(e);
		}
	}

	private static void createStringifyMethod(Class<?> clazz, JCodeModel codeModel, JDefinedClass definedClass) {
		JMethod stringifyMethod = definedClass.method(JMod.PUBLIC, String.class, "stringify");
		stringifyMethod.param(Object.class, "object");
		JBlock jBlock = stringifyMethod.body();
		JClass stringBuilderClass = codeModel.ref(StringBuilder.class);
		jBlock.decl(stringBuilderClass, "sb", JExpr._new(stringBuilderClass));
		
		jBlock.directStatement("sb.append(\"" + clazz.getSimpleName() + " [\");");				

		Field[] fields = Foo.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			
			Field field = fields[i];						
			String fieldName = field.getName();			
			
			jBlock.directStatement("sb.append(\"" + fieldName + "=\" + ((" + clazz.getName() + ") object)." + fieldName + ");");

			if (i != fields.length - 1) {
				jBlock.directStatement("sb.append(\", \");");
			}
		}
		jBlock.directStatement("sb.append(\"]\");");
		jBlock.directStatement("return sb.toString();");
	}	
	
	private static String codeModelToString(Object object) throws StringifyException {
		if (impl == null) {
			try {
				Class clazz = buildImpl(object.getClass());		
				impl = (LoggingHelper) clazz.newInstance();
			} catch ( InstantiationException | IllegalAccessException 
					| SecurityException | IllegalArgumentException e) {
				throw new StringifyException(e);			
			} 	
		}
		return impl.stringify(object);
	}	
}