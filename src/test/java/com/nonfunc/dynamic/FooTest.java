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

import org.junit.Assert;
import org.junit.Test;

public class FooTest {

	static final Foo foo = FooFactory.create();

	@Test
	public void reflectionHelperMatchesToString() throws Exception {
		Assert.assertEquals(foo.toString(), 
				new ReflectionLoggingHelper().stringify(foo));
	}	
	
	@Test
	public void javassistHelperMatchesToString() throws Exception {
		Assert.assertEquals(foo.toString(), 
				new JavassistLoggingHelper().stringify(foo));
	}
	
	@Test
	public void codeModelHelperMatchesToString() throws Exception {
		Assert.assertEquals(foo.toString(), 
				new CodeModelLoggingHelper().stringify(foo));
	}	
}