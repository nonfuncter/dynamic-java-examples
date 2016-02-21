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

public class ReflectionLoggingHelper implements LoggingHelper {
		
	@Override
	public String stringify(Object object) throws StringifyException {
		
		StringBuilder sb = new StringBuilder(object.getClass().getSimpleName() + " [");
		
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			
			Object fieldValue;
			try {
				fieldValue = (Object) field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new StringifyException(e);
			}
			 
			sb.append(fieldName);
			sb.append("=");
			sb.append(fieldValue);
			if (i != fields.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		
		return sb.toString();		
	}
}
