Running the comms test:

Run the CougaarSE node like this:

      Node MotherNode

Run the CougaarME measurement node like this:

      KvmNode Node2 <mothernode ip address> 1235

Run the CougaarME relay nodes like this:

      KvmNode Node1 <mothernode ip address> 1235
      KvmNode Node3 <mothernode ip address> 1235


You should see measure tasks allocated from the MotherNode to Node1 and Node3.  These tasks
then get allocated to Node2 where the measurements are taken.  The allocation results flow back
to the mother node who prints the values.


