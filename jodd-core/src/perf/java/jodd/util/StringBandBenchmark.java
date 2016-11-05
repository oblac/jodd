package jodd.util;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Run:
 * <code>
 * gw :jodd-core:perf -PStringBandBenchmark
 * </code>
 * <p>
 * Results:
 * <pre>
 * string2: 300
 * string3: 148
 * ------------
 * stringBand2: 262 (it slower then Java, for strings <= 16 chars)
 * stringBand3: 191 (its faster for any longer strings)
 * </pre>
 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class StringBandBenchmark {

	private String[] strings = new String[5];

	@Setup
	public void prepare() {
		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = new RandomString().randomAlphaNumeric(8);
		}
	}

	@Benchmark
	public String string2() {
		return strings[1] + strings[2];
	}

	@Benchmark
	public String stringBand2() {
		return new StringBand(2)
			.append(strings[1])
			.append(strings[2])
			.toString();
	}

	@Benchmark
	public String string3() {
		return strings[1] + strings[2] + strings[3];
	}

	@Benchmark
	public String stringBand3() {
		return new StringBand(3)
			.append(strings[1])
			.append(strings[2])
			.append(strings[3])
			.toString();
	}
}
