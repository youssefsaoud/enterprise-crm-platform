#!/bin/sh
set -eu

if [ "${CRM_RECRUITER_DEMO_ENABLED:-false}" = "true" ]; then
  case ",${SPRING_PROFILES_ACTIVE:-}," in
    *,prod,*) echo "Demo mode cannot run with production." >&2; exit 1 ;;
  esac
  if [ "${CRM_DB_URL:-}" != "jdbc:postgresql://postgres:5432/orqestra_demo" ]; then
    echo "Demo mode requires its isolated Docker database." >&2
    exit 1
  fi
  export CRM_JWT_SECRET="$(head -c 48 /dev/urandom | base64 | tr -d '\n')"
fi

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
