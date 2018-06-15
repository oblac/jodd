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

package jodd.chalk;

/**
 * Chalk256 allows you to color output going to ansi-256 console.
 * @see Chalk
 */
public class Chalk256 extends Chalk<Chalk256> {

	private static final String[] FG_CODES = new String[256];
	private static final String[] BG_CODES = new String[256];

	static {
		for (int i = 0; i < FG_CODES.length; i++) {
			FG_CODES[i] = "38;5;" + i;
		}
		for (int i = 0; i < BG_CODES.length; i++) {
			BG_CODES[i] = "48;5;" + i;
		}
	}

	public static Chalk256 chalk() {
		return new Chalk256();
	}

	// ---------------------------------------------------------------- fg codes

	public Chalk256 standard(final int index) {
		startSequence(FG_CODES[index(index, 0, 8)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 bright(final int index) {
		startSequence(FG_CODES[index(index, 8, 16)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 rgb(final int index) {
		startSequence(FG_CODES[index(index, 16, 232)]);
		endSequence(RESET);
		return _this();
	}

	/**
	 * Colors with red-green-blue value, in the range 0 to 6.
	 */
	public Chalk256 rgb(final int r, final int b, final int g) {
		startSequence(FG_CODES[index(36*r + 6*g + b,16, 232)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 grayscale(final int index) {
		startSequence(FG_CODES[index(index, 232, 256)]);
		endSequence(RESET);
		return _this();
	}
	// ---------------------------------------------------------------- bg codes

	public Chalk256 bgStandard(final int index) {
		startSequence(BG_CODES[index(index, 0, 8)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 bgBright(final int index) {
		startSequence(BG_CODES[index(index, 8, 16)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 bgRgb(final int index) {
		startSequence(BG_CODES[index(index, 16, 232)]);
		endSequence(RESET);
		return _this();
	}

	/**
	 * Colors with red-green-blue value, in the range 0 to 6.
	 */
	public Chalk256 bgRgb(final int r, final int b, final int g) {
		startSequence(BG_CODES[index(36*r + 6*g + b,16, 232)]);
		endSequence(RESET);
		return _this();
	}
	public Chalk256 bgGrayscale(final int index) {
		startSequence(BG_CODES[index(index, 232, 256)]);
		endSequence(RESET);
		return _this();
	}

	// ---------------------------------------------------------------- bgcolors

	private int index(int index, final int from, final int to) {
		index += from;
		if ((index < from) || (index >= to)) {
			throw new IllegalArgumentException("Color index not in range: [0, " + (to - from) + "]");
		}
		return index;
	}

}
