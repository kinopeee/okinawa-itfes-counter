#!/usr/bin/env bash
set -euo pipefail

ANDROID_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Android/Sdk}}"
SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

if ! command -v javac >/dev/null 2>&1 || [[ "$(javac -version 2>&1 | sed -E 's/^javac ([0-9]+).*/\1/')" != "17" ]]; then
    if (( EUID == 0 )); then
        apt-get update
        apt-get install -y openjdk-17-jdk
    elif command -v sudo >/dev/null 2>&1 && sudo -n true; then
        sudo -n apt-get update
        sudo -n apt-get install -y openjdk-17-jdk
    else
        printf '%s\n' 'JDK 17 is required; install openjdk-17-jdk and rerun this script.' >&2
        exit 1
    fi
fi

JAVAC_PATH="$(readlink -f "$(command -v javac)")"
JAVA_HOME="$(dirname "$(dirname "$JAVAC_PATH")")"
export JAVA_HOME
export ANDROID_HOME="$SDK_ROOT"
export ANDROID_SDK_ROOT="$SDK_ROOT"

mkdir -p "$SDK_ROOT"
if [[ ! -x "$SDKMANAGER" ]]; then
    temporary_directory="$(mktemp -d)"
    trap 'rm -rf "$temporary_directory"' EXIT
    curl --fail --location --retry 3 \
        https://dl.google.com/android/repository/commandlinetools-linux-16111833_latest.zip \
        --output "$temporary_directory/command-line-tools.zip"
    unzip -q "$temporary_directory/command-line-tools.zip" -d "$temporary_directory"
    mkdir -p "$SDK_ROOT/cmdline-tools/latest"
    cp -a "$temporary_directory/cmdline-tools/." "$SDK_ROOT/cmdline-tools/latest/"
fi

"$SDKMANAGER" --sdk_root="$SDK_ROOT" --install \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0"

escaped_sdk_root="${SDK_ROOT//\\/\\\\}"
printf 'sdk.dir=%s\n' "$escaped_sdk_root" > "$ANDROID_DIR/local.properties"
printf 'JDK 17: %s\nAndroid SDK: %s\n' "$JAVA_HOME" "$SDK_ROOT"
