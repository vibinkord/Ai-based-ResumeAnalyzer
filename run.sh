#!/usr/bin/env bash
set -euo pipefail

# Resolve to the project root (handles being called from anywhere)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Stop any existing Java process using port 8082
echo "Checking for existing processes on port 8082..."
PIDS=$(tasklist | grep -i "java.exe" | awk '{print $2}' || true)
if [ -n "$PIDS" ]; then
    echo "Stopping existing Java processes..."
    for PID in $PIDS; do
        taskkill //F //PID $PID 2>/dev/null || true
    done
    sleep 2
fi

# Start the Spring Boot application using the Maven Wrapper
./mvnw spring-boot:run
