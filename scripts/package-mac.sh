#!/usr/bin/env bash
set -euo pipefail

APP_NAME="ResumeCraft"
BUNDLE_ID="com.zylo.resumecraft"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERVER_DIR="$REPO_ROOT/server"
JAR_NAME="resume-craft-server-0.1.0.jar"
JAR_PATH="$SERVER_DIR/target/$JAR_NAME"
PACKAGE_DIR="$REPO_ROOT/target/mac-app"

SKIP_FRONTEND_BUILD="${SKIP_FRONTEND_BUILD:-false}"

assert_command() {
    if ! command -v "$1" &>/dev/null; then
        echo "Error: $1 was not found. $2" >&2
        exit 1
    fi
}

assert_command "java" "Install JDK 17+ and make sure java is on PATH."
assert_command "mvn" "Install Maven 3.9+ and make sure mvn is on PATH."

# Build frontend
if [ "$SKIP_FRONTEND_BUILD" != "true" ]; then
    assert_command "npm" "Install Node.js 18+ and make sure npm is on PATH."
    echo "Building frontend..."
    (cd "$REPO_ROOT" && npm run build:desktop)
fi

# Build backend
echo "Building backend..."
(cd "$SERVER_DIR" && mvn clean -DskipTests package)

if [ ! -f "$JAR_PATH" ]; then
    echo "Error: Backend jar was not created: $JAR_PATH" >&2
    exit 1
fi

assert_command "jpackage" "Install a full JDK 17+ that includes jpackage."

# Clean output directory
rm -rf "$PACKAGE_DIR"
mkdir -p "$PACKAGE_DIR"

echo "Running jpackage..."
jpackage \
    --type app-image \
    --name "$APP_NAME" \
    --dest "$PACKAGE_DIR" \
    --input "$SERVER_DIR/target" \
    --main-jar "$JAR_NAME" \
    --java-options "-Dspring.profiles.active=local" \
    --java-options "-Dfile.encoding=UTF-8" \
    --java-options "-Djava.awt.headless=false" \
    --java-options "-DRESUMECRAFT_DESKTOP_APP=true" \
    --mac-package-identifier "$BUNDLE_ID" \
    --mac-package-name "$APP_NAME"

APP_PATH="$PACKAGE_DIR/$APP_NAME.app"
if [ ! -d "$APP_PATH" ]; then
    echo "Error: jpackage finished, but .app was not found: $APP_PATH" >&2
    exit 1
fi

echo ""
echo "Mac app image created:"
echo "$APP_PATH"
echo ""
echo "To open: open \"$APP_PATH\""
