import { NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  label: string;
  route: string;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [NgFor, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.component.html',
  styleUrls: ['./app-shell.component.css']
})
export class AppShellComponent {
  readonly navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Companies', route: '/companies' },
    { label: 'Contacts', route: '/contacts' },
    { label: 'Leads', route: '/leads' },
    { label: 'Opportunities', route: '/opportunities' },
    { label: 'Activities', route: '/activities' },
    { label: 'Status', route: '/status' }
  ];
}
