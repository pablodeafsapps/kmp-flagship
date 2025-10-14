#!/bin/bash

# SwiftLint Installation Script
# This script installs SwiftLint using Homebrew

set -e

echo "Installing SwiftLint..."

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "Homebrew is not installed. Please install Homebrew first:"
    echo "https://brew.sh/"
    exit 1
fi

# Install SwiftLint
brew install swiftlint

echo "SwiftLint installed successfully!"
echo "You can now run: ./gradlew swiftlint"
