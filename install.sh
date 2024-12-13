#!/bin/bash

# Crear un directorio temporal para el JDK en el proyecto
mkdir -p ./java

# Descargar OpenJDK 11 desde una fuente confiable (Adoptium)
curl -L -o openjdk.tar.gz https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.20.1%2B1/OpenJDK11U-jdk_x64_linux_hotspot_11.0.20.1_1.tar.gz

# Extraer el JDK al directorio temporal
tar -xzf openjdk.tar.gz -C ./java --strip-components=1

# Configurar JAVA_HOME y PATH
export JAVA_HOME=$(pwd)/java
export PATH=$JAVA_HOME/bin:$PATH

# Verificar la instalación de Java
java -version

# Compilar el proyecto con Maven Wrapper
./mvnw clean package
