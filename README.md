Maven extension to modify plugins.

# Goal
modify compiler for maven projects to use non javac compiler.

# Non Goal
modify entire pom


to activate extension pass `-Decj-native=1` while compilitation.
extension expects `~/.m2/compile-config.xml` to have something like this.

```xml
<!-- <?xml version="1.0" encoding="UTF-8"?> -->
<configuration>
  <fork>true</fork>
  <executable>/home/prasanth/devil/ecj-native/ecj-native</executable>
  <compilerArgs>
    <arg>-proceedOnError</arg>
  </compilerArgs>
  <target>1.8</target>
  <source>1.8</source>
</configuration>
```