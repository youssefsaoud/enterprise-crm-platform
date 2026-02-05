# Token Storage

The Angular MVP keeps the JWT access token in memory only. This means a browser refresh clears authentication state, but it avoids placing credentials or bearer tokens in localStorage.

If persistent sessions are added later, use an explicit threat model and document XSS mitigations before changing token storage.
