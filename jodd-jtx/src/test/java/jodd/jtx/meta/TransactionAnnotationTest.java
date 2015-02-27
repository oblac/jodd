// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.meta;

import org.junit.Test;

import java.lang.reflect.Method;

import static jodd.jtx.JtxIsolationLevel.*;
import static jodd.jtx.JtxPropagationBehavior.*;
import static org.junit.Assert.assertEquals;

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
		TransactionAnnotation<Transaction> txAnnotation = new TransactionAnnotation<Transaction>(Transaction.class);
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
		TransactionAnnotation<CustomTransaction> txAnnotation = new TransactionAnnotation<CustomTransaction>(CustomTransaction.class);
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