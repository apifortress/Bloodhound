# AFtheM

## Preamble

API Fortress - The Mediator

AFtheM is an Open Source, Scala / Akka based, asynchronous API Micro-Gateway.

The purpose of this project is to provide a highly modular gateway to help developers, QAs and data scientists to:

* Capture
* Measure
* Transform
* Filter
* Simulate

...API Calls.

The processing engine can also be fine tuned to the extreme to have it perform the way you need.


## Building

just use Maven to package the software. One big fat JAR will be created.

## Runtime setup
It's very easy really.
directory structure should look as follows:

```aidl
etc/
afthem.jar
bin/
```

**Note:** we provide various *etc* configurations to test, named `etc.whatever`. Copy (or `ln -s`) that directory
as `etc` and you're set to go.

## Run!

simply invoke `bin/start.sh` from the root directory of the runtime setup.