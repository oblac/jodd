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
public class BeanCopy extends BeanVisitorImplBase<BeanCopy> {

	protected Object destination;
	protected boolean forced;
	protected boolean declaredTarget;
	protected boolean isTargetMap;

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new BeanCopy process between the source and the destination.
	 * Both source and destination can be a POJO object or a <code>Map</code>.
	 */
	public BeanCopy(final Object source, final Object destination) {
		this.source = source;
		this.destination = destination;
	}

	private BeanCopy(final Object source) {
		this.source = source;
	}

	/**
	 * Simple static factory for <code>BeanCopy</code>.
	 * @see #BeanCopy(Object, Object)
	 */
	public static BeanCopy beans(final Object source, final Object destination) {
		return new BeanCopy(source, destination);
	}

	/**
	 * Creates <code>BeanCopy</code> with given POJO bean as a source.
	 */
	public static BeanCopy fromBean(final Object source) {
		return new BeanCopy(source);
	}

	/**
	 * Creates <code>BeanCopy</code> with given <code>Map</code> as a source.
	 */
	public static BeanCopy fromMap(final Map source) {
		BeanCopy beanCopy = new BeanCopy(source);

		beanCopy.isSourceMap = true;

		return beanCopy;
	}

	/**
	 * Defines source, detects a map.
	 */
	public static BeanCopy from(final Object source) {
		BeanCopy beanCopy = new BeanCopy(source);

		beanCopy.isSourceMap = source instanceof Map;

		return beanCopy;
	}

	// ---------------------------------------------------------------- destination

	/**
	 * Defines destination bean.
	 */
	public BeanCopy toBean(final Object destination) {
		this.destination = destination;
		return this;
	}

	/**
	 * Defines destination map.
	 */
	public BeanCopy toMap(final Map destination) {
		this.destination = destination;

		isTargetMap = true;

		return this;
	}

	/**
	 * Defines destination, detects a map.
	 */
	public BeanCopy to(final Object destination) {
		this.destination = destination;

		this.isTargetMap = destination instanceof Map;

		return this;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Defines if all properties should be copied (when set to <code>true</code>)
	 * or only public (when set to <code>false</code>, default).
	 */
	@Override
	public BeanCopy declared(final boolean declared) {
		this.declared = declared;
		this.declaredTarget = declared;
		return this;
	}

	/**
	 * Fine-tuning of the declared behaviour.
	 */
	public BeanCopy declared(final boolean declaredSource, final boolean declaredTarget) {
		this.declared = declaredSource;
		this.declaredTarget = declaredTarget;
		return this;
	}

	public BeanCopy forced(final boolean forced) {
		this.forced = forced;
		return this;
	}

	// ---------------------------------------------------------------- visitor

	protected BeanUtil beanUtil;

	/**
	 * Performs the copying.
	 */
	public void copy() {
		beanUtil = new BeanUtilBean()
						.declared(declared)
						.forced(forced)
						.silent(true);
		visit();
	}

	/**
	 * Copies single property to the destination.
	 * Exceptions are ignored, so copying continues if
	 * destination does not have some of the sources properties.
	 */
	@Override
	protected boolean visitProperty(String name, final Object value) {
		if (isTargetMap) {
			name = LEFT_SQ_BRACKET + name + RIGHT_SQ_BRACKET;
		}

		beanUtil.setProperty(destination, name, value);

		return true;
	}

}