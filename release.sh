#!/bin/bash
set -e

# ensure we're up to date
git pull

function validate_release_type() {
    release_type="${1}"
    if [[ "${release_type}" = "major" ]] \
      || [[ "${release_type}" = "minor" ]] \
      || [[ "${release_type}" = "patch" ]] \
      || [[ "${release_type}" = "latest" ]]; then
      echo "Release type: ${release_type}"
    else
      echo "Illegal release type argument: ${release_type}"
      echo "Use one of: [patch|minor|major|latest]"
      exit 1
    fi
}

function validate_project() {
  project="${1}"
  if [[ "${project}" = "reentrant" ]] \
    || [[ "${project}" = "hazelcast-3x" ]] \
    || [[ "${project}" = "hazelcast-4x" ]] \
    || [[ "${project}" = "redis" ]] \
    || [[ "${project}" = "zookeeper" ]] \
    || [[ "${project}" = "api" ]]; then
      echo "Project is ${project}"
  else
    echo "Illegal release type argument: ${project}"
    echo "Use one of: [reentrant|hazelcast-3x|hazelcast-4x|redis|zookeeper|api]"
    exit 1
  fi
}

forceNewVersion="false"
snapshot="false"
project=""
release_type="patch"
project_dir=""
root_project_dir="${PWD}"

until [[ "$#" == "0" ]]; do
    case "$1" in
        --release-type )
            shift
            release_type="${1}"
            ;;
        --force-new-version )
            forceNewVersion="true"
            echo "forceNewVersion=${forceNewVersion}"
            ;;
        --snapshot )
            snapshot="true"
            ;;
        --project )
            shift
            project="${1}"
            ;;
        * )
          echo "Unknown option: ${1}"
          exit 1
          ;;
    esac
    shift
done

validate_release_type "${release_type}"
validate_project "${project}"

# [reentrant|hazelcast-3x|hazelcast-4x|redis|zookeeper|api]
if [[ "${project}" = "reentrant" ]]; then
  project_dir="${PWD}/implementations/reentrant"
elif [[ "${project}" = "hazelcast-3x" ]]; then
  project_dir="${PWD}/implementations/hazelcast-3x"
elif [[ "${project}" = "hazelcast-4x" ]]; then
  project_dir="${PWD}/implementations/hazelcast-4x"
elif [[ "${project}" = "redis" ]]; then
  project_dir="${PWD}/implementations/redis"
elif [[ "${project}" = "zookeeper" ]]; then
  project_dir="${PWD}/implementations/zookeeper"
elif [[ "${project}" = "api" ]]; then
  project_dir="${PWD}/lock-provider"
fi

cd "${project_dir}"
echo "Current directory: ${PWD}"

version_file="${project_dir}/VERSION"
version_prefix="${project}"

# Check if the current commit is already versioned
previousVersion=$(cat "${version_file}")
git_tag="${version_prefix}/${previousVersion}"
alreadyVersioned=$(git tag -l --points-at HEAD "${git_tag}")
if [[ "${alreadyVersioned}" != "" ]]; then
  echo "Current HEAD already contains version tag ${alreadyVersioned}"
fi

newVersion="false"
if [[ "${alreadyVersioned}" = "" ]] || [[ "${forceNewVersion}" = "true" ]]; then
  if [[ "${snapshot}" = "false" ]] && [[ "${release_type}" != "latest" ]]; then
    newVersion="true"

    # bump (increment) version
    docker run --rm -v "$PWD":/app treeder/bump "${release_type}"

    while [[ "$(git tag -l "$(cat "${version_file}")")" != "" ]]
    do
      echo "Tag $(cat "${version_file}") already exists, patching"
      docker run --rm -v "$PWD":/app treeder/bump "patch"
    done
  fi
fi

version=$(cat "${version_file}")
git_tag="${version_prefix}/${version}"
echo "Version: ${version}"

export SNAPSHOT_RELEASE="${snapshot}"

# run build & tests
"${root_project_dir}/gradlew" clean build

if [[ "${newVersion}" = "true" ]]; then
  # tag it
  git add -A
  git commit -m "Release version ${version} for project ${project}"
  git tag -a "${git_tag}" -m "Release version ${version} for project ${project}"
  git push
  git push --tags
fi

# publish it
cd "${root_project_dir}"

if [[ "${snapshot}" = "false" ]]; then
  "${root_project_dir}/gradlew" :"lock-provider-${project}":publish closeAndReleaseRepository
else
  "${root_project_dir}/gradlew" :"lock-provider-${project}":publish
fi
