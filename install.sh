#!/bin/bash

# Instalar Java (OpenJDK 11)
apt-get update && apt-get install -y openjdk-11-jdk

# Establecer JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verificar la instalación de Java
java -version

# Compilar el proyecto con Maven Wrapper
./mvnw clean package

