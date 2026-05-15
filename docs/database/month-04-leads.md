# Month 4 Database Notes: Leads

Month 4 extends the Month 2 lead table and adds durable lead event history through `V4__month_04_lead_management.sql`.

## Lead Columns

- `first_name`, `last_name`, `email`, `phone`
- `company_name` for free-text prospect company names
- optional `company_id` link to Month 3 companies
- `created_by_id` for creator ownership
- existing `assigned_to_id` for assignment ownership
- `assigned_at`, `deleted_at`, and optimistic `version`
- `source` and `status` enum-style check constraints

## Lead Events

`lead_events` records business history for creation, updates, assignments, unassignments, status changes, and deletions.

```text
lead_events
- id uuid pk
- lead_id uuid fk leads(id)
- event_type varchar(32)
- old_value varchar(255)
- new_value varchar(255)
- performed_by_user_id uuid fk app_users(id)
- created_at timestamptz
```

## Indexes

- `(assigned_to_id, status)` for representative queues
- `created_at` for recent lead lists
- `company_id` for linked company filters
- `created_by_id` for creator visibility
- lower-case name/email expression index for keyword search
- `(lead_id, created_at desc)` for history reads
