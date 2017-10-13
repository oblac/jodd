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

# Create various primitive array lists

import sys

types = ['int', 'long', 'float', 'double', 'char', 'byte', 'short', 'boolean', 'E']
for atype in types:

	atypeTitle = atype.title()

	extended = 0
	if (atype == 'char') | (atype == 'E'):
		extended = 1

	if atype == 'E':
		atypeTitle = ''

	# read template
	f = open('FastBuffer.java.template', 'r')
	template = f.read()
	f.close()

	# strip header and footer from template
	if extended == 1:
		strip = ''
		out = 0
		for line in iter(template.splitlines()):
			if line.find('@@generated') != -1:
				out += 1
			if out == 1:
				strip = strip + line + '\n'
		template = strip

	# transform
	template = template.replace('<Type>', atypeTitle)
	template = template.replace('<type>', atype)
	if (atype == 'E'):
		template = template.replace('FastBuffer', 'FastBuffer<E>')
		template = template.replace('public FastBuffer<E>(', 'public FastBuffer(')
		template = template.replace('new E[16][]', '(E[][]) new Object[16][]')
		template = template.replace('new E[newLen][]', '(E[][]) new Object[newLen][]')
		template = template.replace('new E[', '(E[]) new Object[')

	# remove @@generated tags from template
	java = ''
	for line in iter(template.splitlines()):
		if line.find('@@generated') != -1:
			continue
		java = java + line + '\n'

	dest = 'Fast' + atypeTitle + 'Buffer.java'

	# insert template into extended file: read prefix and suffix
	if extended == 1:
		template = java

		f = open(dest, 'r')
		java = f.read()
		f.close()

		prefix = ''
		suffix = ''
		out = 0
		for line in iter(java.splitlines()):
			if out == 0:
				prefix = prefix + line + '\n'
			if line.find('@@generated') != -1:
				out += 1
			if out == 2:
				suffix = suffix + line + '\n'

		java = prefix + template + suffix

	# write file
	d = open(dest, 'w')
	d.write(java)
	d.close()

