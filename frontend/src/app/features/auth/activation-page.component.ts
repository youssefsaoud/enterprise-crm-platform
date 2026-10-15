import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TeamService, Welcome, employeeRoleLabel } from '../team/team.service';
import { apiErrorMessage } from '../../core/api/api-error';

@Component({
  standalone: true, selector: 'app-activation-page', imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './activation-page.component.html', styleUrls: ['./activation-page.component.css']
})
export class ActivationPageComponent implements OnInit {
  readonly roleLabel = employeeRoleLabel;
  readonly form = this.fb.nonNullable.group({
    password: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(64), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[\s\S]*$/)]],
    confirmPassword: ['', Validators.required]
  }, { validators: control => control.get('password')?.value === control.get('confirmPassword')?.value ? null : { passwordMismatch: true } });
  welcome: Welcome | null = null;
  state: 'loading' | 'valid' | 'expired' | 'invalid' | 'success' = 'loading';
  saving = false;
  showPassword = false;
  error = '';
  private token = '';
  constructor(private readonly fb: FormBuilder, private readonly route: ActivatedRoute,
              private readonly service: TeamService, private readonly location: Location) {}
  ngOnInit(): void {
    // Fragment links never send the token to the web server. Keep it until success so refresh works.
    this.token = new URLSearchParams(this.route.snapshot.fragment ?? '').get('token')
      ?? this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!/^[A-Za-z0-9_-]{43}$/.test(this.token)) { this.state = 'invalid'; return; }
    this.service.validate(this.token).subscribe({
      next: welcome => { this.welcome = welcome; this.state = 'valid'; },
      error: error => { this.state = error.status === 410 ? 'expired' : 'invalid'; }
    });
  }
  activate(): void {
    if (this.form.invalid || this.saving || this.state !== 'valid') { this.form.markAllAsTouched(); return; }
    this.saving = true; this.error = '';
    this.service.accept(this.token, this.form.controls.password.value).subscribe({
      next: () => {
        this.saving = false; this.state = 'success'; this.token = ''; this.form.reset();
        this.location.replaceState('/activate-account');
      },
      error: (error: HttpErrorResponse) => {
        this.saving = false;
        if (error.status === 410) this.state = 'expired';
        else if (error.status === 400 && !error.error?.details?.length && !error.error?.message?.includes('UTF-8')) this.state = 'invalid';
        else this.error = apiErrorMessage(error, 'Activation could not be completed. Please try again.');
      }
    });
  }
}
