# UDP

Objectives

The goal of this coursework assignment is to write a simple client-server application where
multiple clients connect to a server. The first client to connect will send a looping audio
recording (in chunks) to the server. The server will relay the audio stream to all the
clients who connect after this.
Even though we haven't studied the TCP and UCP protocols directly you should have
enough information, together with appropriate online sources, to tackle this (interesting)
problem. It is more representative of the commercial usage of Java networking than is
RMI, which is usually hidden under other layers.

The problem description
The first client to connect to the server sends audio the audio. Clients that connect to
the server after this will receive the audio and play it back.

Protocol usage
TCP | connection is used for signaling
UDP | connection is used for streaming data


Architecture (slightly different from the docs as the architecture was suggested, not mandated):
* The server class launches a listener thread which can add new clients at any given time.
* When a client connects, the server class launches a new thread through which server and client can communicate via TCP
* The server recieves data via a datagram socket and broadcasts to all clients via a multicast socket.
* As the server recieves data only from one client and sends data only to one multicast socket, the UDP data handling remains
  in the Server class.

Problems:
* Sending and recieving audio has not been implemented due to time constraints.
* Testing was done manually because unit tests - as they were taught - did not work within a client/server model.
  https://moodle.bbk.ac.uk/mod/forumng/discuss.php?d=496
  
Achievements:
* This has some fairly sophisticated error handling:
	- If the server is not available when clients connect, clients will keep trying to connect until the server becomes available
	  rather than crashing.
	- If and invalid server address was entered, the client will point that out.
	- If and unvalid address for the sound file was entered, the client will point that out.
	- The server class stores all client threads in a data structure. Whenever the sender clients disconnects, the server will
	  request another sender client via its related thread rather than crashing.
* Instead of audio data, the sender client can send text via UDP and the server relays that text via UDP to all connected clients.
  In order to demonstrate this functionality, clients send their ID and print statements on both recieving clients/server
  demonstrate that the data was relayed properly.
* Audio playback functionality was implemented. Since sending/recieving audio functionality was not implemented due to time contstraints
  this is implemented with some mock up code. The first part of the AudioThread class shows what would happen in the sender client -
  an input stream is created from the sound file and sent via the network. The second part shows what would happen in the recieving
  client - the data would be extracted from the packet and fed back into an input stream.
  In order to prove functionality, a recieving client will play back an audio file which it has already stored. This was tested
  with wav files.
  
Usage:
* enter 'java Server' to launch the server.
* enter 'java <IP> <SOUNDFILE>' to launch the client, replacing any <> statements with appropriate entries.
*



