# Create various primitive array lists

import sys

types = ['int', 'long', 'float', 'double', 'char', 'byte', 'short', 'boolean']
for atype in types:

	# read file
	f = open('FastBuffer.java.template', 'r')
	template = f.read()
	f.close()

	# transform
	template = template.replace('<Type>', atype.title())
	template = template.replace('<type>', atype)

	# write file
	dest = 'Fast' + atype.title() + 'Buffer.java'
	d = open(dest, 'w')
	d.write(template)
	d.close()

