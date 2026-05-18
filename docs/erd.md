# Entity Relationship Model

## Initial ERD

```text
users 1---* leads
users 1---* opportunities
users 1---* activities
roles 1---* users
companies 1---* contacts
companies 1---* opportunities
companies 1---* leads
leads 0..1---0..1 opportunities
opportunities 1---* opportunity_stage_history
companies 1---* activities
contacts 1---* activities
leads 1---* activities
opportunities 1---* activities
```

## Entities

### roles

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `name` | ADMIN, MANAGER, SALES_REPRESENTATIVE. |
| `created_at` | Audit timestamp. |

A role enum may be enough for the MVP, but a `roles` table leaves room for future administration.

### users

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `role_id` | Required role reference. |
| `full_name` | Display name. |
| `email` | Unique login identifier. |
| `password_hash` | Added with authentication in Month 2. |
| `active` | Allows account disabling. |
| `created_at`, `updated_at` | Audit timestamps. |

### companies

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `name` | Required company name. |
| `industry` | Optional classification. |
| `website` | Optional URL. |
| `phone` | Optional main phone. |
| `owner_id` | User responsible for the account. |
| `deleted_at` | Nullable soft-delete timestamp. |
| `created_at`, `updated_at` | Audit timestamps. |

### contacts

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `company_id` | Required parent company. |
| `first_name`, `last_name` | Required contact name. |
| `email` | Optional but validated when present. |
| `phone` | Optional. |
| `job_title` | Optional. |
| `deleted_at` | Nullable soft-delete timestamp. |
| `created_at`, `updated_at` | Audit timestamps. |

### leads

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `company_id` | Optional or newly created company during conversion. |
| `assigned_to_id` | User assigned to qualify the lead. |
| `source` | Lead source such as WEBSITE, REFERRAL, OUTBOUND. |
| `status` | NEW, CONTACTED, QUALIFIED, UNQUALIFIED. |
| `summary` | Short business need. |
| `created_at`, `updated_at` | Audit timestamps. |

### opportunities

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `company_id` | Required company reference. |
| `owner_id` | Responsible user. |
| `lead_id` | Optional converted lead reference. |
| `stage` | PROSPECTING, QUALIFICATION, PROPOSAL, NEGOTIATION, WON, LOST. |
| `estimated_value` | Decimal amount. |
| `expected_close_date` | Planned close date. |
| `created_at`, `updated_at` | Audit timestamps. |

### activities

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `author_id` | User who logged the activity. |
| `type` | NOTE, CALL, EMAIL, MEETING. |
| `subject` | Short title. |
| `body` | Activity notes. |
| `activity_at` | When the interaction happened. |
| `related_record_type` | COMPANY, CONTACT, LEAD, OPPORTUNITY. |
| `related_record_id` | UUID of the related record. |

### opportunity_stage_history

| Field | Notes |
| --- | --- |
| `id` | UUID primary key. |
| `opportunity_id` | Required opportunity reference. |
| `from_stage`, `to_stage` | Stage transition values. |
| `changed_by_id` | User who changed the stage. |
| `changed_at` | Transition timestamp. |

## Relationship Assumptions

- A company has many contacts.
- A company may have many leads and opportunities.
- A lead may be assigned to one user.
- A qualified lead may become one opportunity.
- An opportunity belongs to one company and one owner.
- Activities use a generic related-record reference to avoid many nullable foreign keys in the first MVP design.

