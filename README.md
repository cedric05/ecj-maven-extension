Maven extension to modify plugins.

# Goal
Modify compiler for maven projects to use non javac compiler.

# Non Goal
Modify entire pom or other plugins.

## Install

⚠️ minimal required maven version is `3.6.3`
[Follow](https://maven.apache.org/studies/extension-demo/)

#### Configure for all projects

Or add jar file in `${maven.home}/lib/ext/` (enables for all projects)

#### Configure for specific project
just create or update `${basedir}/.mvn/extensions.xml` file

```xml
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd">
    <extension>
        <groupId>me.qoomon</groupId>
        <artifactId>maven-git-versioning-extension</artifactId>
        <version>5.2.0</version>
    </extension>
</extensions>
```



## Usage
Follows profile behaviour like we have in maven. User can define multiple profiles in `~/.m2/compile-config.xml`. 

To activate particular profile `java8` pass `-Decj-native=java8` while running `$ mvn compile`.
extension expects `~/.m2/compile-config.xml` to have something like this.

Schema for compile-config.xml is defined [here](./compile-config.xsd)


## sample config
```xml
<!-- <?xml version="1.0" encoding="UTF-8"?> -->
<profiles>
  <profile id="java8">
    <configuration>
      <fork>true</fork>
      <executable>/usr/bin/javac</executable>
      <compilerArgs>
        <arg>-proceedOnError</arg>
      </compilerArgs>
      <target>1.8</target>
      <source>1.8</source>
       <failOnError>false</failOnError>
    </configuration>
    <dependencies>
      <dependency>
        <version>1.0</version>
        <artifactId>rt.jar</artifactId>
        <groupId>rt.jar</groupId>
        <scope>system</scope>
        <systemPath>/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/lib/resources.jar</systemPath>
      </dependency>
    </dependencies>
  </profile>
</profiles>
```