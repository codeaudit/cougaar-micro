                    A Simple CougaarME Example

This example illustrates how two plugins within a node can communicate by
publishing and subscribing to objects. 

There are two plugins in this example:

ManagerPlugin: publishes Job objects and subscribes to Status objects.

WorkerPlugin: subscribes to Job objects and publishes Status objects.

These two plugins pass objects back and forth through the blackboard forever or
until a maximum limit is reached. The maximum is set as a parameter in the
XML configuration file for the ManagerPlugin.

Run the example like this:

C:\> KvmNode Tutorial.xml

You should see output like this:


C:\> c:\dev\j2me_cldc\kvm\vmWin\build\kvm_g -classpath
c:\dev\j2me_cldc\bin\common\api\classes;..\..\data\cougaarme.jar;. org.cougaar.microedition.node.Node Tutorial.xml
I am java null or CLDC-1.0, running on null
Reading config from local file: Tutorial.xml
My description: Example CougaarME Node
Listening on 7000
WorkerPlugin got a new Job: Work1
ManagerPlugin got a status: Done1
WorkerPlugin got a new Job: Work2
ManagerPlugin got a status: Done2
WorkerPlugin got a new Job: Work3
ManagerPlugin got a status: Done3
WorkerPlugin got a new Job: Work4
ManagerPlugin got a status: Done4
WorkerPlugin got a new Job: Work5
ManagerPlugin got a status: Done5
WorkerPlugin got a new Job: Work6
ManagerPlugin got a status: Done6
WorkerPlugin got a new Job: Work7
ManagerPlugin got a status: Done7
WorkerPlugin got a new Job: Work8
ManagerPlugin got a status: Done8
WorkerPlugin got a new Job: Work9
ManagerPlugin got a status: Done9
WorkerPlugin got a new Job: Work10
ManagerPlugin got a status: Done10
WorkerPlugin got a new Job: Work11
ManagerPlugin got a status: Done11
WorkerPlugin got a new Job: Work12
ManagerPlugin got a status: Done12
...


You can also use a CougaarSE agent to serve the configuration file over the
network.  To do that, first copy the cougaarmedomain.jar file from the 
data/ directory into the lib/ directory under your COUGAAR_INSTAL_PATH.
Then run the CougaarSE node like this:

    C:\> Node MotherNode

Then run the CougaarME node(s) like this:

    C:\> KvmNode Tutorial 127.0.0.1 1235

Use the mother node's IP address and port number instead of (127.0.0.1) and
(1235).  1235 is the default port, so unless you changed it, that should work.



