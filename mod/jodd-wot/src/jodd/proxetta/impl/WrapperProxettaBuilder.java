// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.impl;

import jodd.proxetta.ProxettaBuilder;
import jodd.proxetta.ProxettaException;
import jodd.proxetta.asm.ProxettaWrapperClassBuilder;
import jodd.proxetta.asm.TargetClassInfoReader;
import jodd.proxetta.asm.WorkData;
import org.objectweb.asm.ClassReader;

import java.lang.reflect.Field;

/**
 * Creates wrapper using ASM library.
 */
public class WrapperProxettaBuilder extends ProxettaBuilder {

	protected final WrapperProxetta wrapperProxetta;

	public WrapperProxettaBuilder(WrapperProxetta wrapperProxetta) {
		super(wrapperProxetta);
		this.wrapperProxetta = wrapperProxetta;
	}

	protected Class targetClassOrInterface;
	protected Class targetInterface;
	protected String targetFieldName = "_target";

	/**
	 * Defines class or interface that will be wrapped.
	 * Resulting wrapper will be of the <b>same type</b> as target.
	 * For wrapping just interface, use also {@link #setTargetInterface(Class)}.
	 */
	@Override
	public void setTarget(Class target) {
		super.setTarget(target);
		this.targetClassOrInterface = target;
	}

	/**
	 * Defines the interface that will be wrapped.
	 * Only interface methods are wrapped, even if
	 * target has more.
	 */
	public void setTargetInterface(Class targetInterface) {
		this.targetInterface = targetInterface;
	}

	/**
	 * Defines target field name.
	 */
	public void setTargetFieldName(String targetFieldName) {
		this.targetFieldName = targetFieldName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected WorkData process(ClassReader cr, TargetClassInfoReader targetClassInfoReader) {
		ProxettaWrapperClassBuilder pcb =
				new ProxettaWrapperClassBuilder(
						targetClassOrInterface,
						targetInterface,
						targetFieldName,
						destClassWriter,
						wrapperProxetta.getAspects(),
						resolveClassNameSuffix(),
						requestedProxyClassName,
						targetClassInfoReader);

		cr.accept(pcb, 0);

		return pcb.getWorkData();
	}


	/**
	 * Injects target into wrapper.
	 */
	public void injectTargetIntoWrapper(Object target, Object wrapper) {
		try {
			Field field = wrapper.getClass().getField(targetFieldName);
			field.setAccessible(true);
			field.set(wrapper, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}

}