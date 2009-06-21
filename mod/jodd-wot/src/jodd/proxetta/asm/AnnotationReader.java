// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import jodd.proxetta.AnnotationInfo;


/**
 * Reads annotation inner data.
 */
@SuppressWarnings({"AnonymousClassVariableHidesContainingMethodVariable"})
public class AnnotationReader extends EmptyAnnotationVisitor implements AnnotationInfo {

	protected final String desc;
	protected final String className;
	protected final boolean visible;
	protected final Map<String, Object> elements;

	public AnnotationReader(String desc, boolean visible) {
		this.desc = desc;
		this.visible = visible;
		this.elements = new HashMap<String, Object>();
		this.className = ProxettaAsmUtil.typeref2Name(desc);
	}

	// ---------------------------------------------------------------- info

	public String getAnnotationClassname() {
		return className;
	}

	public String getAnnotationSignature() {
		return desc;
	}

	public boolean isVisible() {
		return visible;
	}

	public Object getElement(String name) {
		return elements.get(name);
	}

	public Set<String> getElementNames() {
		return elements.keySet();
	}

	// ---------------------------------------------------------------- visitor


	@Override
	public void visit(String name, Object value) {
		elements.put(name, value);
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		elements.put(name, new String[]{desc, value});		
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		AnnotationReader nestedAnnotation = new AnnotationReader(desc, true);
		elements.put(name, nestedAnnotation);
		return nestedAnnotation;
	}

	@Override
	public AnnotationVisitor visitArray(final String name) {
		final List<Object> array = new ArrayList<Object>();
		return new EmptyAnnotationVisitor() {

			@Override
			public void visit(String name, Object value) {
				array.add(value);
			}

			@Override
			public void visitEnd() {
				Object[] data = array.toArray(new Object[array.size()]);
				elements.put(name, data);
			}
		};
	}

}
