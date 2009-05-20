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

#-------------------------------------------------------------------------- main

result = ''

print('\n------------\n pant, pant\n------------\n')

# run tasks.py
data = readFile(os.path.join(tmplRoot, 'tasks.py'))
exec(data)

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
