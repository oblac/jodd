
# settings
prjName = 'Jodd'
prjId = 'jodd'
prjDescription = 'Jodd - generic purpose open-source Java library and frameworks.'
prjVersion = '3.0.9'

# vars
copyright = 'Copyright &#169; 2003-2010 Jodd Team'

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
module_compile('test', 		 'jdk5', '#production, mail, servlets, junit, emma')
module_do_build('production, test')
module_do_doc('production')
module_do_test('jodd.TestJodd')
module_do_findbugs()
module_dist(moduleName, 'jodd.Jodd')

module('jodd-wot')
module_compile('production', 'jdk5', '>jodd.production, servlets, asm, slf4j')
module_javadoc(moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_compile('test', 		 'jdk5', '>jodd.production, #production, asm, slf4j, hsqldb, h2, junit, emma')
module_do_build('production, test')
module_do_doc('production')
module_do_test('jodd.TestJoddWot')
module_do_findbugs()
module_dist(moduleName, 'jodd.JoddWot')

module('jodd-joy')
module_compile('production', 'jdk5', '>jodd.production, >jodd-wot.production, servlets, slf4j')
module_do_build('production')
module_dist(moduleName)

module('jodd-gfx')
module_compile('production', 'jdk5', '')
module_do_build('production')

project()
project_task('build', '.jodd, .jodd-wot, .jodd-joy, .jodd-gfx')
project_task('javadoc', 'build, .jodd, .jodd-wot')
project_task('emma', 'build, .jodd, .jodd-wot')
project_task('findbugs', 'build, .jodd, .jodd-wot')
project_task('dist', 'build, .jodd, .jodd-wot, .jodd-joy')
project_clean()

project_target('release', 'clean, build, javadoc, emma, findbugs, dist', 'creates full release')


pack_dist = '''
	${jodd.jar}
	${jodd-wot.jar}
	file_id.diz
'''
pack_src = pack_dist + '''
	${jodd.production.src.dir}/**
	${jodd.production.javadoc.dir}/**
	${jodd.test.src.dir}/**
	${jodd-wot.production.src.dir}/**
	${jodd-wot.production.javadoc.dir}/**
	${jodd-wot.test.src.dir}/**
	etc/javadoc/**
	build*
'''
pack_all = pack_src + '''
	lib/**
'''
pack('dist', 'jodd', 'dist', pack_dist, '')
pack('src',  'jodd-all', 'pack-dist', pack_src, '')
pack('all',  'jodd-all-with-dependencies', 'pack-src', pack_all, 'lib/oracle/*')


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


Pack
----
pack-dist:	pack just distribution jars
pack-src:	pack sources and documents
pack-all:	pack all
''')
project_footer()
