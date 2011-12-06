import os, fnmatch, string


def locate(pattern, root=os.curdir):
	for path, dirs, files in os.walk(os.path.abspath(root)):
		for filename in fnmatch.filter(files, pattern):
			yield os.path.join(path, filename)

for packageHtml in locate('package.html'):
	
	# create package name
	
	package = packageHtml
	index = package.find('src')
	if (index == -1):
		continue
	package = package[index + 4:]
	
	index = package.find('package.html')
	if (index == -1):
		continue
	package = package[:index - 1]
	package = package.replace('\\', '.')
	package = package.replace('/', '.')
	
	# create package-info.java path
	
	index = packageHtml.find('package.html')
	if (index == -1):
		continue

	packageInfo = packageHtml[:index] + 'package-info.java'
	
	# load 
	f = open(packageHtml, "r")
	text = f.read()
	text = text.replace("<html><body>\n", "")
	text = text.replace("\n</body></html>", "")
	f.close()
	
	# prepare javadoc
	
	lines = text.split('\n')
	
	# write
	f = open(packageInfo, "w")
	f.write('/**\n');
	for line in lines:
		line = line.strip()
		if (len(line) == 0):
			continue
		f.write(' * ')
		f.write(line)
		f.write('\n')
	f.write(' */\n');
	f.write('package ')
	f.write(package)
	f.write(';')
	f.close()
	
	# done
	print('\t' + package)

print('Done.')