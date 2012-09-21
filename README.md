# NWDI-Core-Plugin

The NWDI-Core-Plugin is a '[Jenkins plugin](http://jenkins-ci.org)' that provides a means to

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

### Global settings

After a restart of your Jenkins instance configure the global settings of the plugin using the
Jenkins system configuration view.

