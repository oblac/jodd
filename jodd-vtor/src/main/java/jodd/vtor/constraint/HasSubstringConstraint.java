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

package jodd.vtor.constraint;

import jodd.util.StringUtil;
import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;


public class HasSubstringConstraint implements ValidationConstraint<HasSubstring> {

	public HasSubstringConstraint() {
	}

	public HasSubstringConstraint(final String substring, final boolean ignoreCase) {
		this.substring = substring;
		this.ignoreCase = ignoreCase;
	}

	// ---------------------------------------------------------------- properties

	protected String substring;
	protected boolean ignoreCase;

	public String getSubstring() {
		return substring;
	}

	public void setSubstring(final String substring) {
		this.substring = substring;                                          
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(final boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	// ---------------------------------------------------------------- configure

	@Override
	public void configure(final HasSubstring annotation) {
		this.substring = annotation.value();
		this.ignoreCase = annotation.ignoreCase();
	}

	// ---------------------------------------------------------------- valid

	@Override
	public boolean isValid(final ValidationConstraintContext vcc, final Object value) {
		return validate(value, substring, ignoreCase);
	}

	public static boolean validate(final Object value, final String substring, final boolean ignoreCase) {
		if (value == null) {
			return true;
		}
		if (ignoreCase) {
			return StringUtil.indexOfIgnoreCase(value.toString(), substring) > -1;
		}
		return value.toString().contains(substring);
	}

}