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

package jodd.proxetta;

import jodd.asm.TraceSignatureVisitor;
import jodd.asm7.signature.SignatureReader;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collector of generics information from the signature.
 */
public class GenericsReader {

	final Map<String, String> genericsMap;

	public GenericsReader() {
		this.genericsMap = new LinkedHashMap<>();
	}

	/**
	 * Parses signature for generic information and returns a map where key is generic name
	 * and value is raw type. Returns an empty map if signature does not define any generics.
	 */
	public Map<String, String> parseSignatureForGenerics(final String signature, final boolean isInterface) {

		if (signature == null) {
			return Collections.emptyMap();
		}

		final int indexOfBracket = signature.indexOf("<");
		final String declaringClass;
		if (indexOfBracket > 0) {
			declaringClass = signature.substring(1, indexOfBracket) + ":";
		}
		else {
			declaringClass = null;
		}

		final SignatureReader sr = new SignatureReader(signature);
		final StringBuilder sb = new StringBuilder();

		TraceSignatureVisitor v = new TraceSignatureVisitor(sb, isInterface) {
			String genericName;
			int classTypeCounter = 0;

			@Override
			public void visitFormalTypeParameter(final String name) {
				genericName = name;
				super.visitFormalTypeParameter(name);
			}

			@Override
			public void visitClassType(final String name) {
				classTypeCounter++;

				if (genericName != null) {
					genericsMap.put(genericName, 'L' + name + ';');
					genericName = null;
				} else {
					if (declaringClass != null) {
						genericsMap.put(declaringClass + (classTypeCounter - 1), 'L' + name + ';');
					}
				}
				super.visitClassType(name);
			}

		};
		sr.accept(v);

		return genericsMap;
	}

}
