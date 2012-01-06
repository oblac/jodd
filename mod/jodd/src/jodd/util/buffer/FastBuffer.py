# Create various primitive array lists

import sys

types = ['int', 'long', 'float', 'double', 'char', 'byte', 'short', 'boolean']
for atype in types:

	extended = 0
	if atype == 'char':
		extended = 1

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
	template = template.replace('<Type>', atype.title())
	template = template.replace('<type>', atype)

	# remove @@generated tags from template
	java = ''
	for line in iter(template.splitlines()):
		if line.find('@@generated') != -1:
			continue
		java = java + line + '\n'

	dest = 'Fast' + atype.title() + 'Buffer.java'

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

