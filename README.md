# Java-Chat-App

A very basic terminal chat app where users can connect to a server and send messages which are then transmitted to every other user connected to the server at that time.

## Overview

- There is no gui. You can type messages in the terminal and recieve messages in the terminal. 
- The chats persist for as long as the server runs
- There is no reconnection logic; if you disconnect you log in as a new user automatically.
- The client side code does attempt to reestablish connection to the server in case of disconnection
- Ctrl + C is used to exit the app on either client side or the server side.

