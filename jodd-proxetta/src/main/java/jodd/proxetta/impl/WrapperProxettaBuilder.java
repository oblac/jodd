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
	 * Defines class or interface to wrap.
	 * For setting the interface of the resulting class,
	 * use {@link #setTargetInterface(Class)}.
	 */
	@Override
	public void setTarget(Class target) {
		super.setTarget(target);
		this.targetClassOrInterface = target;
	}

	/**
	 * Defines the interface of the resulting class.
	 */
	public void setTargetInterface(Class targetInterface) {
		if (targetInterface.isInterface() == false) {
			throw new ProxettaException("Not an interface: " + targetInterface.getName());
		}
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