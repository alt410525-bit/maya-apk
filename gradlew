#!/bin/sh
# Gradle wrapper script

DIR="$(cd "$(dirname "$0")" && pwd)"
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(cd "$(dirname "$0")" && pwd)

# Use JAVA_HOME if set
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="java"
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Download wrapper if missing
if [ ! -f "$CLASSPATH" ]; then
    echo "Downloading Gradle wrapper..."
    WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
    mkdir -p "$APP_HOME/gradle/wrapper"
    curl -fsSL -o "$CLASSPATH" "$WRAPPER_URL" 2>/dev/null || \
    wget -q -O "$CLASSPATH" "$WRAPPER_URL" 2>/dev/null || {
        echo "Failed to download gradle-wrapper.jar"
        echo "Manually run: gradle wrapper --gradle-version 8.2"
        exit 1
    }
fi

exec $JAVA_CMD -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
