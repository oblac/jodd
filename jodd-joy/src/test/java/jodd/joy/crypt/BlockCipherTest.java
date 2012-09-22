// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.crypt;

import junit.framework.TestCase;

public class BlockCipherTest extends TestCase {

	static class FooBlockCipher extends BlockCipher {

		protected FooBlockCipher() {
			super(5 * 8);
		}

		@Override
		protected byte[] encryptBlock(byte[] content, int offset) {
			byte[] encrypted = new byte[blockSizeInBytes];
			System.arraycopy(content, offset, encrypted, 0, blockSizeInBytes);
			return encrypted;
		}

		@Override
		protected byte[] decryptBlock(byte[] encryptedContent, int offset) {
			byte[] decrypted = new byte[blockSizeInBytes];
			System.arraycopy(encryptedContent, offset, decrypted, 0, blockSizeInBytes);
			return decrypted;
		}
	}

	public void testBlock8() {
		FooBlockCipher cypher = new FooBlockCipher();

		byte[] encrypted = cypher.encrypt("Jodd".getBytes());
		assertEquals("Jodd", new String(encrypted).substring(0, encrypted.length - 1));
		byte[] decrypted = cypher.decrypt(encrypted);
		assertEquals("Jodd", new String(decrypted));

		encrypted = cypher.encrypt("Jo".getBytes());
		assertEquals("Jo", new String(encrypted).substring(0, encrypted.length - 3));
		decrypted = cypher.decrypt(encrypted);
		assertEquals("Jo", new String(decrypted));

		encrypted = cypher.encrypt("Jodder".getBytes());
		assertEquals("Jodder", new String(encrypted).substring(0, encrypted.length - 4));
		decrypted = cypher.decrypt(encrypted);
		assertEquals("Jodder", new String(decrypted));
	}

}
