import os

marker = '// ---------------------------------------------------------------- common'
common = '''

	/**
	 * Profiles.
	 */
	String[] profiles() default {};

	/**
	 * Severity.
	 */
	int severity() default 0;
}
'''

def processfile(filename):
	file = open(filename, 'r')
	f = file.read()
	file.close()

	ndx = f.find(marker)
	if ndx == -1:
		return
	print(filename)
	ndx = ndx + len(marker)
	f = f[:ndx] + common
	file = open(filename, 'w')
	file.write(f)
	file.close()

for root, dirs, files in os.walk('.'):
	for file in files:
		if file.endswith('java'):
			path = os.path.join(root, file)
			processfile(path)

