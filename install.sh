#!/bin/bash

# Descargar e instalar Java manualmente
curl -L -o openjdk.tar.gz https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz
mkdir -p /opt/java
tar -xzf openjdk.tar.gz -C /opt/java

# Configurar JAVA_HOME y PATH
export JAVA_HOME=/opt/java/jdk-11.0.2
export PATH=$JAVA_HOME/bin:$PATH

# Verificar la instalación de Java
java -version

# Compilar el proyecto con Maven Wrapper
./mvnw clean package
