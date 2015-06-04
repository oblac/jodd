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