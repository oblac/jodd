import sys

if len(sys.argv) < 2:
	print("Need module name")
	sys.exit(1)
	
name = sys.argv[1]

f = open('module.xml', 'r')
template = f.read()
f.close

data = template.replace('[MODULE]', name);

f = open(name + '.xml', 'w')
f.write(data)
f.close


