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

package jodd.lagarto;

import jodd.io.StreamUtil;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.util.ClassLoaderUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;

/**

 Benchmark                                  Mode  Cnt     Score     Error  Units
 EmitStringsBenchmark.lagartoDomBuilder    thrpt   10  5198.527 ± 126.427  ops/s
 EmitStringsBenchmark.lagarto_emitStrings  thrpt   10  9519.027 ± 251.129  ops/s
 EmitStringsBenchmark.lagarto_noStrings    thrpt   10  9632.507 ± 113.255  ops/s

 */
@Fork(1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class EmitStringsBenchmark {


	@Benchmark
	public LagartoParser lagarto_noStrings() {
		LagartoParser lagartoParser = new LagartoParser(HTML1, false);
		lagartoParser.parse(new EmptyTagVisitor());
		return lagartoParser;
	}

	@Benchmark
	public LagartoParser lagarto_emitStrings() {
		LagartoParser lagartoParser = new LagartoParser(HTML1, true);
		lagartoParser.parse(new EmptyTagVisitor());
		return lagartoParser;
	}

	@Benchmark
	public LagartoDOMBuilder lagartoDomBuilder() {
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.parse(HTML1);
		return lagartoDOMBuilder;
	}

	static {
		try {
			char[] data = StreamUtil.readChars(ClassLoaderUtil.getResourceAsStream("jodd/lagarto/file1.html"));
			HTML1 = new String(data);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String HTML1;

}
