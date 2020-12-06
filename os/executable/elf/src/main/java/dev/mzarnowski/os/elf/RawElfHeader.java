package dev.mzarnowski.os.elf;

final class RawElfHeader {
    final int arch;
    final ABI abi;
    final long abiVersion;

    RawElfHeader(int arch, ABI abi, long abiVersion) {
        this.arch = arch;
        this.abi = abi;
        this.abiVersion = abiVersion;
    }
}