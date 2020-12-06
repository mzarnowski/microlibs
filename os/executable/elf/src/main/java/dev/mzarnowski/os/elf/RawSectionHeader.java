package dev.mzarnowski.os.elf;

final class RawSectionHeader {
    int nameIdx;
    Section.Type type;
    long flags;
    long addr;
    long offset;
    long size;
    int link;
    int info;
    long alignment;
    long entrySize;
}