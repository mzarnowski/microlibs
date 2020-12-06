package dev.mzarnowski.os.elf;

public final class ElfHeader {
    public final int arch;
    public final ABI abi;
    public final long abiVersion;
    public final Executable.Type type;
    public final ISA isa;
    public final long entryPoint;
    public final long flags;

    ElfHeader(int arch, ABI abi, long abiVersion, Executable.Type type, ISA isa, long entryPoint, long flags) {
        this.arch = arch;
        this.abi = abi;
        this.abiVersion = abiVersion;
        this.type = type;
        this.isa = isa;
        this.entryPoint = entryPoint;
        this.flags = flags;
    }
}
