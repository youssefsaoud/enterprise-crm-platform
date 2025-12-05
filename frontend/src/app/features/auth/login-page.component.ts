import { Component } from '@angular/core';

@Component({
  selector: 'app-login-page',
  standalone: true,
  template: `
    <section class="page-header">
      <p>Access</p>
      <h1>Login</h1>
    </section>
    <form class="form-panel" aria-label="Login form">
      <label>
        Email
        <input type="email" autocomplete="email" placeholder="name@example.com" />
      </label>
      <label>
        Password
        <input type="password" autocomplete="current-password" placeholder="Password" />
      </label>
      <button type="button">Sign in</button>
    </form>
  `
})
export class LoginPageComponent {}
