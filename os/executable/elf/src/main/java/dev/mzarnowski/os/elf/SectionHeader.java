package dev.mzarnowski.os.elf;

public final class SectionHeader {
    public final String name;
    public final Section.Type type;
    public final long flags;
    public final long addr;
    public final long offset;
    public final long size;
    public final String linked;
    public final int info;
    public final long alignment;
    public final long entrySize;


    SectionHeader(String name, Section.Type type, long flags, long addr, long offset, long size, String linked, int info, long alignment, long entrySize) {
        this.name = name;
        this.type = type;
        this.flags = flags;
        this.addr = addr;
        this.offset = offset;
        this.size = size;
        this.linked = linked;
        this.info = info;
        this.alignment = alignment;
        this.entrySize = entrySize;
    }
}
