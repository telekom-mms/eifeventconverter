# EIFEventConverter

### What is this?
This program listen on a port (config) for incomming EIF Events. After receiving the programm convert the event into a json and send it out to teh destination
Event Integration Facility (EIF)
https://www.ibm.com/docs/en/iirfz/11.3.0?topic=availability-event-integration-facility-eif

### New Features:
* Response added (Timeout and EOF)

### Removed Feature:

### Requirements:
```
JRE java-11
the endpoint instanaEventAdapter is reachable "http://instana01.test.drv.drv:28080"
```
### How to build or deploy 
In the "build" folder are all needed files

```
 * config/*
 * libs/*
 * EIFEventConverter.jar
 * EIFEventConverter.sh
```

### Install
* install InstanaEventAdapter first
* copy the files in the build folder to your server
* edit the config.properties file if needed
* edit the log4j2.config file if needed
* rename directory "instanaEventAdapter-bin" to "instanaEventAdapter"
* allow the execution of the file EIFEventAdapter.sh
* chmod u+x EIFEventAdapter.sh
* if there is any https connection you need to add the cert to the truststore

### How to Install from binary
* copy the files in the build folder to your server typical 
```
mkdir /opt/EIFEventConverter
```
* allow the execution of the file EIFEventConverter.sh chmod 
```
chmod 755  /opt/EIFEventConverter/EIFEventConverter.sh
```
* edit the file EIFEventConverter.sh
```
EIFEventConverter_HOME should point the the home of the EIFEventConverter /opt/EIFEventConverter
JAVA_EXECUTABLE should point the a java-11 executabel java file
```

* edit the config.properties file if needed (seed config file)
* edit the log4j2.config file if needed

### install as service
* copy the service file EIFEventConverter.service to /etc/systemd/system/
+ edit the file and check the settings, maybe choose the "user"
* execute
```
systemctl daemon-reload
systemctl enable EIFEventConverter.service
```

### config properties
```
http_port=28081
https_port=18081
eifeof=END
charset=UTF-8
keystore_path=config/keystore.jks
keystore_pass=changeit
enable_ssl=false

sendToAdapter=true
connectionTimeout=10000
eventAdapter_protokoll=http
eventAdapter_port=28080
eventAdapter_host=localhost
eventAdapter_endpoint=/direct

eventType=eif
```

### Start EIFEventConverter
```
/opt/EIFEventConverter/EIFEventConverter.sh start
or
service EIFEventConverter start
```
### Stop EIFEventtAdapter
```
/opt/EIFEventConverter/EIFEventConverter.sh stop
or
service EIFEventConverter stop
```
### Logging
StandardLog: There is a Standard Logfile in the log folder
EventLog: additionanlly we log all events in a special file

### License: 
Copyright 2023 Deutsche Telekom MMS GmbH (https://www.t-systems-mms.com/) 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

### Author: 
Kay Koedel, kay.koedel@telekom.de
