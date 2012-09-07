# settings
prjName = 'Jodd'
prjId = 'jodd'
prjDescription = 'Jodd - open-source Java utility library and web application frameworks.'
#prjVersion = '3.3.2-' + time_date
prjVersion = '3.3.9-beta'
#prjVersion = '3.3.8'

# vars
copyright = 'Copyright &#169; 2003-' + time_year + ' Jodd Team'

# ant
project()
project_header()

lib('asm')
lib('mail')
lib('servlets')
lib('slf4j')
lib('test')

#---------------------------------------------------------- jodd

module('jodd', '5')
module_compile('src',  'mail, servlets')
module_compile('test', '#src, mail, servlets, test')
module_build('src, test')
module_emma('src', 'test', 'jodd.TestJodd')
module_javadoc('src', moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_findbugs('src')

module_task('javadoc', '.src')
module_task('emma', '.test')
module_task('findbugs', '.src')

artifact_jar_slim(moduleName, 'jodd.src', 'jodd.Jodd')
artifact_jar_sources(moduleName, 'jodd.src')
artifact_jar_javadoc(moduleName, 'jodd.src')

module_task('dist', 'jar.jodd')
module_task('dist-all', 'jar.jodd, jar-sources.jodd, jar-javadoc.jodd')

#---------------------------------------------------------- jodd-wot

module('jodd-wot', '5')
module_compile('src',  '>jodd.src, servlets, asm, slf4j')
module_compile('test', '>jodd.src, #src, servlets, asm, slf4j, test')
module_build('src, test')
module_emma('src', 'test', 'jodd.TestJoddWot')
module_javadoc('src', moduleName.capitalize() + ' Library ${prjVersion}', copyright)
module_findbugs('src')

module_task('javadoc', '.src')
module_task('emma', '.test')
module_task('findbugs', '.src')

moduleName = 'jodd-wot'
artifact_jar_slim(moduleName, 'jodd-wot.src', 'jodd.Jodd')
artifact_jar_sources(moduleName, 'jodd-wot.src')
artifact_jar_javadoc(moduleName, 'jodd-wot.src')

module_task('dist', 'jar.jodd-wot')
module_task('dist-all', 'jar.jodd-wot, jar-sources.jodd-wot, jar-javadoc.jodd-wot')

#---------------------------------------------------------- jodd-joy

module('jodd-joy', '5')
module_compile('src', '>jodd.src, >jodd-wot.src, servlets, slf4j')
module_build('src')

artifact_jar_slim(moduleName, 'jodd-joy.src', '')

module_task('dist', 'jar.jodd-joy')
module_task('dist-all', 'jar.jodd-joy')

#---------------------------------------------------------- project

project_task('build', '.jodd, .jodd-wot, .jodd-joy')
project_task('javadoc', 'build, .jodd, .jodd-wot')
project_task('emma', 'build, .jodd, .jodd-wot')
project_task('findbugs', 'build, .jodd, .jodd-wot')
project_task('dist', 'build, .jodd, .jodd-wot, .jodd-joy')
project_task('dist-all', 'build, .jodd, .jodd-wot, .jodd-joy')

project_clean()

pack_dist = '''
	${jodd.jar}
	${jodd-wot.jar}
	${jodd-joy.jar}
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
	${jodd.src.src-dir}/**
	${jodd.src.javadoc-dir}/**
	${jodd.test.src-dir}/**
	${jodd-wot.src.src-dir}/**
	${jodd-wot.src.javadoc-dir}/**
	${jodd-wot.test.src-dir}/**
	etc/javadoc/**
	build*
'''
pack_all = pack_src + '''
	lib/**
'''
pack('dist', 'jodd', 'dist', pack_dist, '')
pack('mvn',  'jodd-mvn', 'dist-all', pack_maven, '')
pack('src',  'jodd-all', 'pack-dist, pack-mvn', pack_src, '')
pack('all',  'jodd-all-with-dependencies', 'pack-src', pack_all, 'lib/oracle/*')


out('''
	<target name="release">
		<echo>Releasing: ${prjVersion}</echo>

		<antcall target="clean"/>
		<antcall target="build"/>
		<antcall target="javadoc"/>
		<antcall target="emma"/>
		<antcall target="findbugs"/>
		<antcall target="dist-all"/>
		<antcall target="pack-all"/>

		<echo>Done!</echo>
	</target>
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
clean:	cleans all outputs
build:	compile all
javadoc:	generates javadoc
emma:	runs all tests
findbugs:	finds bugs
dist:	builds distribution jars
dist-all:	build all distribution jars
release:	performs all tasks for creating a release


Pack
----
pack-dist:	pack just distribution jars
pack-mvn:	pack distribution, sources and javadoc jars
pack-src:	pack sources and documents
pack-all:	pack all
''')

project_footer()