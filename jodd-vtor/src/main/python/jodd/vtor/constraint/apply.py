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

import os

marker = '// ---------------------------------------------------------------- common'
common = '''

	/**
	 * Profiles.
	 */
	String[] profiles() default {};

	/**
	 * Severity.
	 */
	int severity() default 0;

	/**
	 * Message.
	 */
	String message() default "jodd.vtor.constraint.$N";
}
'''

def processfile(filename):
	file = open(filename, 'r')
	f = file.read()
	file.close()

	ndx = f.find(marker)
	if ndx == -1:
		return
	print(filename)

	name = filename
	name = name.replace("./", "")
	name = name.replace(".java", "")

	ndx = ndx + len(marker)
	f = f[:ndx] + common

	f = f.replace("$N", name)

	file = open(filename, 'w')
	file.write(f)
	file.close()

for root, dirs, files in os.walk('.'):
	for file in files:
		if file.endswith('java'):
			path = os.path.join(root, file)
			processfile(path)

