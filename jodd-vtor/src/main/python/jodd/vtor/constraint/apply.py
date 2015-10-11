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

	/**
	 * Message.
	 */
	String message() default "jodd.vtor.constraint.$N";
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

	name = filename
	name = name.replace("./", "")
	name = name.replace(".java", "")

	ndx = ndx + len(marker)
	f = f[:ndx] + common

	f = f.replace("$N", name)

	file = open(filename, 'w')
	file.write(f)
	file.close()

for root, dirs, files in os.walk('.'):
	for file in files:
		if file.endswith('java'):
			path = os.path.join(root, file)
			processfile(path)

