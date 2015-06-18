# The Jenkins NWDI-Core-Plugin guide

## Introduction

The NWDI-Core-Plugin integrates the SAP NetWeaver development infrastructure (NWDI) into the Jenkins continuous integration environment.The plugin adds various new capabilities to Jenkins:

* A new project type that lets the user configure a CBS build space (or NWDI track) to monitor for changes and build.

* A new source code management system (SCM) that monitors the configured track for changes to development components.

* A new type of build that synchronizes changed development components (and their dependencies) to a Jenkins workspace and builds those components.
The plugin also exposes an object model to be used by other plugins to add functionality with respect to SAP NetWeaver development components to Jenkins.
## Building and installing the plugin

The plugin is not available through the Jenkins update center yet. To build the plugin you'll need to clone the following GitHub repositories:

See https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment for inital Jenkins plugin configuration. Esp. settings.xml settings.

```
git clone git://github.com/weigo/NWDI-config-plugin.git
git clone git://github.com/weigo/NWDI-pom-Plugin.git
git clone git://github.com/weigo/NWDI-Core-Plugin.git
```
and build the Maven projects:
```
for d in NWDI-config-plugin NWDI-pom-Plugin NWDI-Core-Plugin;\
  do (cd $d; mvn install); done
```
in **NWDI-Core-Plugin/target** you'll find the **NWDI-Core-Plugin.hpi** file which you should upload using the Jenkins update center extended settings view.
## NetWeaver DI command line tools installation


### NetWeaver 7.0.x

With NetWeaver 7.0.x the DI command tools are provided together with NetWeaver developer studio. They are located in the sub folder **tools** beneath your NWDS installation folder. Copy this folder to your Jenkins server.

### NetWeaver 7.1.+

NetWeaver versions more recent than 7.0.x do not provide the DI command tools with the NetWeaver developer studio. You'll need to download the software component archive (SCA) **DICLIENTS.SCA** from SAP market place (enter 'DICLIENTS' as search term).

Unzip the SCA. In the sub folder **DEPLOYARCHIVES** there is the SDA **tc~di~cmd_tools~sda.sda** . Extract the ZIP archive **di_cmd_tools.zip** and copy it to your Jenkins Server. Unpack the archive to a location of your choice.

### patching dctool.(bat|sh)

The batch file/shell script needs to be adapted to use the environment variable _JDK_PROPERTY_NAME_ to build other build variants than `default`.

On Unix systems please verify the encoding/line endings of the modified shell scripts. These should not contain DOS line endings. The interpreter to execute the shell scripts won't be found otherwise.


#### NetWeaver 7.0.x

The call to the Java VM (on Windows) should look like this:


```
call "%JAVA_HOME%\bin\java" -classpath "%startup%" -Xmx256m -Xss20m
  -Ddctool.jarrootdir="%NWDITOOLLIB%"
  -Ddctool.JDK_PROPERTY_NAME="%JDK_PROPERTY_NAME%" %PARAM_JDK% %APPL% %*
```
On Unix the VM should be called like this:


```
"$JAVA_HOME/bin/java" -classpath "$startup" -Xmx256m -Xss20m\
  -Ddctool.jarrootdir="$NWDITOOLLIB"\
  -Ddctool.JDK_PROPERTY_NAME="$JDK_PROPERTY_NAME" $PARAM_JDK $APPL $*
```

#### NetWeaver 7.1+

The call to the Java VM (on Windows) should look like this:


```
call "%JAVA_HOME%\bin\java" -classpath "%startup%" -Xmx256m -Xss20m
  -Dappl.jars="%NWDITOOLLIB%"
  -Dappl.classname=com.sap.tc.cetool.DcConsoleApplication
  -Ddctool.JDK_PROPERTY_NAME=%JDK_PROPERTY_NAME% %PARAM_JDK% %APPL% %*
```
On a Unix system the shell script should call the Java VM like this:


```
"$JAVA_HOME/bin/java" -cp "$startup" -Xmx256m -Xss20m\
  -Dappl.jars="$NWDITOOLLIB"\
  -Dappl.classname=com.sap.tc.cetool.DcConsoleApplication\
  -Ddctool.JDK_PROPERTY_NAME="$JDK_PROPERTY_NAME" $PARAM_JDK $APPL $*
```

## Global configuration


## Creating and configuring a new NWDI-Project


## Jenkins plugins based on the NWDI-Core-Plugin

<table>
<tr><td><a href="https://github.com/weigo/NWDI-Checkstyle-Plugin">NWDI-Checkstyle-Plugin</a></td>
<td>This plugins runs<a href="http://checkstyle.sourceforge.net/">Checkstyle</a>on development components containing Java source code.The results of this analysis can be visualized using the<a href="https://wiki.jenkins-ci.org/display/JENKINS/Checkstyle+Plugin">Jenkins Checkstyle plugin</a>.</td>
</tr>

<tr><td><a href="https://github.com/weigo/NWDI-Cobertura-Plugin">NWDI-Cobertura-Plugin</a></td>
<td>This plugin enables the execution of JUnit test cases for development components. The test coverage will be recorded using<a href="http://cobertura.sourceforge.net">Cobertura</a>.</td>
</tr>

<tr><td><a href="https://github.com/weigo/NWDI-DC-Documenter-Plugin">NWDI-DC-Documenter-Plugin</a></td>
<td>The plugin generates an overview of a track, its software components and development components and publishes this information to a confluence wiki. The generated information contains dependencies, usage of a development component (inside the track). Depending on the type of development component other information is determined from the DCs meta data and content (i.e. licenses of external libraries) and visualized accordingly.</td>
</tr>

<tr><td><a href="https://github.com/weigo/NWDI-JavaDoc-Plugin">NWDI-JavaDoc-Plugin</a></td>
<td>This plugin generates JavaDoc documentation from Java sources contained in development components.If requested the generated documentation can be enriched using UML class diagrams generated using<a href="http://www.umlgraph.org">UmlGraph</a>. This feature requires the installation of<a href="http://www.graphviz.org">GraphViz</a>.</td>
</tr>

<tr><td><a href="https://github.com/weigo/NWDI-PMD-Plugin">NWDI-PMD-Plugin</a></td>
<td>This plugin uses the copy and paste detector (CPD) of<a href="http://pmd.sourceforge.net">PMD</a>to detect duplicated code in development components.The results of this analysis can be visualized using the<a href="https://wiki.jenkins-ci.org/display/JENKINS/PMD+Plugin">Jenkins PMD plugin</a>.</td>
</tr>
</table>New plugins (e.g. FindBugs integration) using the provided infrastructure can easily be created using the plugins mentioned above as an example.