# CRM Product Requirements

## Product Overview

The CRM application helps a small sales team manage companies, contacts, leads, opportunities, and customer interactions in one full-stack web application. The MVP focuses on making the sales process visible and consistent before adding advanced automation, reporting, or integrations.

## Target Users

### Admin

Admins maintain users, planned roles, configuration, and data quality. They need broad access because they are responsible for keeping the CRM reliable.

### Manager

Managers review sales activity, assign leads, monitor pipeline progress, and use dashboard summaries to understand team performance.

### Sales Representative

Sales representatives create and update companies, contacts, leads, opportunities, and activities during daily sales work.

## Business Problems

- Sales information is scattered across notes, spreadsheets, and conversations.
- Managers lack a consistent view of lead ownership and pipeline status.
- Representatives need a clear process for qualifying leads and tracking follow-ups.
- Customer history is difficult to reuse when activities are not tied to CRM records.

## Expected Benefits

- Centralized records for companies, contacts, leads, opportunities, and activities.
- Clear ownership and stage tracking for sales work.
- Better follow-up history for customer conversations.
- A technical foundation for authentication, role-based access, and reporting in later months.

## Primary Workflows

1. Create a company record with basic organization details.
2. Add one or more contacts to a company.
3. Create a lead and capture source, status, and interest level.
4. Assign a lead to a sales representative.
5. Qualify or disqualify a lead based on defined rules.
6. Convert a qualified lead into an opportunity.
7. Move an opportunity through the sales pipeline.
8. Log calls, emails, meetings, and notes as activities.

## MVP Module Acceptance Criteria

### Companies

- Users can view a list of companies.
- Users can create and edit company details.
- Company detail pages show related contacts and planned activity history.

### Contacts

- Users can create contacts linked to companies.
- Users can store name, email, phone, job title, and notes.
- Validation prevents empty required fields and malformed emails.

### Leads

- Users can create, update, assign, qualify, and disqualify leads.
- Leads have visible statuses: NEW, CONTACTED, QUALIFIED, DISQUALIFIED.
- Qualified leads can be converted to opportunities after the conversion workflow is implemented.

### Opportunities

- Opportunities belong to a company and an owner.
- Opportunities move through planned stages from prospecting to won or lost.
- Each opportunity tracks estimated value and expected close date.

### Activities

- Users can log notes, calls, emails, and meetings.
- Activities are linked to a related CRM record and author.
- Activity history is shown in chronological order.

### Dashboard

- The dashboard shows planned summary cards for leads, opportunities, and activities.
- Empty states explain when no data is available.
- Production metrics are deferred until real domain entities exist.

## Non-Functional Requirements

- Validate required fields and return predictable error messages.
- Keep ownership and assignment rules explicit.
- Use responsive layouts for desktop and mobile.
- Preserve basic accessibility with semantic HTML, labels, contrast, and keyboard navigation.
- Include backend and frontend smoke tests as implementation begins.
- Avoid hardcoded secrets and real customer or employer data.

## Risks And Assumptions

- Lead conversion rules may change after the first domain model review.
- Team assignment rules need more detail before role-based authorization.
- Reporting definitions should wait until the opportunity model is stable.
- The MVP assumes a modular monolith backend and a separate Angular SPA.
