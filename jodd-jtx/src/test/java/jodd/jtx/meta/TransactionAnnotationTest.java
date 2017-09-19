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

package jodd.jtx.meta;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static jodd.jtx.JtxIsolationLevel.*;
import static jodd.jtx.JtxPropagationBehavior.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionAnnotationTest {

	@Transaction
	public void hello() {
	}

	@Transaction(readOnly = false, isolation = ISOLATION_SERIALIZABLE, propagation = PROPAGATION_REQUIRES_NEW, timeout = 1000)
	public void hello2() {
	}

	@CustomTransaction
	public void hello3() {
	}

	@CustomTransaction(propagation = PROPAGATION_MANDATORY)
	public void hello4() {
	}


	@Test
	public void testTransactionAnnotationOnly() throws NoSuchMethodException {
		TransactionAnnotation<Transaction> txAnnotation = new TransactionAnnotation<>(Transaction.class);
		assertEquals(Transaction.class, txAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello");
		TransactionAnnotationData<Transaction> annotationData = txAnnotation.readAnnotationData(method);

		assertEquals(ISOLATION_DEFAULT, annotationData.isolation);
		assertEquals(PROPAGATION_SUPPORTS, annotationData.propagation);
		assertEquals(true, annotationData.readOnly);
		assertEquals(-1, annotationData.timeout);

		method = this.getClass().getMethod("hello2");
		annotationData = txAnnotation.readAnnotationData(method);

		assertEquals(ISOLATION_SERIALIZABLE, annotationData.isolation);
		assertEquals(PROPAGATION_REQUIRES_NEW, annotationData.propagation);
		assertEquals(false, annotationData.readOnly);
		assertEquals(1000, annotationData.timeout);
	}

	@Test
	public void testCustomTransactionAnnotation() throws NoSuchMethodException {
		TransactionAnnotation<CustomTransaction> txAnnotation = new TransactionAnnotation<>(CustomTransaction.class);
		assertEquals(CustomTransaction.class, txAnnotation.getAnnotationClass());

		Method method = this.getClass().getMethod("hello3");
		TransactionAnnotationData<CustomTransaction> annotationData = txAnnotation.readAnnotationData(method);

		assertEquals(ISOLATION_DEFAULT, annotationData.isolation);
		assertEquals(PROPAGATION_REQUIRED, annotationData.propagation);
		assertEquals(false, annotationData.readOnly);
		assertEquals(-1, annotationData.timeout);

		method = this.getClass().getMethod("hello4");
		annotationData = txAnnotation.readAnnotationData(method);

		assertEquals(ISOLATION_DEFAULT, annotationData.isolation);
		assertEquals(PROPAGATION_MANDATORY, annotationData.propagation);
		assertEquals(false, annotationData.readOnly);
		assertEquals(-1, annotationData.timeout);
	}


}
