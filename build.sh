#!/bin/sh
#
# Short build script.
local_version=10-ox1-alpha9
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

MAVEN_OPTS="-Xms168m -Xmx512m -XX:PermSize=128m -XX:NewSize=64m -Dmaven.test.skip=true"

export MAVEN_OPTS

rm -rf build
mvn clean install sakai:deploy -Dlocal.service=$local_version -Dlocal.sakai=$local_sakai -Dmaven.tomcat.home=$(pwd)/build/
(cd build && tar zcf ../sakai-${local_version}.tgz .)
