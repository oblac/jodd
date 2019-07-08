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

package jodd.bean;

import jodd.bean.fixture.JavaBean;
import jodd.bean.fixture.NestedJavaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 Benchmark                                                                  (fieldName)  Mode  Cnt     Score    Error  Units
 BeanUtilBenchmark.apacheBeanUtils                                               fieldA  avgt   15   163.354 ±  6.607  ns/op
 BeanUtilBenchmark.apacheBeanUtils                                nestedJavaBean.fieldA  avgt   15   472.832 ± 16.434  ns/op
 BeanUtilBenchmark.apacheBeanUtils                 nestedJavaBean.nestedJavaBean.fieldA  avgt   15   776.449 ± 24.245  ns/op
 BeanUtilBenchmark.apacheBeanUtils  nestedJavaBean.nestedJavaBean.nestedJavaBean.fieldA  avgt   15  1069.650 ± 29.715  ns/op
 BeanUtilBenchmark.joddBean                                                      fieldA  avgt   15   176.249 ±  2.907  ns/op
 BeanUtilBenchmark.joddBean                                       nestedJavaBean.fieldA  avgt   15   303.406 ± 16.470  ns/op
 BeanUtilBenchmark.joddBean                        nestedJavaBean.nestedJavaBean.fieldA  avgt   15   361.891 ±  5.886  ns/op
 BeanUtilBenchmark.joddBean         nestedJavaBean.nestedJavaBean.nestedJavaBean.fieldA  avgt   15   429.029 ± 13.577  ns/op
 */
@Fork(3)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BeanUtilBenchmark {

	@Param({
		"fieldA",
		"nestedJavaBean.fieldA",
		"nestedJavaBean.nestedJavaBean.fieldA",
		"nestedJavaBean.nestedJavaBean.nestedJavaBean.fieldA"
	})
	String fieldName;

	JavaBean javaBean;

	@Setup
	public void setup() {
		final NestedJavaBean nestedJavaBean3 = new NestedJavaBean().setFieldA("nested-3");
		final NestedJavaBean nestedJavaBean2 = new NestedJavaBean().setFieldA("nested-2").setNestedJavaBean(nestedJavaBean3);
		final NestedJavaBean nestedJavaBean1 = new NestedJavaBean().setFieldA("nested-1").setNestedJavaBean(nestedJavaBean2);
		javaBean = new JavaBean().setFieldA("fieldA").setNestedJavaBean(nestedJavaBean1);
	}

	/**
	 * Reference: http://commons.apache.org/proper/commons-beanutils/
	 */
	@Benchmark
	public Object apacheBeanUtils() throws Exception {
		return PropertyUtils.getNestedProperty(javaBean, fieldName);
	}

	@Benchmark
	public Object joddBean() {
		return BeanUtil.declared.getProperty(javaBean, fieldName);
	}

}
