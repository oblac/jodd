// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

/**
 * <p>
 * This package assembles proxy classes. You will probably like to keep out from this code:)
 * </p>
 * <h2>Replacements and modification rules</h2>
 * <p>
 * During creation of proxy methods, several replacement and modification happens in order to produce valid proxy subclass.
 * Here is the list of all such rules.
 * </p>
 * <h3>Add all constructors [A1]</h3>
 * <p>
 * Proxy subclass must contain all constructors as target subclass. New constructors simply
 * delegates invocation to the super class. All constructor annotations are copied.
 * </p>
 * <h3>Add the last method in chain [A2]</h3>
 * <p>
 * Last method in proxy chain is the one that simply delegates the invocation to the target method in super class.
 * </p>
 * <h3>Add all type annotations [A3]</h3>
 * <p>
 * Proxy subclass must contain all type annotations as the target one.
 * </p>
 * <h3>Copy all annotations to the first method in proxy method chain [A4]</h3>
 * <p>
 * Proxy methods must contain all type annotations as the target one.
 * </p>
 * <h3>Fix the offset of local variables [F1]</h3>
 * <p>
 * Offset of all local variables has to be incremented by the <em>size</em> of target method argument list.
 * Size of arguments list is the number of 32bit words used by arguments on stack, which means that
 * all types has length of 1 word, except <code>Long</code> and <code>Double</code> that weight 2 words
 * (or one dword).
 * </p>
 * <div style="width:100px;float:left; border:1px solid gray; margin-right:10px">
 * <pre>
 * iconst_1
 * istore_1
 * </pre>
 * </div>
 * <div style="width:100px;float:left; border:1px solid gray;">
 * <pre>
 * iconst_1
 * istore_13
 * </pre>
 * </div>
 * <p style="clear:both">
 * Here is the order of local variables:</p>
 * <ul>
 * <li>0 - always 'this'</li>
 * <li>arguments (if exist)</li>
 * <li>locals (if exist)</li>
 * </ul>
 * <p>
 * Therefore, index 0 is left as it is and offset will not be added to it.
 * </p>
 * <br style="clear:both;">
 * <h3>Replace ProxyTarget.argsCount [R2]</h3>
 * <p>
 * Call to <code>ProxyTarget.argsCount()</code> has to be replaces with hardcoded arguments count.
 * Method call is simply replaces with appropriate load instruction: <code>iload_n</code> where n is in [0. 5];
 * <code>bipush n</code> where n is in byte range; or <code>sipush n</code> when n is in integer range.
 * </p>
 * <br>
 * <h3>Replace ProxyTarget.getArgType [R3]</h3>
 * <p>
 * Call to <code>ProxyTarget.getArgType(int )</code>	has to be replaces with hardcoded argument Class, where argument
 * index is provided as an argument for <code>ProxyTarget.getArgType(int )</code>.
 * Method call is replaced with <code>getClass()</code> call on specified argument. If argument is an primitive
 * then method is replaced with reading the <code>TYPE</code> attribute of appropriate wrapper.
 * </p>
 * <p>
 * One caveat: opcode for pushing argument offset to stack is not removed from the bytecode,
 * due to performance issues of class creation. Instead, this value is poped from the stack before method call is replaced.
 * It is assumed that this value is an integer.
 * </p>
 * <div style="width:400px;float:left; border:1px solid gray; margin-right:10px">
 * <pre>
 * iconst_1
 * invokestatic package/ProxyTarget.getArgClass
 * astore_1
 * iconst_2
 * invokestatic package/ProxyTarget.getArgClass
 * astore_2
 * </pre>
 * </div>
 * <div style="width:400px;float:left; border:1px solid gray;">
 * <pre>
 * (iconst_1
 * pop)
 * aload_1
 * invokevirtual java/lang/Object.getClass
 * astore 13
 * (iconst_2
 * pop)
 * getstatic java/lang/Byte.TYPE
 * astore 14
 * </pre>
 * </div>
 * <br style="clear:both;">
 * <h3>Replace ProxyTarget.getArg [R4]</h3>
 * <p>
 * Call to <code>ProxyTarget.getArg(int )</code> has to be replaces with hardcoded argument value, where
 * index is provided as an argument for <code>ProxyTarget.getArg(int )</code>.
 * If argument is a primitive, its wrapper object will be created.
 * </p>
 * <div style="width:400px;float:left; border:1px solid gray; margin-right:10px">
 * <pre>
 * iconst_1
 * invokestatic package/ProxyTarget.getArg
 * astore_1
 * bipush 6
 * invokestatic package/ProxyTarget.getArg
 * astore_3
 * </pre>
 * </div>
 * <div style="width:400px;float:left; border:1px solid gray;">
 * <pre>
 * aload_1
 * astore_13
 * lload 6
 * invokestatic java/lang/Long.&lt;init&gt;
 * astore 14
 * </pre>
 * </div>
 * <br style="clear:both;">
 * <h3>Replace ProxyTarget.setArg [R5]</h3>
 * <p>
 * Call to <code>ProxyTarget.setArg(Object, int )</code> has to be replaces with hardcoded setting of the argument value,
 * where index is provided as an argument for <code>ProxyTarget.setArg(Object, int )</code>.
 * If argument is a primitive, its wrapper object must be provided.
 * </p>
 * <h3>Replace ProxyTarget.createArgsArray [R6]</h3>
 * <p>
 * Call to <code>ProxyTarget.createArgsArray()</code> has to be replaces with hardcoded creation of an object array,
 * where elements are target method arguments. Primitive arguments are wrapped.
 * </p>
 * <h3>Replace ProxyTarget.invoke [R7]</h3>
 * <p>
 * Call to <code>ProxyTarget.invokeAndGetResult()</code> has to be replaced with call to super target method. Since target methods
 * may have one or more arguments, it is required to push all arguments to the stack prior to call of super target method.
 * Note that <code>aload_0</code> is always the first instruction (load <code>this</code>), no matter how many arguments there are.
 * </p>
 * <div style="width:400px;float:left; border:1px solid gray; margin-right:10px">
 * <pre>
 * invokestatic package/ProxyTarget.invoke
 * </pre>
 * </div>
 * <div style="width:300px;float:left; border:1px solid gray;">
 * <pre>
 * aload_0
 * aload_1
 * iload_2
 * ...
 * invokespecial package/Target.method
 * </pre>
 * </div>
 * <p>
 * Situation here is a bit more complicated since return value must be provided, so the following fixes has to be
 * applied, too.
 * </p>
 * <h3>Fix POP for ProxyTarget.invoke [F3]</h3>
 * <p>
 * When <code>ProxyTarget.invoke()</code> is invoked without assignment, <code>POP/POP2</code> instruction
 * is added afterwards, to remove the value from the stack. For targets that do not return void, this opcode
 * has to be fixed, i.e. removed. (Fact is that targets that return void, do not have POP:).
 * </p>
 * <h3>Fix return value and Fix ASTORE for ProxyTarget.invoke [F4]</h3>
 * <p>
 * When <code>ProxyTarget.invoke()</code> is invoked with assignment, <code>xSTORE</code> instruction
 * is added afterwards, to assign return value to a local variable. Therefore, it has to be loaded again on stack
 * before return.
 * </p>
 * <p>Creates all return values, performs casting for small types.</p>
 * <h3>Replace ProxyTarget.getTargetClass [R9]</h3>
 * <p>
 * Returns the target class.
 * </p>
 * <h3>Replace ProxyTarget.getTargetMethodName [R10]</h3>
 * <p>
 * Returns target method name.
 * </p>
 * <h3>Replace ProxyTarget.getReturnType [R11]</h3>
 * <p>
 * Returns return type of the target method or <code>null</code> if metod returns void.
 * </p>
 * <h3>Fix field access [F5]</h3>
 * <p>
 * Access to advice's fields has to be replaced with access to local fields. In relation with [A5].
 * </p>
 * <h3>Copy advice's fields to proxy [A5]</h3>
 * <p>
 * All fields from advice has to be copied to proxy, with proxy index added to the name, to prevent duplicate names.
 * </p>
 * <h3>Copy and fix advice static constructor [A6/F6]</h3>
 * <p>
 * Static block of an advice should be copied to the proxy, with fixed field access (see F5).
 * </p>
 * <h3>Copy and fix advices default constructors [A7/F7]</h3>
 * <p>
 * Advice's constructor will be copied to regular methods, except first two instructions (calling super constructor) will be
 * ignored. Field access will be fixed (see F5).
 * </p>
 */
package jodd.proxetta.asm;