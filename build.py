
# settings
prjName = 'Jodd'
prjId = 'jodd'
prjDescription = 'Jodd - generic purpose open-source Java library and frameworks.'
prjVersion = '3.0.6'

# vars
copyright = 'Copyright &#169; 2003-2009 Jodd Team'

# ant
project_header()

lib('asm')
lib('junit, emma')
lib('hsqldb, h2')
lib('mail')
lib('servlets')
lib('slf4j')

module('jodd')
module_compile('production', 'jdk5', 'mail, servlets')
module_javadoc(moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_dist(moduleName)
module_dist_jar('jodd.Jodd')
module_dist_src()
module_dist_doc()
module_compile('test', 		 'jdk5', '#production, mail, servlets, junit, emma')
module_do_build('production, test')
module_do_doc('production')
module_do_test('jodd.TestJodd')
module_do_findbugs()

module('jodd-wot')
module_compile('production', 'jdk5', '>jodd.production, servlets, asm, slf4j')
module_javadoc(moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_dist(moduleName)
module_dist_jar('jodd.JoddWot')
module_dist_src()
module_dist_doc()
module_compile('test', 		 'jdk5', '>jodd.production, #production, asm, slf4j, hsqldb, h2, junit, emma')
module_do_build('production, test')
module_do_doc('production')
module_do_test('jodd.TestJoddWot')
module_do_findbugs()

module('jodd-gfx')
module_compile('production', 'jdk5', '')
module_do_build('production')

project()
project_task('build', 'jodd, jodd-wot, jodd-gfx')
project_task('javadoc', 'jodd, jodd-wot')
project_task('emma', 'jodd, jodd-wot')
project_task('dist', 'jodd, jodd-wot')
project_task2('all', 'dist', 'jodd, jodd.src, jodd.doc, jodd-wot, jodd-wot.src, jodd-wot.doc')
project_task('findbugs', 'jodd, jodd-wot')
project_clean()

project_target('release', 'clean, build, javadoc, emma, findbugs, all', 'creates full release')


pack('dist', 'jodd',     '', '''
	${jodd.jar}
	${jodd-wot.jar}
	file_id.diz
''', '')
pack('dist-srcdoc',  'jodd-all', 'pack-dist', '''
	${jodd.jar}
	${jodd-sources.jar}
	${jodd-javadoc.jar}
	${jodd-wot.jar}
	${jodd-wot-sources.jar}
	${jodd-wot-javadoc.jar}
	file_id.diz
''', '')
pack('all',  'jodd-all-with-dependencies', 'pack-dist-srcdoc', '''
	${jodd.jar}
	${jodd-sources.jar}
	${jodd-javadoc.jar}
	${jodd-wot.jar}
	${jodd-wot-sources.jar}
	${jodd-wot-javadoc.jar}
	file_id.diz
	lib/**
	build*
	pant.py
''', '''
	lib/oracle/*
''')


project_help('''
Module targets
--------------
build:	builds all modules
javadoc:	generates javadocs
dist:	creates distribution jars
findbugs:	generates findbugs report
clean:	cleans out folders


Project targets
---------------
clean: 	cleans all outputs
build:	compile all
javadoc:	generates javadoc
emma:	runs all tests
findbugs:	finds bugs
dist:	builds distribution jars
all:	builds all jars


Pack
----
pack-dist:	pack just distribution jars
pack-all:	pack all
''')
project_footer()
