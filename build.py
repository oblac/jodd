
# settings

prjName = 'Jodd'
prjDescription = 'Jodd - generic purpose open-source Java library and frameworks.'
prjVersion = '3.0.3'

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
module_compile('test', 		 'jdk5', '#production, mail, servlets, junit, emma')
module_build('production, test')
module_javadoc('production')
module_test('jodd.TestJodd')
module_dist('jodd.Jodd')
module_findbugs()

module('jodd-wot')
module_compile('production', 'jdk5', '>jodd.production, servlets, asm, slf4j')
module_compile('test', 		 'jdk5', '>jodd.production, #production, asm, slf4j, hsqldb, h2, junit, emma')
module_build('production, test')
module_javadoc('production')
module_test('jodd.TestJoddWot')
module_dist('jodd.JoddWot')
module_findbugs()

module('jodd-gfx')
module_compile('production', 'jdk5', '')
module_build('production')
module_javadoc('production')
module_dist('jodd.JoddGfx')

project_task('build', 'jodd, jodd-wot, jodd-gfx')
project_task('javadoc', 'jodd, jodd-wot')
project_task('emma', 'jodd, jodd-wot')
project_task('dist', 'jodd, jodd-wot, jodd-gfx')
project_task('findbugs', 'jodd, jodd-wot')
project_clean()
project_target('release', 'clean, build, javadoc, emma, findbugs, dist', 'creates full release')


pack('dist', 'jodd',     'dist', '''
	${jodd.jar}
	${jodd-wot.jar}
	file_id.diz
''', '')
pack('all',  'jodd-all', 'pack-dist', '''
	${jodd.jar}
	${jodd-wot.jar}
	file_id.diz
	etc/javadoc/**
	etc/pant/**
	lib/**
	build*
	pant.py
	${jodd.production.src.dir}/**
	${jodd.production.javadoc.dir}/**
	${jodd.test.src.dir}/**
	${jodd.junit.dir}/**
	${jodd.emma.dir}/**
	${jodd-wot.production.src.dir}/**
	${jodd-wot.production.javadoc.dir}/**
	${jodd-wot.test.src.dir}/**
	${jodd-wot.junit.dir}/**
	${jodd-wot.emma.dir}/**
''', '''
	lib/oracle/*
	${jodd.emma.dir}/instr/**
	${jodd-wot.emma.dir}/instr/**
''')


project_help('''
Module targets
--------------
build - builds all modules
javadoc - generates javadocs
dist - creates all distribution jars
findbugs - generates findbugs report
clean - cleans out folders


Project targets
---------------
clean: cleans all outputs
build: compile all
javadoc: generates javadoc
emma: runs all tests
findbugs: find bugs
dist: builds jars

Pack
----
pack-dist: pack distribution jars
pack-all: pack all
''')
project_footer()
