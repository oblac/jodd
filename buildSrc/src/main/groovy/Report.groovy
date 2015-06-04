import groovy.json.*

/**
 * The model class of the report for Coveralls' format.
 */
class Report {
	String service_job_id;
	String service_name;
	String repo_token;
	List<SourceReport> source_files;

	public Report(Integer serviceJobId, String repoToken, List<SourceReport> sourceFiles) {
		this.service_name = "travis-ci";
		this.service_job_id = String.valueOf(serviceJobId);
		this.repo_token = repoToken;
		this.source_files = sourceFiles;
	}

	public String toJson() {
		JsonBuilder json = new JsonBuilder(this)
		return json.toString()
	}
}