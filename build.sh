#!/bin/sh
#
# Short build script.

local_version=2.5-ox4.2
local_sakai=2.5.x

git submodule sync
git submodule update

MAVEN_OPTS="-Xms168m -Xmx512m -XX:PermSize=96m -XX:NewSize=64m -Dmaven.test.skip=true -Dsakai.withjsmath=true"

export MAVEN_OPTS

rm -rf build
mvn clean install sakai:deploy -Pfull,oxford -Dlocal.service=$local_version -Dlocal.sakai=$local_sakai -Dmaven.tomcat.home=$(pwd)/build/ || exit 1
(cd build && tar zcf ../sakai-${local_version}.tgz .)
