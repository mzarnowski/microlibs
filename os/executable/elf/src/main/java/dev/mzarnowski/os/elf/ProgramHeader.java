package dev.mzarnowski.os.elf;

public final class ProgramHeader {
    public final Program.Type type;
    public final int flags;
    public final long offset;
    public final long vaddr;
    public final long paddr;
    public final long filesz;
    public final long memsize;
    public final long align;


    ProgramHeader(Program.Type type, int flags, long offset, long vaddr,
                  long paddr, long filesz, long memsize, long align) {
        this.type = type;
        this.flags = flags;
        this.offset = offset;
        this.vaddr = vaddr;
        this.paddr = paddr;
        this.filesz = filesz;
        this.memsize = memsize;
        this.align = align;
    }
}
