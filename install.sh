#!/bin/bash
java -version

# Establece la variable JAVA_HOME (puedes adaptarlo si el entorno ya tiene Java)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Compila el proyecto con Maven
./mvnw clean package
