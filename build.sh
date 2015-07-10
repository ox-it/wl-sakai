#!/bin/sh
#
# Short build script.
local_version=$(git describe --tags)
local_sakai=10.x

# If any command fails abort the build
set -e

# Check we have the correct java version
java_version=$(java -version 2>&1 | sed -n 's/^java version *"\(.*\)"/\1/p')
if echo $java_version | grep -q "1\.7\..*" ; then
  echo Found Sun JDK: $java_version
else
  echo You have to build with Sun JDK 1.7.x, we found:
  java -version 2>&1
  exit 1
fi

git submodule init
git submodule sync
git submodule update

# This checks that all the submodule are free from local changes.
# It checks for staged changes and also for removed files.
# The update-index is also needed to get the index back in sync with the filesystem ( git status does this behind the scenes ).
# To get everything back in sync do:
# git submodule update -f
git submodule foreach 'git update-index -q --refresh ; git diff-index --cached --quiet HEAD && git diff-files --quiet'

MAVEN_OPTS="-Xms168m -Xmx512m -XX:PermSize=128m -XX:NewSize=64m -Dmaven.test.skip=true"

export MAVEN_OPTS

rm -rf build
mvn clean install sakai:deploy -Dlocal.service=$local_version -Dlocal.sakai=$local_sakai -Dmaven.tomcat.home=$(pwd)/build/
(cd build && tar zcf ../sakai-${local_version}.tgz .)
