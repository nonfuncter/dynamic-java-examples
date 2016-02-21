package com.nonfunc.dynamic;

import java.util.UUID;

public class FooFactory {
	
	static int created = 0;
	
	public static Foo create() {
		Foo foo = new Foo();
		foo.id = created++;
		foo.code = UUID.randomUUID().toString();
		foo.description = UUID.randomUUID().toString();
		return foo;
	}	
}