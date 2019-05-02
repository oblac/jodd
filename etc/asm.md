Jodd and ASM
============

| Jodd version  | ASM version   |
| ------------- |:-------------:|
| 5.0           | 7.1           |
| 4.4           | 6.1.1         |
| 4.0           | 6.0           |
| 3.5.2         | 5.0.1         |
| 3.4.2         | 4.1           |


ASM is bundled with Jodd. We had to do so to remain compatible with systems that still use old version of ASM.

Here are the additional changes we applied on ASM source
in order to minimize the size of the library.

Changes
-------

+ package is renamed to: `jodd.asm7`.
+ `SignatureWriter` class is removed as not used.
+ some JavaDoc links fixed or removed.
+ `TraceSignatureVisitor` is added and modified (from `asm-util`). 