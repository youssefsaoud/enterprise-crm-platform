#!/bin/sh
set -eu

if [ "${#CRM_JWT_SECRET}" -lt 32 ]; then
  echo "CRM_JWT_SECRET must contain at least 32 characters." >&2
  exit 1
fi

if [ "${CRM_BOOTSTRAP_ADMIN_ENABLED:-false}" = "true" ]; then
  if [ -z "${CRM_BOOTSTRAP_ADMIN_EMAIL:-}" ] || [ "${#CRM_BOOTSTRAP_ADMIN_PASSWORD}" -lt 12 ]; then
    echo "Admin bootstrap requires a private email and a password of at least 12 characters." >&2
    exit 1
  fi
fi

exec java -jar /app/crm-api.jar "$@"
