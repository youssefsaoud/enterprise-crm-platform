package com.example.crm.invitations;

import java.time.Instant;
import org.springframework.web.util.HtmlUtils;

final class InvitationEmail {
    private InvitationEmail() {}
    static String text(String name, String role, String url, Instant expiry) {
        return "Hello " + name + ",\n\nYou have been invited to Orqestra, Project & Vendor Platform, as " + role
            + ".\nActivate your account and choose your password:\n" + url + "\n\nThis invitation expires at " + expiry
            + " (UTC) and can only be used once.\nIf you did not expect this invitation, ignore this email.\n\nOrqestra\nEvery project, in harmony.";
    }
    static String html(String name, String role, String url, Instant expiry) {
        String safeUrl = HtmlUtils.htmlEscape(url);
        return """
            <!doctype html><html><body style="margin:0;background:#0E0F12;color:#F5F5F7;font-family:Arial,sans-serif">
            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0"><tr><td align="center" style="padding:32px 16px">
            <table role="presentation" width="560" style="max-width:100%%;background:#17191F;border:1px solid #2A2D35;border-radius:8px" cellpadding="32"><tr><td>
            <h1 style="margin:0 0 8px;font-size:28px;color:#F5F5F7">Orqestra</h1>
            <p style="color:#A1A1AA;margin:0 0 32px">Project &amp; Vendor Platform</p>
            <h2 style="font-size:22px;color:#F5F5F7">You're invited</h2>
            <p>Hello %s,</p><p style="line-height:1.6">You've been invited to join Orqestra as <strong>%s</strong>.
            Activate your account to choose a password and join your team's workspace.</p>
            <p style="margin:32px 0"><a href="%s" style="display:inline-block;background:#2383FF;color:#FFFFFF;padding:14px 24px;border-radius:6px;text-decoration:none;font-weight:bold">Activate your account</a></p>
            <p style="color:#A1A1AA;line-height:1.6">Expires: %s (UTC). This link can only be used once.</p>
            <p style="color:#A1A1AA;font-size:13px">Button not working? Open this address:</p>
            <p style="word-break:break-all;font-size:13px"><a style="color:#76B5FF" href="%s">%s</a></p>
            <hr style="border:0;border-top:1px solid #2A2D35;margin:28px 0">
            <p style="color:#A1A1AA;font-size:13px;line-height:1.6">If this invitation was unexpected, ignore this email. No account is activated until you choose a password.</p>
            <p style="color:#A1A1AA;font-size:13px">Every project, in harmony.</p>
            </td></tr></table></td></tr></table></body></html>
            """.formatted(HtmlUtils.htmlEscape(name), HtmlUtils.htmlEscape(role), safeUrl,
                HtmlUtils.htmlEscape(expiry.toString()), safeUrl, safeUrl);
    }
}
