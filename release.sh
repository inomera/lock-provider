#!/bin/bash
set -e

# ensure we're up to date
git pull

# Validate release type parameter
RELEASE_TYPE=""
if [[ "${1}" = "major" ]] || [[ "${1}" = "minor" ]] || [[ "${1}" = "patch" ]]; then
  RELEASE_TYPE="${1}"
elif [[ ${1} = "" ]]; then
  RELEASE_TYPE="patch"
else
  echo "Illegal argument: ${1}"
  echo "Usage: ./release.sh [patch|minor|major]"
  exit 1
fi

echo "${RELEASE_TYPE}"

forceNewVersion="false"
snapshot="false"

shift
until [[ "$#" == "0" ]]; do
    case "$1" in
        --force-new-version )
            forceNewVersion="true"
            ;;
        --snapshot )
            snapshot="true"
            ;;
        * )
          echo "Unknown option: $1"
          exit 1
          ;;
    esac
    shift
done

echo "forceNewVersion=${forceNewVersion}"

# Check if the current commit is already versioned
previousVersion=$(cat VERSION)
alreadyVersioned=$(git tag -l --points-at HEAD "${previousVersion}")
if [[ "${alreadyVersioned}" != "" ]]; then
  echo "Current HEAD already contains version tag ${alreadyVersioned}"
fi

newVersion="false"
if [[ "${alreadyVersioned}" = "" ]] || [[ "${forceNewVersion}" = "true" ]]; then
  if [[ "${snapshot}" = "false" ]]; then
    newVersion="true"

    # bump (increment) version
    docker run --rm -v "$PWD":/app treeder/bump "${RELEASE_TYPE}"
  fi
fi

version=$(cat VERSION)
echo "Version: ${version}"

# run build & tests
./gradlew clean build

if [[ "${newVersion}" = "true" ]]; then
  # tag it
  git add -A
  git commit -m "Release version ${version}"
  git tag -a "${version}" -m "Version ${version}"
  git push
  git push --tags
fi

export SNAPSHOT_RELEASE="${snapshot}"

# publish it
./gradlew :lock-provider:publish
