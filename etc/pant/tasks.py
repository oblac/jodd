
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

# defines global library
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
