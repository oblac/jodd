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

package jodd.petite;

/**
 * Petite configuration.
 */
public class PetiteConfig {

	public PetiteConfig() {
		defaultWiringMode = WiringMode.STRICT;
		detectDuplicatedBeanNames = false;
		resolveReferenceParameters = true;
		useFullTypeNames = false;
		lookupReferences = PetiteReferenceType.DEFAULT;
		useParamo = true;
		wireScopedProxy = false;
		detectMixedScopes = false;
		useAltBeanNames = true;
		implicitParamInjection = true;
	}

	// ----------------------------------------------------------------

	protected boolean implicitParamInjection;

	/**
	 * Returns {@code true} if implicit parameter injection is enabled.
	 */
	public boolean isImplicitParamInjection() {
		return implicitParamInjection;
	}

	/**
	 * Enables implicit parameter injection.
	 */
	public void setImplicitParamInjection(final boolean implicitParamInjection) {
		this.implicitParamInjection = implicitParamInjection;
	}
	// ----------------------------------------------------------------

	protected boolean useAltBeanNames;

	/**
	 * Returns if alternative bean names are in use.
	 */
	public boolean isUseAltBeanNames() {
		return useAltBeanNames;
	}

	/**
	 * Enables alternative bean names.
	 */
	public PetiteConfig setUseAltBeanNames(final boolean useAltBeanNames) {
		this.useAltBeanNames = useAltBeanNames;
		return this;
	}

	// ----------------------------------------------------------------

	protected WiringMode defaultWiringMode;
	/**
	 * Returns default wiring mode.
	 */
	public WiringMode getDefaultWiringMode() {
		return defaultWiringMode;
	}
	/**
	 * Specifies default wiring mode.
	 */
	public PetiteConfig setDefaultWiringMode(final WiringMode defaultWiringMode) {
		if ((defaultWiringMode == null) || (defaultWiringMode == WiringMode.DEFAULT)) {
			throw new PetiteException("Invalid default wiring mode: " + defaultWiringMode);
		}
		this.defaultWiringMode = defaultWiringMode;
		return this;
	}
	/**
	 * Resolves wiring mode by checking if default and <code>null</code> values.
	 */
	protected WiringMode resolveWiringMode(WiringMode wiringMode) {
		if ((wiringMode == null) || (wiringMode == WiringMode.DEFAULT)) {
			wiringMode = defaultWiringMode;
		}
		return wiringMode;
	}


	// ----------------------------------------------------------------

	protected boolean detectDuplicatedBeanNames;
	/**
	 * Returns <code>true</code> if container detects duplicated bean names.
	 */
	public boolean getDetectDuplicatedBeanNames() {
		return detectDuplicatedBeanNames;
	}
	/**
	 * Specifies if an exception should be thrown if two beans with same exception are registered with this container.
	 */
	public PetiteConfig setDetectDuplicatedBeanNames(final boolean detectDuplicatedBeanNames) {
		this.detectDuplicatedBeanNames = detectDuplicatedBeanNames;
		return this;
	}

	// ----------------------------------------------------------------

	protected boolean resolveReferenceParameters;
	/**
	 * Returns <code>true</code> if parameter references should be resolved.
	 */
	public boolean getResolveReferenceParameters() {
		return resolveReferenceParameters;
	}
	/**
	 * Defines if reference parameters should be resolved.
	 */
	public PetiteConfig setResolveReferenceParameters(final boolean resolveReferenceParameters) {
		this.resolveReferenceParameters = resolveReferenceParameters;
		return this;
	}

	// ----------------------------------------------------------------

	protected boolean useFullTypeNames;

	public boolean getUseFullTypeNames() {
		return useFullTypeNames;
	}

	/**
	 * Specifies if type names should be full or short.
	 */
	public PetiteConfig setUseFullTypeNames(final boolean useFullTypeNames) {
		this.useFullTypeNames = useFullTypeNames;
		return this;
	}

	// ---------------------------------------------------------------- references

	protected PetiteReferenceType[] lookupReferences;

	public PetiteReferenceType[] getLookupReferences() {
		return lookupReferences;
	}

	/**
	 * Specifies references for bean name lookup, when name
	 * is not specified, in given order.
	 */
	public PetiteConfig setLookupReferences(final PetiteReferenceType... lookupReferences) {
		this.lookupReferences = lookupReferences;
		return this;
	}

	// ----------------------------------------------------------------

	protected boolean useParamo;

	public boolean getUseParamo() {
		return useParamo;
	}

	/**
	 * Specifies if <b>Paramo</b> tool should be used to resolve
	 * method and ctor argument names.
	 */
	public void setUseParamo(final boolean useParamo) {
		this.useParamo = useParamo;
	}

	// ----------------------------------------------------------------

	protected boolean wireScopedProxy;
	protected boolean detectMixedScopes;

	public boolean isWireScopedProxy() {
		return wireScopedProxy;
	}

	/**
	 * Defines if scoped proxies should be wired.
	 */
	public PetiteConfig setWireScopedProxy(final boolean wireScopedProxy) {
		this.wireScopedProxy = wireScopedProxy;
		return this;
	}

	public boolean isDetectMixedScopes() {
		return detectMixedScopes;
	}

	/**
	 * Defines if mixed scopes should be detected as errors.
	 * If {@link #wireScopedProxy} is not set, then enabling this flag
	 * will throw an exception on mixed scopes. If {@link #wireScopedProxy} is set
	 * enabling this flag will just issue a warn message in the log.
	 */
	public PetiteConfig setDetectMixedScopes(final boolean detectMixedScopes) {
		this.detectMixedScopes = detectMixedScopes;
		return this;
	}
}
