#!/bin/bash

cp ../src/bundles/pom.xml bundles/pom.xml

cd bundles && ./create_openhab_binding_skeleton.sh $1 nexiles nexiles

NAME=`echo "$1" | tr '[:upper:]' '[:lower:]'`
PACKAGE_NAME="org.openhab.binding.${NAME}"

mkdir -p ../../src/bundles

cp pom.xml ../../src/bundles/
cp ../pom.xml ../../src/

mv ${PACKAGE_NAME} ../../src/bundles

cp -r ../tools/ ../../src/tools
