# Copyright (c) 2003-present, Jodd Team (http://jodd.org)
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

import string

f = open('Printf.java', 'w')
f.write('''
package jodd.format;

/**
 * Printf.
 */
public class Printf {''')

prim_types = ['byte', 'char', 'short', 'int', 'long', 'float', 'double', 'boolean', 'String']
wrapper_types = ['Byte', 'Character', 'Short', 'Integer', 'Long', 'Float', 'Double', 'Boolean']

f.write('\n\n\t// ---------------------------------------------------------------- primitives\n')
template = '''
	public static String str(String format, $T value) {
		return new PrintfFormat(format).form(value);
	}
	public static void out(String format, $T value) {
		System.out.println(str(format, value));
	}

'''
for type in prim_types:
	data = string.replace(template, '$T', type)
	f.write(data)

f.write('\n\n\t// ---------------------------------------------------------------- wrappers\n')
template = '''
	public static String str(String format, $T value) {
		return new PrintfFormat(format).form(value.$tValue());
	}
	public static void out(String format, $T value) {
		System.out.println(str(format, value));
	}
	
'''
count = 0
for type in wrapper_types:
	data = string.replace(template, '$T', type)
	data = string.replace(data, '$t', prim_types[count])
	f.write(data)
	count = count + 1

f.write('\n\n\t// ---------------------------------------------------------------- arrays\n')
template = '''
	public static String str(String format, $T[] params) {
		PrintfFormat pf = new PrintfFormat();
		for ($T param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, $T[] params) {
		System.out.println(str(format, params));
	}

'''
for type in prim_types:
	data = string.replace(template, '$T', type)
	f.write(data)

f.write('\n\n\t// ---------------------------------------------------------------- wrapper arrays\n')
template = '''
	public static String str(String format, $T... params) {
		PrintfFormat pf = new PrintfFormat();
		for ($T param : params) {
			format = pf.reinit(format).form(param);
		}
	    return format;
	}
	public static void out(String format, $T... params) {
		System.out.println(str(format, params));
	}
'''
count = 0
for type in wrapper_types:
	data = string.replace(template, '$T', type)
	data = string.replace(data, '$t', prim_types[count])
	f.write(data)
	count = count + 1

f.write('\n\n\t// ---------------------------------------------------------------- object array\n')
f.write('''
	public static String str(String format, Object... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Object param : params) {
			pf.reinit(format);
			if (param instanceof Integer) {
				format = pf.form(((Integer) param).intValue());
			} else if (param instanceof Long) {
				format = pf.form(((Long) param).longValue());
			} else if (param instanceof Character) {
				format = pf.form(((Character) param).charValue());
			} else if (param instanceof Double) {
				format = pf.form(((Double) param).doubleValue());
			} else if (param instanceof Float) {
				format = pf.form(((Float) param).floatValue());
			} else {
				format = pf.form(param.toString());
			}
		}
		return format;
	}
	public static void out(String format, Object... params) {
		System.out.println(str(format, params));
	}

''')

f.write('}')
f.close()
