#!/usr/bin/env bash
set -euo pipefail

echo "Running St Marys Digital Library..."

# default mode is GUI; pass "console" for console mode
mode="${1:-gui}"

CP="out:lib/sqlite-jdbc.jar"

if [ ! -d "out" ]; then
  echo "Build output 'out' directory is missing. Run ./build.sh or build.bat first." >&2
  exit 1
fi

if [ ! -f "lib/sqlite-jdbc.jar" ]; then
  echo "Missing library: lib/sqlite-jdbc.jar" >&2
  exit 1
fi

if [ "$mode" = "console" ]; then
  echo "Starting console mode..."
  java -cp "$CP" stmarys.library.Main console
else
  echo "Starting GUI mode..."
  java -cp "$CP" stmarys.library.Main
fi
