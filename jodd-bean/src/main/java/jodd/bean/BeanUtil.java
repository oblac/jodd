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

/**
 * Supreme utility for reading and writing bean properties. However, this one is the fastest available.
 * Although it provides various methods, the whole thing can be easily extended to match most needs.
 * <p>
 * BeanUtil supports:
 * <ul>
 * <li>Nested properties: separated by a dot ('.')</li>
 * <li>Indexed properties: arrays or Lists</li>
 * <li>Simple properties: accessor or Map</li>
 * </ul>
 *
 * <p>
 * Variants includes combinations of forced, declared and silent writing.
 * <ul>
 * <li><i>Forced</i> setting property tries to create destination property so it can be set correctly.</li>
 * <li><i>Silent</i> doesn't throw an exception if destination doesn't exist or if conversion fails.</li>
 * <li><i>Declared</i> includes only declared (public) properties.</li>
 * </ul>
 * <p>
 * This utility considers both bean property methods (set and get accessors), and bean fields.
 * This is done because of several reasons: often there is no need for both set/get accessors, since
 * bean logic requires just one functionality (e.g. just reading). In such case, other bean manipulation
 * libraries still requires to have both accessors in order to set or get value.
 * Another reason is that most common usage is to work with public accessors, and in that case
 * private fields are ignored.
 */
public interface BeanUtil {

	/**
	 * Default instance of {@link BeanUtilBean}.
	 */
	BeanUtil pojo = new BeanUtilBean();

	BeanUtil declared = new BeanUtilBean().declared(true);

	BeanUtil silent = new BeanUtilBean().silent(true);

	BeanUtil forced = new BeanUtilBean().forced(true);

	BeanUtil declaredSilent = new BeanUtilBean().declared(true).silent(true);

	BeanUtil declaredForced = new BeanUtilBean().declared(true).forced(true);

	BeanUtil declaredForcedSilent = new BeanUtilBean().declared(true).forced(true).silent(true);

	BeanUtil forcedSilent = new BeanUtilBean().forced(true).silent(true);

	// ---------------------------------------------------------------- SET

	/**
	 * Sets Java Bean property.
	 * @param bean Java POJO bean or a Map
	 * @param name property name
	 * @param value property value
	 */
	void setProperty(Object bean, String name, Object value);

	/**
	 * Sets indexed property.
	 */
	void setIndexProperty(Object bean, String property, int index, Object value);

	/**
	 * Sets simple property.
	 */
	void setSimpleProperty(Object bean, String property, Object value);


	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 * <p>
	 * In silent mode, returning of <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 * <p>
	 * Using forced mode does not have any influence on the result.
	 */
	<T> T getProperty(Object bean, String name);

	/**
	 * Returns value of indexed property.
	 */
	<T> T getIndexProperty(Object bean, String property, int index);

	/**
	 * Reads simple property.
	 */
	<T> T getSimpleProperty(Object bean, String property);


	// ---------------------------------------------------------------- HAS

	/**
	 * Returns <code>true</code> if bean has a property.
	 */
	boolean hasProperty(Object bean, String name);

	/**
	 * Returns <code>true</code> if bean has only a root property.
	 * If yes, this means that property may be injected into the bean.
	 * If not, bean does not contain the property.
	 */
	boolean hasRootProperty(Object bean, String name);

	/**
	 * Returns <code>true</code> if simple property exist.
	 */
	boolean hasSimpleProperty(Object bean, String property);


	// ---------------------------------------------------------------- type

	/**
	 * Returns property type.
	 */
	Class<?> getPropertyType(Object bean, String name);


	// ---------------------------------------------------------------- misc

	/**
	 * Returns the very first name chunk of the property.
	 */
	public String extractThisReference(String propertyName);

}