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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base32Test {

	@Test
	public void testEncode32() {
		assertEquals("", Base32.encode("".getBytes()));
		assertEquals("MY", Base32.encode("f".getBytes()));
		assertEquals("MZXW6YTBOI", Base32.encode("foobar".getBytes()));
	}

	@Test
	public void testDecode32() {
		assertEquals("foobar", new String(Base32.decode("MZXW6YTBOI")));
		assertEquals("f", new String(Base32.decode("MY")));
	}

	static String text = "Base32 is a notation for encoding arbitrary byte data using a restricted set of symbols " +
			"which can be conveniently used by humans and processed by old computer systems which only recognize " +
			"restricted character sets. It comprises a symbol set made up of 32 different characters, as well " +
			"as an algorithm for encoding arbitrary strings using 8-bit characters into the Base32 alphabet. " +
			"This uses more than one 5-bit Base32 symbol for each 8-bit input character, and thus also specifies " +
			"requirements on the allowed lengths of Base32 strings (which must be multiples of 40 bits). " +
			"The Base64 system, in contrast, is closely related but uses a larger set of 64 symbols.";

	static String base32 = "IJQXGZJTGIQGS4ZAMEQG433UMF2GS33OEBTG64RAMVXGG33ENFXGOIDBOJRGS5DSMFZHSIDCPF2GKIDEMF2GC" +
			"IDVONUW4ZZAMEQHEZLTORZGSY3UMVSCA43FOQQG6ZRAON4W2YTPNRZSA53INFRWQIDDMFXCAYTFEBRW63TWMVXGSZLOORWH" +
			"SIDVONSWIIDCPEQGQ5LNMFXHGIDBNZSCA4DSN5RWK43TMVSCAYTZEBXWYZBAMNXW24DVORSXEIDTPFZXIZLNOMQHO2DJMNU" +
			"CA33ONR4SA4TFMNXWO3TJPJSSA4TFON2HE2LDORSWIIDDNBQXEYLDORSXEIDTMV2HGLRAJF2CAY3PNVYHE2LTMVZSAYJAON" +
			"4W2YTPNQQHGZLUEBWWCZDFEB2XAIDPMYQDGMRAMRUWMZTFOJSW45BAMNUGC4TBMN2GK4TTFQQGC4ZAO5SWY3BAMFZSAYLOE" +
			"BQWYZ3POJUXI2DNEBTG64RAMVXGG33ENFXGOIDBOJRGS5DSMFZHSIDTORZGS3THOMQHK43JNZTSAOBNMJUXIIDDNBQXEYLD" +
			"ORSXE4ZANFXHI3ZAORUGKICCMFZWKMZSEBQWY4DIMFRGK5BOEBKGQ2LTEB2XGZLTEBWW64TFEB2GQYLOEBXW4ZJAGUWWE2L" +
			"UEBBGC43FGMZCA43ZNVRG63BAMZXXEIDFMFRWQIBYFVRGS5BANFXHA5LUEBRWQYLSMFRXIZLSFQQGC3TEEB2GQ5LTEBQWY4" +
			"3PEBZXAZLDNFTGSZLTEBZGK4LVNFZGK3LFNZ2HGIDPNYQHI2DFEBQWY3DPO5SWIIDMMVXGO5DIOMQG6ZRAIJQXGZJTGIQHG" +
			"5DSNFXGO4ZAFB3WQ2LDNAQG25LTOQQGEZJANV2WY5DJOBWGK4ZAN5TCANBQEBRGS5DTFEXCAVDIMUQEEYLTMU3DIIDTPFZX" +
			"IZLNFQQGS3RAMNXW45DSMFZXILBANFZSAY3MN5ZWK3DZEBZGK3DBORSWIIDCOV2CA5LTMVZSAYJANRQXEZ3FOIQHGZLUEBX" +
			"WMIBWGQQHG6LNMJXWY4ZO";

	@Test
	public void testText() {
		assertEquals(base32, Base32.encode(text.getBytes()));
		assertEquals(text, new String(Base32.decode(base32)));
	}

}
