package com.example.crm.invitations;

public class InvitationException extends RuntimeException {
    final int status;
    public InvitationException(int status, String message) { super(message); this.status = status; }
    static InvitationException invalid() { return new InvitationException(400, "This invitation is invalid or no longer available. Contact your Admin."); }
}
