#!/bin/sh
set -e

# Render expone la connection string de PostgreSQL en formato libpq:
#   postgresql://user:pass@host[:port]/db
# Spring/HikariCP requieren formato JDBC:
#   jdbc:postgresql://host[:port]/db
# con username y password como propiedades separadas.
#
# Este script detecta el formato libpq y lo convierte. Si SPRING_DATASOURCE_URL
# ya viene en formato JDBC (o no esta definido), no toca nada.

if [ -n "$SPRING_DATASOURCE_URL" ]; then
  case "$SPRING_DATASOURCE_URL" in
    postgresql://*|postgres://*)
      rest="${SPRING_DATASOURCE_URL#*://}"
      case "$rest" in
        *@*)
          creds="${rest%%@*}"
          hostdb="${rest#*@}"
          # Solo sobrescribir si las vars no estan ya definidas explicitamente
          if [ -z "$SPRING_DATASOURCE_USERNAME" ]; then
            SPRING_DATASOURCE_USERNAME="${creds%%:*}"
            export SPRING_DATASOURCE_USERNAME
          fi
          if [ -z "$SPRING_DATASOURCE_PASSWORD" ]; then
            case "$creds" in
              *:*) SPRING_DATASOURCE_PASSWORD="${creds#*:}" ; export SPRING_DATASOURCE_PASSWORD ;;
            esac
          fi
          SPRING_DATASOURCE_URL="jdbc:postgresql://${hostdb}"
          ;;
        *)
          SPRING_DATASOURCE_URL="jdbc:postgresql://${rest}"
          ;;
      esac
      export SPRING_DATASOURCE_URL
      echo "[entrypoint] SPRING_DATASOURCE_URL normalizado a formato JDBC."
      ;;
  esac
fi

exec java -Dserver.port="${PORT:-8080}" -jar /app/app.jar
