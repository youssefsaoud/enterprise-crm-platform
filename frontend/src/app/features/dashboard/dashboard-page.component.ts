import { Component } from '@angular/core';

@Component({ selector: 'app-dashboard-page', standalone: true, templateUrl: './dashboard-page.component.html' })
export class DashboardPageComponent { activityTarget(): string { return 'CRM record'; } stagePercent(): number { return 6; } }

