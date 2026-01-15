#!/bin/bash

# This script handles automatic version bumping for Maven projects.
# It increments the patch version (e.g., 0.0.1 -> 0.0.2 or 0.0.1-SNAPSHOT -> 0.0.2).
#
# LIMITATIONS:
# - Currently designed for simple patch increments.
# - Does not handle complex versioning schemes like pre-releases (alpha, beta, rc)
#   or build metadata without modification. For such cases, a more sophisticated
#   versioning tool or script logic would be required.

# Removing 'set -e' to allow script to log errors from individual commands
# set -e

echo "--- Starting Version Bump Script ---"

# Function to extract version from pom.xml
get_current_version() {
    echo "Attempting to get current version from pom.xml..."
    CURRENT_VERSION_OUTPUT=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
    MVN_STATUS=$?
    if [ $MVN_STATUS -ne 0 ]; then
        echo "ERROR: mvn help:evaluate failed with status $MVN_STATUS"
        echo "Output: $CURRENT_VERSION_OUTPUT"
        exit $MVN_STATUS
    fi
    if [ -z "$CURRENT_VERSION_OUTPUT" ]; then
        echo "ERROR: mvn help:evaluate returned an empty version."
        exit 1
    fi
    echo "$CURRENT_VERSION_OUTPUT"
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

if [ -z "$MAJOR" ] || [ -z "$MINOR" ] || [ -z "$PATCH" ]; then
    echo "ERROR: Could not parse version parts from $VERSION_NO_SNAPSHOT (derived from $CURRENT_POM_VERSION)"
    exit 1
fi

# Increment patch version
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$NEW_PATCH"

echo "New version will be: $NEW_VERSION"

# Update version in pom.xml using Maven versions plugin
echo "Attempting to set version in pom.xml to $NEW_VERSION..."
MVN_SET_OUTPUT=$(mvn versions:set -DnewVersion="$NEW_VERSION" -DprocessAllModules=true 2>&1)
MVN_SET_STATUS=$?

echo "mvn versions:set output:"
echo "$MVN_SET_OUTPUT"

if [ $MVN_SET_STATUS -ne 0 ]; then
    echo "ERROR: mvn versions:set failed with status $MVN_SET_STATUS."
    exit $MVN_SET_STATUS
fi
echo "Successfully ran mvn versions:set."

echo "Attempting to commit version changes in pom.xml..."
MVN_COMMIT_OUTPUT=$(mvn versions:commit -DprocessAllModules=true 2>&1)
MVN_COMMIT_STATUS=$?

echo "mvn versions:commit output:"
echo "$MVN_COMMIT_OUTPUT"

if [ $MVN_COMMIT_STATUS -ne 0 ]; then
    echo "ERROR: mvn versions:commit failed with status $MVN_COMMIT_STATUS."
    # Even if commit fails, versions:set might have left pom.xml.versionsBackup files.
    # It's safer to exit here as the state of pom.xml is uncertain for README update.
    exit $MVN_COMMIT_STATUS
fi
echo "Successfully ran mvn versions:commit."

echo "Version updated in pom.xml to $NEW_VERSION (verified by script logic, pom should reflect this)."

# Update version in README.md
echo "Attempting to update README.md..."
# The sed command needs to handle the old version string, which might include -SNAPSHOT
# Escape dots for regex and handle the -SNAPSHOT part if present
OLD_VERSION_REGEX=$(echo "$CURRENT_POM_VERSION" | sed 's/\./\\./g' | sed 's/-/\\-/g')

sed "s/\(\*\*Journal App\*\* - Version: \)$OLD_VERSION_REGEX/\1$NEW_VERSION/" README.md > README.md.tmp
SED_STATUS=$?
if [ $SED_STATUS -ne 0 ]; then
    echo "ERROR: sed command failed to update README.md (status $SED_STATUS)."
    rm -f README.md.tmp # Clean up temp file
    exit $SED_STATUS
fi

mv README.md.tmp README.md
MV_STATUS=$?
if [ $MV_STATUS -ne 0 ]; then
    echo "ERROR: mv command failed to replace README.md (status $MV_STATUS)."
    exit $MV_STATUS
fi

echo "README.md updated to version $NEW_VERSION."

echo "Version bump process seems complete. Writing new version to output_new_version.txt"
echo "$NEW_VERSION" > output_new_version.txt

echo "--- Finished Version Bump Script ---"
