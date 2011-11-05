// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class Base32Test extends TestCase {

	public void testEncode32() {
		assertEquals("", Base32.encode("".getBytes()));
		assertEquals("MY", Base32.encode("f".getBytes()));
		assertEquals("MZXW6YTBOI", Base32.encode("foobar".getBytes()));
	}

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

	public void testText() {
		assertEquals(base32, Base32.encode(text.getBytes()));
		assertEquals(text, new String(Base32.decode(base32)));
	}

}
