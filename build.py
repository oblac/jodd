
# settings
prjName = 'Jodd'
prjId = 'jodd'
prjDescription = 'Jodd - open-source Java utility library and web application frameworks.'
prjVersion = '3.1.0'

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
module_dist_sources(moduleName, 'production')
module_dist_javadoc(moduleName, 'production')

module('jodd-wot')
module_compile('production', 'jdk5', '>jodd.production, servlets, asm, slf4j')
module_javadoc(moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_compile('test', 		 'jdk5', '>jodd.production, #production, asm, slf4j, hsqldb, h2, junit, emma')
module_do_build('production, test')
module_do_doc('production')
module_do_test('jodd.TestJoddWot')
module_do_findbugs()
module_dist(moduleName, 'jodd.JoddWot')
module_dist_sources(moduleName, 'production')
module_dist_javadoc(moduleName, 'production')


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
project_task('dist-all', 'dist, dist-sources.jodd, dist-javadoc.jodd, dist-sources.jodd-wot, dist-javadoc.jodd-wot')
project_clean()

project_target('release', 'clean, build, javadoc, emma, findbugs, dist', 'creates full release')


pack_dist = '''
	${jodd.jar}
	${jodd-wot.jar}
	file_id.diz
'''
pack_maven = pack_dist + '''
	${jodd-sources.jar}
	${jodd-javadoc.jar}
	${jodd-wot-sources.jar}
	${jodd-wot-javadoc.jar}
	pom/**
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
pack('mvn',  'jodd-mvn', 'dist', pack_maven, '')
pack('src',  'jodd-all', 'pack-dist', pack_src, '')
pack('all',  'jodd-all-with-dependencies', 'pack-src, pack-mvn', pack_all, 'lib/oracle/*')


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
clean:	cleans all outputs
build:	compile all
javadoc:	generates javadoc
emma:	runs all tests
findbugs:	finds bugs
dist:	builds distribution jars
dist-all:	build all distribution jars


Pack
----
pack-dist:	pack just distribution jars
pack-mvn:	pack distribution, sources and javadoc jars
pack-src:	pack sources and documents
pack-all:	pack all
''')
project_footer()
