# Java-Chat-App

A very basic terminal chat app where users can connect to a server and send messages which are then transmitted to every other user connected to the server at that time.

## Overview

- There is no gui. You can type messages in the terminal and recieve messages in the terminal.
![image](https://github.com/user-attachments/assets/e518e140-0701-4f26-b4c9-292f2a258114)

![image](https://github.com/user-attachments/assets/7a11f0d5-a04c-4a11-bdb0-edcbb250e0d6)

![image](https://github.com/user-attachments/assets/738418c2-5950-4a03-8c76-d01cf8bc7bf1)


- The chats persist for as long as the server runs
- There is no reconnection logic; if you disconnect you log in as a new user automatically.
- The client side code does attempt to reestablish connection to the server in case of disconnection
- Ctrl + C is used to exit the app on either client side or the server side.

## Setup
- The MyServer.java file needs the socketutils pakcage and the Message.java file to run
- The Client file needs the them both as well.
- In the client code change the serverAddress to a static ip address or localhost if you are running both server and client code on the same device

## Issues
- There are a lot of issues but the main concern is adding a gui for a better experience
- The reconnection logic on the client side is pretty fragile
- The id system, to me, seems too wasteful
- There should be a better way to connect to the server
-  Commands to shutdown the client side without needing Ctrl + C
-  The client currently shuts down without attempting reconnection if the server closes during selection of username.

