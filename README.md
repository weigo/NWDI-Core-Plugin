# NWDI-Core-Plugin

The NWDI-Core-Plugin is a [Jenkins plugin](http://jenkins-ci.org) that provides a means to

* create jobs in Jenkins that let you choose which CBS track (development configuration)
  to build,
* poll the DTR for changes in this track,
* automatically update the development configuration for the selected track from CBS,
* synchronize the selected track from DTR,
* build changed/all development components contained in the track,
* run configured builders on changed/all development components in this track.

## Setup

### Building the plugin

The plugin is not available through the Jenkins plugin center yet. To build the plugin you'll
need to clone the following GitHub repositories:

```
git clone git://github.com/weigo/NWDI-config-plugin.git
git clone git://github.com/weigo/NWDI-pom-Plugin.git
git clone git://github.com/weigo/NWDI-Core-Plugin.git
```

and build the Maven projects:

```
for d in NWDI-config-plugin NWDI-pom-Plugin NWDI-Core-Plugin; do (cd $d; mvn install); done
```

in 'NWDI-Core-Plugin/target' you'll find the 'NWDI-Core-Plugin.hpi' file which you should upload
using the Jenkins plugin center extended settings view.

### NetWeaver DI command line tools

After installation check that the user running Jenkins can access and execute the batch files/shell
scripts just installed.

##### 7.0.x

With NetWeaver 7.0.x the DI command tools are provided together with NetWeaver developer studio. They
are located in the sub folder `tools` beneath your NWDS installation folder. Copy this folder to your
Jenkins server.

##### 7.1+

NetWeaver versions more recent than 7.0.x do not provide the DI command tools with the NetWeaver developer
studio. You'll need to download the SCA `DICLIENTS` from SAP market place (enter `DICLIENTS` as search term).

Unzip the SCA. In the sub folder `DEPLOYARCHIVES` there is the SDA `tc~di~cmd_tools~sda.sda`. Extract the ZIP
archive `di_cmd_tools.zip` and copy it to your Jenkins Server. Unpack the archive to a location of your choice.

###### patching dctool.(bat|sh)

The batch file/shell script needs to be adapted to use the environment variable `JDK_PROPERTY_NAME` to build
other build variants than `default`.

On Unix systems please verify the encoding/line endings of the modified shell scripts. These should not contain
DOS line endings. The interpreter to execute the shell scripts won't be found otherwise.

###### 7.0.x

The call to the Java VM (on Windows) should look like this:

```
call "%JAVA_HOME%\bin\java" -classpath "%startup%" -Xmx256m -Xss20m
  -Ddctool.jarrootdir="%NWDITOOLLIB%"
  -Ddctool.JDK_PROPERTY_NAME="%JDK_PROPERTY_NAME%" %PARAM_JDK% %APPL%  %*
```

On Unix the VM should be called like this:

```
"$JAVA_HOME/bin/java" -classpath "$startup" -Xmx256m -Xss20m
  -Ddctool.jarrootdir="$NWDITOOLLIB"
  -Ddctool.JDK_PROPERTY_NAME="$JDK_PROPERTY_NAME" $PARAM_JDK $APPL  $*
```
  
###### 7.1+

The call to the Java VM (on Windows) should look like this:

```
call "%JAVA_HOME%\bin\java" -classpath "%startup%" -Xmx256m -Xss20m\
  -Dappl.jars="%NWDITOOLLIB%"\
  -Dappl.classname=com.sap.tc.cetool.DcConsoleApplication\
  -Ddctool.JDK_PROPERTY_NAME=%JDK_PROPERTY_NAME% %PARAM_JDK% %APPL%  %*
```

On a Unix system the shell script should call the Java VM like this:

```
"$JAVA_HOME/bin/java" -cp "$startup" -Xmx256m -Xss20m\
  -Dappl.jars="$NWDITOOLLIB"\
  -Dappl.classname=com.sap.tc.cetool.DcConsoleApplication\
  -Ddctool.JDK_PROPERTY_NAME="$JDK_PROPERTY_NAME" $PARAM_JDK $APPL  $*
```

### Global settings

After a restart of your Jenkins instance configure the global settings of the plugin using the
Jenkins system configuration view.

#### NWDI tool library location (7.0.x and 7.1+) 

There are two text fields where the location of your DI command tools should be entered. These tools
are needed for the communication with the CBS.

Enter the absolute path to the respective locations of the DI command tool installation folders.

#### JDK_HOME_PATHS

Enter a list of paths to JKD installations on your Jenkins server. SAP defines several constants that identify
the different JDK versions to use:

* JDK1.3.1_HOME,
* JDK1.4.2_HOME,
* JDK1.5.0_HOME and
* JDK1.6.0_HOME.

Enter a semicolon separated list of key value pairs of JDK installation Jenkins should use to build your tracks.
The list should read like this:

```
JDK1.4.2_HOME=/opt/jdk1.4.2;JDK1.5.0_HOME=/opt/jdk1.5.0_01;JDK1.6.0_HOME=/opt/jdk1.6.0_35
```

#### NWDI-User and password

Enter the credentials of a user to be used to communicate with the NWDI (CBS, DTR).

#### CBS URL

Enter the URL to your NWDI server (a standard installation will use http://\<server\>:50000).