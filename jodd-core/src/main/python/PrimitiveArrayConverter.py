# Create various primitive array converters

types = ['char']  # 'int', 'long', 'float', 'double', 'char', 'byte', 'short', 'boolean']
for atype in types:

	# read file
	f = open('PrimitiveArrayConverter.java.template', 'r')
	template = f.read()
	f.close()

	# transform
	template = template.replace('<Type>', atype.title())
	template = template.replace('<type>', atype)

	# write file
	dest = atype.title() + 'ArrayConverter.java'
	d = open(dest, 'w')
	d.write(template)
	d.close()
