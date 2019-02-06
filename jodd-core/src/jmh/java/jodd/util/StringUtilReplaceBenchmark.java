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
package jodd.util;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link StringUtil#replace(String, String, String)} method. <br/>
 * Following methods will be compared:
 * <ol>
 *     <li>{@link String#replace(CharSequence, CharSequence)}</li>
 *     <li>{@link StringUtil#replace(String, String, String)}</li>
 * </ol>
 *
 * Code was originally published on <a href="https://blog.jooq.org/2017/10/11/benchmarking-jdk-string-replace-vs-apache-commons-stringutils-replace/#comment-151887">jOOQ blog</a> , but slightly modified.
 *
 * <p>
 * Run:
 * <code>
 * gw :jodd-core:StringUtilReplaceBenchmark
 * </code>
 * </p>
 *
 * Results:
 * <pre>
 * Benchmark                                                                      Mode  Cnt          Score          Error  Units
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringNoMatch          thrpt   21   30664291.493 ±   506821.904  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringOneMatch         thrpt   21    8469800.841 ±   115709.770  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceLongStringSeveralMatches   thrpt   21    4844528.121 ±   143725.969  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringNoMatch         thrpt   21  210039642.933 ±  4099503.317  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringOneMatch        thrpt   21   13016923.170 ±   614851.521  ops/s
 * StringUtilReplaceBenchmark.apacheStringUtilsReplaceShortStringSeveralMatches  thrpt   21    7339967.841 ±   105845.671  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringNoMatch                     thrpt   21    6309114.450 ±    90470.839  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringOneMatch                    thrpt   21    1854754.382 ±    43681.835  ops/s
 * StringUtilReplaceBenchmark.stringReplaceLongStringSeveralMatches              thrpt   21    1412900.760 ±    22663.776  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringNoMatch                    thrpt   21    7600231.241 ±   116226.095  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringOneMatch                   thrpt   21    4406405.853 ±   146412.852  ops/s
 * StringUtilReplaceBenchmark.stringReplaceShortStringSeveralMatches             thrpt   21    2952897.725 ±    26047.413  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringNoMatch                 thrpt   21   31411667.415 ±   451647.428  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringOneMatch                thrpt   21   10121698.874 ±   152851.456  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceLongStringSeveralMatches          thrpt   21    5091103.201 ±   136616.407  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringNoMatch                thrpt   21  207213639.747 ±  5682502.409  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringOneMatch               thrpt   21   19425478.685 ±   766128.831  ops/s
 * StringUtilReplaceBenchmark.stringUtilReplaceShortStringSeveralMatches         thrpt   21    9633490.491 ±   159050.223  ops/s
 * </pre>
 *
 * <b>Note:</b> in Java 9 the method {@link String#replace(CharSequence, CharSequence)} will perform much better!
 */
@Fork(value = 3, jvmArgsAppend = "-Djmh.stack.lines=3")
@Warmup(iterations = 5)
@Measurement(iterations = 7)
public class StringUtilReplaceBenchmark {

    private static final String SHORT_STRING_NO_MATCH = "abc";
    private static final String SHORT_STRING_ONE_MATCH = "a'bc";
    private static final String SHORT_STRING_SEVERAL_MATCHES = "'a'b'c'";
    private static final String LONG_STRING_NO_MATCH =
            "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc";
    private static final String LONG_STRING_ONE_MATCH =
            "abcabcabcabcabcabcabcabcabcabcabca'bcabcabcabcabcabcabcabcabcabcabcabcabc";
    private static final String LONG_STRING_SEVERAL_MATCHES =
            "abcabca'bcabcabcabcabcabc'abcabcabca'bcabcabcabcabcabca'bcabcabcabcabcabcabc";

    // ----------------------------------------------------------------------- Java String#replace
    
    @Benchmark
    public String stringReplaceShortStringNoMatch() {
        return SHORT_STRING_NO_MATCH.replace("'", "''");
    }

    @Benchmark
    public String stringReplaceLongStringNoMatch() {
        return LONG_STRING_NO_MATCH.replace("'", "''");
    }

    @Benchmark
    public String stringReplaceShortStringOneMatch() {
        return SHORT_STRING_ONE_MATCH.replace("'", "''");
    }

    @Benchmark
    public String stringReplaceLongStringOneMatch() {
        return LONG_STRING_ONE_MATCH.replace("'", "''");
    }

    @Benchmark
    public String stringReplaceShortStringSeveralMatches() {
        return SHORT_STRING_SEVERAL_MATCHES.replace("'", "''");
    }

    @Benchmark
    public String stringReplaceLongStringSeveralMatches() {
        return LONG_STRING_SEVERAL_MATCHES.replace("'", "''");
    }

    // ----------------------------------------------------------------------- Jodd StringUtil#replace

    @Benchmark
    public String stringUtilReplaceShortStringNoMatch() {
        return StringUtil.replace(SHORT_STRING_NO_MATCH, "'", "''");
    }

    @Benchmark
    public String stringUtilReplaceLongStringNoMatch() {
        return StringUtil.replace(LONG_STRING_NO_MATCH, "'", "''");
    }

    @Benchmark
    public String stringUtilReplaceShortStringOneMatch() {
        return StringUtil.replace(SHORT_STRING_ONE_MATCH, "'", "''");
    }

    @Benchmark
    public String stringUtilReplaceLongStringOneMatch() {
        return StringUtil.replace(LONG_STRING_ONE_MATCH, "'", "''");
    }

    @Benchmark
    public String stringUtilReplaceShortStringSeveralMatches() {
        return StringUtil.replace(SHORT_STRING_SEVERAL_MATCHES, "'", "''");
    }

    @Benchmark
    public String stringUtilReplaceLongStringSeveralMatches() {
        return StringUtil.replace(LONG_STRING_SEVERAL_MATCHES, "'", "''");
    }
}