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

import jodd.io.FileUtil;
import jodd.mutable.MutableInteger;
import jodd.system.SystemUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * test class for {@link ObjectUtil}
 */
class ObjectUtilTest {

    private static final File BASE_DIR = new File(SystemUtil.info().getTempDir(), "jodd/ObjectUtilTest");

    @BeforeAll
    public static void beforeAll() throws Exception {
        if (BASE_DIR.exists()) {
            // clean up all subdirs & files
            Files.walk(BASE_DIR.toPath(),FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        }
        // created directory is needed for tests
        BASE_DIR.mkdirs();
    }

	@Test
	void testCloneViaSerialization() throws Exception {

		final MutableInteger mu = new MutableInteger(183);

		final MutableInteger mu2 = ObjectUtil.cloneViaSerialization(mu);

		// asserts
		assertFalse(mu == mu2);
		assertTrue(mu.equals(mu2));
		assertEquals(mu.intValue(), mu2.intValue());
	}

	@Test
	void testCloneWithNull() throws Exception {

	    final Object actual = ObjectUtil.clone(null);

	    assertNull(actual);
    }

    @Test
    void testCloneWithCloneableInstance() throws Exception {

	    final MutableInteger instance_to_clone = new MutableInteger(MathUtil.randomInt(1, 1337));

        final MutableInteger actual = ObjectUtil.clone(instance_to_clone);

        // asserts
        assertNotNull(actual);
        assertFalse(instance_to_clone == actual);
        assertEquals(instance_to_clone , actual);
    }

    @Test
    void testCloneWithNoneCloneableInstance() throws Exception {

        assertThrows(CloneNotSupportedException.class, () -> {
            ObjectUtil.clone(new Object());
        });
    }

    @Test
    void testWriteObject() throws Exception {

	    final String instanceToWrite = "Jodd - The Unbearable Lightness of Java :-)";
	    final File targetFile = new File(BASE_DIR, "testWriteObject.ser");

	    ObjectUtil.writeObject(targetFile, instanceToWrite);
    }

    @Test
    void testReadObject() throws Exception {

        final String expected = "Jodd - The Unbearable Lightness of Java :-)";
        final File targetFile = new File(BASE_DIR, "testReadObject.ser");

        // ensure that file exists with other data
        FileUtil.writeString(targetFile, "only test data");
        // overwrite given file
        ObjectUtil.writeObject(targetFile, expected);

        final Object actual = ObjectUtil.readObject(targetFile);

        // asserts
        assertNotNull(actual);
        assertTrue(actual instanceof String);
        assertFalse(expected == actual);
        assertEquals(expected, actual);
    }

    @Test
    void testObjectToByteArray() throws Exception {

        final String instanceToWrite = "Jodd - The Unbearable Lightness of Java :-)";

        final byte[] actual = ObjectUtil.objectToByteArray(instanceToWrite);

        // asserts
        assertNotNull(actual);
    }

    @Test
    void testByteArrayToObject() throws Exception {

        final String expected = "Jodd - The Unbearable Lightness of Java :-)";
        final byte[] input = new byte[] {-84,-19,0,5,116,0,43,74,111,100,100,32,45,32,84,104,101,32,85,110,98,101,97,
                114,97,98,108,101,32,76,105,103,104,116,110,101,115,115,32,111,102,32,74,97,118,97,32,58,45,41};

        final Object actual = ObjectUtil.byteArrayToObject(input);

        // asserts
        assertNotNull(actual);
        assertTrue(actual instanceof String);
        assertFalse(expected == actual);
        assertEquals(expected, actual);
    }
}
