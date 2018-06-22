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

package jodd.cli;

import java.util.function.Consumer;

public class Option {

	String label;
	String shortName;
	String longName;
	String description;
	boolean hasArg;
	String argLabel;
	Consumer<String> consumer;

	public Option shortName(final String shortName) {
		this.shortName = shortName;
		return this;
	}

	public Option longName(final String longName) {
		this.longName = longName;
		return this;
	}

	public Option names(final String shortName, final String longName) {
		this.shortName = shortName;
		this.longName = longName;
		return this;
	}

	public Option description(final String description) {
		this.description = description;
		return this;
	}

	public Option hasArg() {
		this.hasArg = true;
		return this;
	}

	public Option hasArg(final String argLabel) {
		this.hasArg = true;
		this.argLabel = argLabel;
		return this;
	}

	public Option label(final String label) {
		this.label = label;
		return this;
	}

	public Option with(final Consumer<String> consumer) {
		this.consumer = consumer;
		return this;
	}

	@Override
	public String toString() {
		String out = "";
		if (shortName != null) {
			out += "-" + shortName;
		}
		if (longName != null) {
			if (!out.isEmpty()) {
				out += " | ";
			}
			out += "--" + longName;
		}
		return out;
	}
}
