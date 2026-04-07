## How the network service works -
A high level overview, as a basic guide for creating another implementation.

### Setting up the Connection
To set up the connection, the serial port is simply searched for, and once available opened in the program.
Now that this port is open, the Listen and Send threads are started.

### Listening on the Port
The listening thread waits for 2 bytes to be available on the port. Once this is true, it reads the two
bytes and checks if they match the sync code. If they do, it calls the packet read function.

The packet read function is simple - it reads the next two bytes, for packet length. After doing this,
it creates a byte array to serve as the packet buffer, using the length of the packet as the size.
It fills in the sync code and packet length that were already read, and then reads the rest of the packet
out of the serial port into the array, at a 4 byte offset (to not overwrite the sync and length bytes).
Finally, we convert the byte array into a packet with the class constructor that uses a byte array.


Now, the packet is read. This isn't all though - first, we need to check the sequence code to make sure it is proper.
If the packet isn't a repeat sequence type (command ack, etc.) we make sure it is one higher than the 
currently stored rxSequence. If it is greater than that, we send a packet drop notice for every number
that was skipped. Note that this needs to be done using wrapping addition - otherwise you could overflow
and send 65k packets all at once! This would be obviously bad. 

Now, we need to handle the packet. This is also simple - just send the packet off to the handle packet function.
This is a simple switch case that covers each possible packet type. A few of the possible functions are below -
- Packet loss notice - check which sequence number is send, and resend that packet if still available in the past
packets array.
- Telemetry update - decode the data in the protobuf and update the data file.
- Health update - decode the data in the protobuf and update the health status vars in the data file.

Note that all of this is pretty simple. If we are sitting on the open port and see a sync code, read the packet.
Otherwise, go on and wait for a packet.
If we see an out of sequence packet, let the other program know. Otherwise, go on with using the packet data.

### Sending on the Port
This is also super simple. There are only a few issues that need to be avoided.
The primary one is trying to send multiple packets at the same time.