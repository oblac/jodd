// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

import groovyx.net.http.HTTPBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.yaml.snakeyaml.*

import static groovyx.net.http.Method.POST

class CoverallsReporter {

	public void send(List<String> targetSrcDirs, File jacocoReport) {
		String yamlFileContent = new File('.coveralls.yml').text

		Yaml yaml = new Yaml();
		def cmap = yaml.load(yamlFileContent)

		println cmap.travis_job_id
		println cmap.repo_token

		List<SourceReport> sourceReports = createReportList(targetSrcDirs, jacocoReport)

		Report repo = new Report(cmap.travis_job_id, cmap.repo_token, sourceReports)

		String json = repo.toJson()

		postJsonToUrl json, 'https://coveralls.io/api/v1/jobs'
	}


	static List<SourceReport> createReportList(List<File> srcDirs, File jacocoReportFile) {
		// create parser
		XmlParser parser = new XmlParser()

		// skip external DTD validation
		// see http://xerces.apache.org/xerces2-j/features.html#nonvalidating.load-external-dtd
		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)

		// parse
		Node report = parser.parse(jacocoReportFile)

		Map<String, Map<Integer, Integer>> a = [:]

		report.group.each { grp ->
			grp.package.each { pkg ->

				pkg.sourcefile.each { sf ->
					Map<Integer, Integer> cov = a.get("${pkg.@name}/${sf.@name}", [:])

					sf.line.each { ln ->
						Integer lineIndex = ln.@nr.toInteger() - 1

						// jacoco doesn't count hits
						if (ln.@ci.toInteger() > 0) {
							cov[lineIndex] = 1
						} else {
							cov[lineIndex] = 0
						}
					}
				}
			}
		}

		List<SourceReport> reports = new ArrayList<SourceReport>()

		a.each { String filename, Map<Integer, Integer> cov ->

			File sourceFile = srcDirs.collect { new File(it, filename) }.find { it.exists() }

			if (sourceFile == null) {
				return
			}

			String source = sourceFile.text

			List r = [null] * source.readLines().size()

			cov.each { Integer line, Integer hits ->
				r[line] = hits
			}

			// Compute relative path from . via https://gist.github.com/ysb33r/5804364
			String relPath = new File('.').toURI().relativize(sourceFile.toURI()).toString()
			reports.add new SourceReport(relPath, source, r)
		}

		return reports
	}

	static void postJsonToUrl(String json, String url) {
		HTTPBuilder http = new HTTPBuilder(url)

		http.request(POST) { req ->

			req.entity = MultipartEntityBuilder.create().addBinaryBody('json_file', json.getBytes('UTF-8'), ContentType.APPLICATION_JSON, 'json_file').build()

			response.success = { resp, reader ->
				System.out << reader
			}

			response.failure = { resp, reader ->
				System.out << reader
			}
		}
	}

}