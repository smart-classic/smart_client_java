#SMART Java Client Library
Nate Finstein, Children's Hospital Informatics Program

## Obtaining the code, building a sample app

To get started with the SMART Java Client Library, you'll need

    git
    java >= 1.6.0_20
    ant >= 1.7.0

Obtain the code, build the client library from the SMART ontology,
and automatically build a sample servlet:

    git clone https://github.com/chb/smart_client_java.git
    cd smart_client_java/bin

    sh ./build.sh

    ( On windows do:             )
    (    move build.sh build.bat )
    (    build.bat               )

If all goes according to plan, you should be able to run the
`RxReminder` sample servlet now:

  1. Copy `build/smartapp.war` to `<tomcat-directory>/webapps`
  2. Start up tomcat (on port 8000)
  3. Follow instructions at http://dev.smartplatforms.org

---
## Building your own app

When you're ready to build your own app, you'll find the required
dependencies in:

    build/SmartClient.jar
    lib/*.jar

The sample servlet is a good place to start:
    src/org/smartplatforms/client/tests/Reminder.java

---
## Specifying a SMART ontology file

By default, the client library is built against the current
cloud-hosted SMART ontology file at:

    http://sandbox-api.smartplatforms.org/ontology.

This is probably what you want!  But if you'd like to point to a
different location, just change the `ontology_location` parameter in
`bin/build.sh` to point to any URL or local file.
(e.g. -Dontology_location="/home/me/smart.owl")
