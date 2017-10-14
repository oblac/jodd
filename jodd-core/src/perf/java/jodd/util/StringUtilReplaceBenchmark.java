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
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark for {@link StringUtil#replace(String, String, String)} method. <br/>
 * Following methods will be compared:
 * <ol>
 *     <li>{@link String#replace(CharSequence, CharSequence)}</li>
 *     <li>{@link org.apache.commons.lang3.StringUtils#replace(String, String, String)}</li>
 *     <li>{@link StringUtil#replace(String, String, String)}</li>
 * </ol>
 *
 * Code was originally published on <a href="https://blog.jooq.org/2017/10/11/benchmarking-jdk-string-replace-vs-apache-commons-stringutils-replace/#comment-151887">jOOQ blog</a> , but slightly modified.
 *
 * <p>
 * Run:
 * <code>
 * gw :jodd-core:perf -PStringUtilReplaceBenchmark
 * </code>
 * </p>
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
    public void testStringReplaceShortStringNoMatch(Blackhole blackhole) {
        blackhole.consume(SHORT_STRING_NO_MATCH.replace("'", "''"));
    }

    @Benchmark
    public void testStringReplaceLongStringNoMatch(Blackhole blackhole) {
        blackhole.consume(LONG_STRING_NO_MATCH.replace("'", "''"));
    }

    @Benchmark
    public void testStringReplaceShortStringOneMatch(Blackhole blackhole) {
        blackhole.consume(SHORT_STRING_ONE_MATCH.replace("'", "''"));
    }

    @Benchmark
    public void testStringReplaceLongStringOneMatch(Blackhole blackhole) {
        blackhole.consume(LONG_STRING_ONE_MATCH.replace("'", "''"));
    }

    @Benchmark
    public void testStringReplaceShortStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(SHORT_STRING_SEVERAL_MATCHES.replace("'", "''"));
    }

    @Benchmark
    public void testStringReplaceLongStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(LONG_STRING_SEVERAL_MATCHES.replace("'", "''"));
    }

    // ----------------------------------------------------------------------- Apache Commons Lang StringUtils#replace
    
    @Benchmark
    public void testApacheStringUtilsReplaceShortStringNoMatch(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(SHORT_STRING_NO_MATCH, "'", "''"));
    }

    @Benchmark
    public void testApacheStringUtilsReplaceLongStringNoMatch(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(LONG_STRING_NO_MATCH, "'", "''"));
    }

    @Benchmark
    public void testApacheStringUtilsReplaceShortStringOneMatch(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(SHORT_STRING_ONE_MATCH, "'", "''"));
    }

    @Benchmark
    public void testApacheStringUtilsReplaceLongStringOneMatch(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(LONG_STRING_ONE_MATCH, "'", "''"));
    }

    @Benchmark
    public void testApacheStringUtilsReplaceShortStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(SHORT_STRING_SEVERAL_MATCHES, "'", "''"));
    }

    @Benchmark
    public void testApacheStringUtilsReplaceLongStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(org.apache.commons.lang3.StringUtils.replace(LONG_STRING_SEVERAL_MATCHES, "'", "''"));
    }

    // ----------------------------------------------------------------------- Jodd StringUtil#replace

    @Benchmark
    public void testStringUtilReplaceShortStringNoMatch(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(SHORT_STRING_NO_MATCH, "'", "''"));
    }

    @Benchmark
    public void testStringUtilReplaceLongStringNoMatch(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(LONG_STRING_NO_MATCH, "'", "''"));
    }

    @Benchmark
    public void testStringUtilReplaceShortStringOneMatch(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(SHORT_STRING_ONE_MATCH, "'", "''"));
    }

    @Benchmark
    public void testStringUtilReplaceLongStringOneMatch(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(LONG_STRING_ONE_MATCH, "'", "''"));
    }

    @Benchmark
    public void testStringUtilReplaceShortStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(SHORT_STRING_SEVERAL_MATCHES, "'", "''"));
    }

    @Benchmark
    public void testStringUtilReplaceLongStringSeveralMatches(Blackhole blackhole) {
        blackhole.consume(StringUtil.replace(LONG_STRING_SEVERAL_MATCHES, "'", "''"));
    }
}