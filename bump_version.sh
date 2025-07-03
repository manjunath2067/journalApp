#!/bin/bash

# This script handles automatic version bumping for Maven projects.
# It increments the patch version (e.g., 0.0.1 -> 0.0.2 or 0.0.1-SNAPSHOT -> 0.0.2).
#
# LIMITATIONS:
# - Currently designed for simple patch increments.
# - Does not handle complex versioning schemes like pre-releases (alpha, beta, rc)
#   or build metadata without modification. For such cases, a more sophisticated
#   versioning tool or script logic would be required.

set -e # Exit immediately if a command exits with a non-zero status.

# Function to extract version from pom.xml
get_current_version() {
    CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    echo "$CURRENT_VERSION"
}

# Get the current version
CURRENT_POM_VERSION=$(get_current_version)
echo "Current version in pom.xml: $CURRENT_POM_VERSION"

# Increment patch version
# Remove -SNAPSHOT if present
VERSION_NO_SNAPSHOT=${CURRENT_POM_VERSION%-SNAPSHOT}
IFS='.' read -r -a VERSION_PARTS <<< "$VERSION_NO_SNAPSHOT"

MAJOR=${VERSION_PARTS[0]}
MINOR=${VERSION_PARTS[1]}
PATCH=${VERSION_PARTS[2]}

# Increment patch version
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$NEW_PATCH"

echo "New version will be: $NEW_VERSION"

# Update version in pom.xml using Maven versions plugin
mvn versions:set -DnewVersion="$NEW_VERSION" -DprocessAllModules=true
mvn versions:commit -DprocessAllModules=true

echo "Version updated in pom.xml to $NEW_VERSION"

# Update version in README.md
# The sed command needs to handle the old version string, which might include -SNAPSHOT
# Escape dots for regex and handle the -SNAPSHOT part if present
OLD_VERSION_REGEX=$(echo "$CURRENT_POM_VERSION" | sed 's/\./\\./g' | sed 's/-/\\-/g')

# Replace the version in README.md
# Using a temporary file for sed compatibility on different systems (macOS vs Linux)
sed "s/\(\*\*Journal App\*\* - Version: \)$OLD_VERSION_REGEX/\1$NEW_VERSION/" README.md > README.md.tmp && mv README.md.tmp README.md

echo "README.md updated to version $NEW_VERSION"

echo "Version bump complete."
echo "$NEW_VERSION" > output_new_version.txt # Output new version to a file
