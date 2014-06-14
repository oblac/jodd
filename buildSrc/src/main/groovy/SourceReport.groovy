/**
 * The model class of the report of a source file for Coveralls' format.
 */
class SourceReport {
	String name;
	String source;
	List<Integer> coverage;

	public SourceReport(String name, String source, List<Integer> coverage) {
		this.name = name;
		this.source = source;
		this.coverage = coverage;
	}

}