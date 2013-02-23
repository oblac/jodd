Jodd and ASM
============

Since v3.4.2, Jodd comes with ASM 4.1 bundled with it.
We had to do so to remain compatible with systems that still use
old version of ASM.

Here are the additional changes we applied on ASM 4.1 source
in order to minimize the size of the library.

Changes
-------

+ package is renamed to: `jodd.asm4`.
+ `SignatureWriter` class is removed as not used.