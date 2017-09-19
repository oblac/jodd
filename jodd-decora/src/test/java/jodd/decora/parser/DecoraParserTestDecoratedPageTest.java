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

package jodd.decora.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Writer;

import static org.mockito.Mockito.*;

public class DecoraParserTestDecoratedPageTest {

	private DecoraParser decoraParser;
	private Writer writerMock;
	private DecoraTag decoraTagMock;

	@BeforeEach
	public void setUp() {
		decoraParser = new DecoraParser();
		writerMock = mock(Writer.class);
		decoraTagMock = mock(DecoraTag.class);
	}

	@Test
	public void testWriteDecoratedPageDecoraTagLengthNegative() throws Exception {
		// setup
		when(decoraTagMock.getStartIndex()).thenReturn(-1);
		DecoraTag[] decoraTags = { decoraTagMock };
		char[] decoratorContent = new char[] {};

		// when
		decoraParser.writeDecoratedPage(writerMock, decoratorContent, new char[] {}, decoraTags);

		// then
		verify(decoraTagMock, never()).getEndIndex();
		verify(writerMock).write(decoratorContent, 0, decoratorContent.length);
	}

	@Test
	public void testWriteDecoratedPageDecoraTagRegionUndefined() throws Exception {
		// setup
		DecoraTag decoraTagMock2 = mock(DecoraTag.class);
		when(decoraTagMock.isRegionUndefined()).thenReturn(true);
		when(decoraTagMock.getRegionStart()).thenReturn(0);
		when(decoraTagMock.getRegionLength()).thenReturn(10);
		DecoraTag[] decoraTags = { decoraTagMock2 };
		char[] pageContent = new char[] {};

		// when
		decoraParser.writeRegion(writerMock, pageContent, decoraTagMock, decoraTags);

		// then
		verify(decoraTagMock2, never()).isInsideOtherTagRegion(decoraTagMock);
		verify(writerMock).write(pageContent, 0, 10);
	}
}
