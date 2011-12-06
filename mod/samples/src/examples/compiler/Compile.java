// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.compiler;

import jodd.compiler.JavaCompiler;

import java.io.IOException;

public class Compile {

	public static void main(String[] args) throws IOException {
		JavaCompiler javaCompiler = new JavaCompiler();
		javaCompiler.setUseJikes(true);
		javaCompiler.setDebug(true);
		javaCompiler.setEncoding("UTF8");
		javaCompiler.setSourceVersion("1.4");
		javaCompiler.setTargetVersion("1.4");
		javaCompiler.compile("d:\\projects\\java\\apps\\jodd\\mod\\samples\\TestClass.java");
	}
}
