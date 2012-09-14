# Create various primitive array lists

import sys

types = ['int', 'long', 'float', 'double', 'char', 'byte', 'short', 'boolean']
for atype in types:

	# read file
	f = open('PrimitiveArrayList.java.template', 'r')
	template = f.read()
	f.close()

	# transform
	fp1 = ''
	fp2 = 'if (array[i] == data) {'
	if (atype == 'float') or (atype == 'double'):
		fp1 = ', <type> delta'
		fp2 = 'if (Math.abs(array[i] - data) <= delta) {'
	template = template.replace('<FP1>', fp1)
	template = template.replace('<FP2>', fp2)

	template = template.replace('<Type>', atype.title())
	template = template.replace('<type>', atype)

	# write file
	dest = atype.title() + 'ArrayList.java'
	d = open(dest, 'w')
	d.write(template)
	d.close()
