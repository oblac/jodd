// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.ProxettaException;
import jodd.proxetta.impl.WrapperProxetta;
import org.objectweb.asm.ClassReader;

import java.lang.reflect.Field;

public class ProxettaWrapperCreator extends ClassProcessor {

	protected final WrapperProxetta wrapperProxetta;

	public ProxettaWrapperCreator(WrapperProxetta wrapperProxetta) {
		super(wrapperProxetta);
		this.wrapperProxetta = wrapperProxetta;
	}

	protected Class targetClassOrInterface;
	protected Class targetInterface;

	@Override
	public void setTarget(Class target) {
		super.setTarget(target);
		this.targetClassOrInterface = target;
	}

	public void setTargetInterface(Class targetInterface) {
		this.targetInterface = targetInterface;
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
						destClassWriter,
						wrapperProxetta.getAspects(),
						classNameSuffix(),
						requestedProxyClassName,
						targetClassInfoReader);

		cr.accept(pcb, 0);
		return pcb.wd;
	}


	/**
	 * Injects target into wrapper.
	 */
	public void injectTargetIntoWrapper(Object target, Object wrapper) {
		try {
			Field field = wrapper.getClass().getField("_wrapper");
			field.setAccessible(true);
			field.set(wrapper, target);
		} catch (Exception ex) {
			throw new ProxettaException(ex);
		}
	}


}
