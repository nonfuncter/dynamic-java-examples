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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompileUtil {

	private static final String JAVA = ".java";

	public static Class<?> compileAndLoad(Path basePath, String className) throws MalformedURLException, ClassNotFoundException {
	
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		Path file = basePath.resolve(className.replaceAll("\\.", File.separator) + JAVA);
		int result = compiler.run(null, null, null, file.toString());
		if (result != 0) {
			throw new IllegalStateException("Unable to compile generated code.");
		}
				
		URL[] urls = new URL[] { basePath.toUri().toURL() };
		@SuppressWarnings("resource")
		ClassLoader classLoader = new URLClassLoader(urls);
		
		return classLoader.loadClass(className);
	}
}