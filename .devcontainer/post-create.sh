#!/bin/bash
set -euo pipefail

WORKSPACE="${CONTAINER_WORKSPACE_FOLDER:-$(pwd)}"
USER_ID="$(id -u)"
GROUP_ID="$(id -g)"

echo "Initializing Maven development environment in $WORKSPACE"

sudo mkdir -p "$HOME/.m2/repository"
sudo chown -R "$USER_ID:$GROUP_ID" "$HOME/.m2"

find "$WORKSPACE" \
    -name pom.xml \
    -not -path '*/target/*' \
    -print0 |
while IFS= read -r -d '' pom; do
    module="$(dirname "$pom")"
    target="$module/target"

    echo "Initializing $target"

    sudo mkdir -p "$target"
    sudo chown -R "$USER_ID:$GROUP_ID" "$target"
done

echo
echo "Java:"
java -version

echo
echo "Maven:"
mvn -version

echo
echo "Node:"
node --version

echo
echo "npm:"
npm --version

echo
echo "Environment ready."