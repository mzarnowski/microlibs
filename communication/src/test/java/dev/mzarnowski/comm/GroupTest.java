package dev.mzarnowski.comm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {
    @Test
    public void rejects_invitation_from_unknown_issuer(){
        // given an invitation
        var invitation = new Invitation("issuer");
        // and a group without its issuer
        var group = new Group();

        // when the invitation is verified
        var result = group.verify(invitation);

        // then it is rejected
        assertThat(result).isEqualTo(Group.VerificationResult.UNKNOWN_ISSUER);
    }
}