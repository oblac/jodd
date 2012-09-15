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

