#!/usr/bin/env sh

PROG8CLASSPATH=./compiler/build/classes/kotlin/main:./compiler/build/resources/main
KOTLINPATH=${HOME}/.IntelliJIdea2019.1/config/plugins/Kotlin
LIBJARS=${KOTLINPATH}/lib/kotlin-stdlib.jar:${KOTLINPATH}/lib/kotlin-reflect.jar

java -cp ${PROG8CLASSPATH}:${LIBJARS} prog8.StackVmMainKt $*
