# python ant builder

import glob
import os
import sys
import shutil

tmplRoot = sys.path[0] + '/etc/pant/'

#-------------------------------------------------------------------------- system

# reads file content
def readFile(filename):
	f = open(filename, 'r')
	data = f.read()
	f.close()
	return data

# writes file content
def writeFile(filename, content):
	f = open(filename, 'w')
	f.write(content)
	f.close()

# detects last iteration in iterable
def iterate(iterable):
	it = iter(iterable)
	value = it.__next__()
	for nextvalue in it:
		yield(False, value)
		value = nextvalue
	yield(True, value)

#-------------------------------------------------------------------------- template

# evaluates single string by replacing all properties $[] with appropriate values
# and all arguments with %ARG%
def evaltmpl(string, list = None):
	# replace arguments
	if list != None:
		while True:
			i = string.find('%ARG')
			if i == -1:
				break
			j = string.find('%', i + 4)
			arg = string[i + 4:j]
			arg = str(list[int(arg)])
			string = string[:i] + arg + string[j+1:]
	# replace properties
	while True:
		i = string.find('$[')
		if i == -1:
			break
		j = string.find(']', i)
		data = eval(string[i+2:j])
		string = string[:i] + data + string[j+1:]
	return string

# invokes string by replacing all properties $[] with appropriate values
def out(str):
	global result
	result += evaltmpl(str)

# reads template file and outputs the content
def tmpl(*args):
	args = list(args)
	if len(args) < 1:
		raise AttributeError("No template for filename")
	filename = str(args[0])
	tmpl = readFile(os.path.join(tmplRoot, filename + '.xml'))
	global result
	result += evaltmpl(tmpl, args)

# checks if template exist
def tmplExist(filename):
	return os.path.isfile(os.path.join(tmplRoot, filename + '.xml'))

#-------------------------------------------------------------------------- vars

result = ''

#-------------------------------------------------------------------------- utils

#build comma-separated list from given source list
def buildlist(src, prefix):
	src = src.split(',')
	list = ''
	for m in iterate(src):
		list = list + prefix + m[1].strip()
		if m[0] == False:
			list = list + ', '
	return list

# returns correct source folder of a target (for compile)
def source(target):
	if target == 'production':
		return 'src'
	return target
#-------------------------------------------------------------------------- pant tasks

#defines global library
def lib(lib):
	global libName
	tokens = lib.split(',')
	for token in tokens:
		libName = token.strip()
		tmpl('lib')

# defines new module
def module(mod):
	global moduleName
	moduleName = mod
	out('\n\n\n\t<!-- MODULE : $[moduleName] -->\n')

#define module compiles
def module_compile(dest, jdk, cpath):
	global moduleJdk, target, modid
	target = dest
	moduleJdk = jdk[-1:]
	modid = moduleName + '.' + target

	out('\t<path id="$[modid].classpath">\n')
	tokens = cpath.split(',')
	for token in tokens:
		token = token.strip()
		if (len(token) == 0):
			continue
		if token.startswith('>'):
			out('\t\t<path path="${' + token[1:] + '.out.dir}"/>\n')
		elif token.startswith('#'):
			out('\t\t<path path="${' + moduleName + '.' + token[1:] + '.out.dir}"/>\n')
		else:
			out('\t\t<path refid="lib.' + token + '"/>\n')
	out('\t</path>\n')

	tmpl('module_compile')

#-------------------------------------------------------------------------- main

print('\n------------\n pant, pant\n------------\n')

# run build.py
build = readFile('build.py')
data = ''
f = open('build.py', 'r')
for line in f:
	start = 0
	for c in line:			# count starting tabs
		if c == '\t':
			start += 1
		else:
			break;
	n = line.find('(', start)
	if n != -1:
		method = line[start : n]
		try:
			eval(method)	# is defined method?
		except:
			if tmplExist(method):	# if not a method maybe it is a template
				line = line[0 : start] + "tmpl('" + method + "', " + line[n + 1:]	# template
	data = data + line
f.close()
exec(data)
writeFile('build.xml', result)
