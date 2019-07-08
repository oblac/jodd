# Copyright (c) 2003-present, Jodd Team (http://jodd.org)
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

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