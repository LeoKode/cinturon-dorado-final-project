#!/bin/bash

# Crear un directorio temporal para el JDK en el proyecto
mkdir -p ./java

# Descargar OpenJDK 11
curl -L -o openjdk.tar.gz https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz

# Extraer el JDK al directorio temporal
tar -xzf openjdk.tar.gz -C ./java --strip-components=1

# Configurar JAVA_HOME y PATH
export JAVA_HOME=$(pwd)/java
export PATH=$JAVA_HOME/bin:$PATH

# Verificar la instalación de Java
java -version

# Compilar el proyecto con Maven Wrapper
./mvnw clean package

