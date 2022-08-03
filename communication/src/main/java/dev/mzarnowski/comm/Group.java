package dev.mzarnowski.comm;

public final class Group {
    public VerificationResult verify(Invitation invitation) {
        return VerificationResult.UNKNOWN_ISSUER;
    }

    public enum VerificationResult {
        AUTHENTIC, UNKNOWN_ISSUER;
    }
}
