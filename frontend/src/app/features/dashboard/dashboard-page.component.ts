import { Component } from '@angular/core';

@Component({ selector: 'app-dashboard-page', standalone: true, templateUrl: './dashboard-page.component.html' })
export class DashboardPageComponent { loading = false; error = ''; load(): void {} }

