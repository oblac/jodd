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

import java.util.Map;

import static jodd.util.StringPool.LEFT_SQ_BRACKET;
import static jodd.util.StringPool.RIGHT_SQ_BRACKET;

/**
 * Powerful tool for copying properties from one bean into another.
 * <code>BeanCopy</code> works with POJO beans, but also with <code>Map</code>.
 *
 * @see BeanVisitor
 */
public class BeanCopy extends BeanVisitor {

	protected Object destination;
	protected boolean forced;
	protected boolean declaredTarget;
	protected boolean isTargetMap;

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new BeanCopy process between the source and the destination.
	 * Both source and destination can be a POJO object or a <code>Map</code>.
	 */
	public BeanCopy(Object source, Object destination) {
		this.source = source;
		this.destination = destination;
	}

	private BeanCopy(Object source) {
		this.source = source;
	}

	/**
	 * Simple static factory for <code>BeanCopy</code>.
	 * @see #BeanCopy(Object, Object)
	 */
	public static BeanCopy beans(Object source, Object destination) {
		return new BeanCopy(source, destination);
	}

	/**
	 * Creates <copy>BeanCopy</copy> with given POJO bean as a source.
	 */
	public static BeanCopy fromBean(Object source) {
		return new BeanCopy(source);
	}

	/**
	 * Creates <copy>BeanCopy</copy> with given <code>Map</code> as a source.
	 */
	public static BeanCopy fromMap(Map source) {
		BeanCopy beanCopy = new BeanCopy(source);

		beanCopy.isSourceMap = true;

		return beanCopy;
	}

	// ---------------------------------------------------------------- destination

	/**
	 * Defines destination bean.
	 */
	public BeanCopy toBean(Object destination) {
		this.destination = destination;
		return this;
	}

	/**
	 * Defines destination map.
	 */
	public BeanCopy toMap(Map destination) {
		this.destination = destination;

		isTargetMap = true;

		return this;
	}


	// ---------------------------------------------------------------- properties

	/**
	 * Excludes all properties, i.e. enables blacklist mode.
	 */
	public BeanCopy excludeAll() {
		blacklist = false;
		return this;
	}

	/**
	 * Defines excluded property names.
	 */
	public BeanCopy exclude(String... excludes) {
		for (String ex : excludes) {
			rules.exclude(ex);
		}
		return this;
	}

	/**
	 * Exclude a property.
	 */
	public BeanCopy exclude(String exclude) {
		rules.exclude(exclude);
		return this;
	}

	/**
	 * Defines included property names.
	 */
	public BeanCopy include(String... includes) {
		for (String in : includes) {
			rules.include(in);
		}
		return this;
	}

	/**
	 * Include a property.
	 */
	public BeanCopy include(String include) {
		rules.include(include);
		return this;
	}

	/**
	 * Defines included property names as public properties
	 * of given template class. Sets to black list mode.
	 */
	public BeanCopy includeAs(Class template) {
		blacklist = false;

		String[] properties = getAllBeanPropertyNames(template, false);

		include(properties);

		return this;
	}

	/**
	 * Defines if <code>null</code> values should be ignored.
	 */
	public BeanCopy ignoreNulls(boolean ignoreNulls) {
		this.ignoreNullValues = ignoreNulls;

		return this;
	}

	/**
	 * Defines if all properties should be copied (when set to <code>true</code>)
	 * or only public (when set to <code>false</code>, default).
	 */
	public BeanCopy declared(boolean declared) {
		this.declared = declared;
		this.declaredTarget = declared;
		return this;
	}

	/**
	 * Fine-tuning of the declared behaviour.
	 */
	public BeanCopy declared(boolean declaredSource, boolean declaredTarget) {
		this.declared = declaredSource;
		this.declaredTarget = declaredTarget;
		return this;
	}

	/**
	 * Defines if fields without getters should be copied too.
	 */
	public BeanCopy includeFields(boolean includeFields) {
		this.includeFields = includeFields;
		return this;
	}

	public BeanCopy forced(boolean forced) {
		this.forced = forced;
		return this;
	}

	// ---------------------------------------------------------------- visitor

	/**
	 * Performs the copying.
	 */
	public void copy() {
		visit();
	}

	/**
	 * Copies single property to the destination.
	 * Exceptions are ignored, so copying continues if
	 * destination does not have some of the sources properties.
	 */
	@Override
	protected boolean visitProperty(String name, Object value) {
		if (isTargetMap) {
			name = LEFT_SQ_BRACKET + name + RIGHT_SQ_BRACKET;
		}

		BeanUtil.setProperty(destination, name, value, declared, forced, true);

		return true;
	}

}