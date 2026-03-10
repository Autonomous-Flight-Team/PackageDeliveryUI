When the program is started, the network manager searches for an open serial communication port.
There are two status variables related to an open port - A) Is a port available and B) is there a 
talking device on the other end, in this case a drone. A thread is started on network manager creation
that searches for an open port matching the telemetry port name. Then, if this is true, it sends heartbeat
requests over this port until a connection is established.

Once a open serial port is found, A becomes true, and a heartbeat packet is sent over the port every
second. Once a heartbeat is recieved back, the connection is listed as open, and B becomes true.

When both conditions A and B are fulfilled, the stream is continously reading bytes off until a sync
code is identified. Once this code is identified, the rest of the 5 byte header is read and decoded.
Then, the packet data is handled according to its type. There are seperate functions for each packet 
type. When a packet is received, the sequence number of the new packet is read. It should be 1 greater
than local. If this isn't true, the previous packet(s) should be sent. 

For data to be sent - the packets are encoded into a packet format, and send in byte encoding through
the serial connection. They are sent sequentially, and the local sequence number is incremented up.

When a disconnect is requested, the port is closed.