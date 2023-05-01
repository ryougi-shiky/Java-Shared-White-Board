# Shared White Board - Distributed Users

The objective is to develop a white board that can be shared between multiple users over the network. 

# Basic features:

1. Dealing with concurrency:
   - Regardless of the technology you use, you will have to ensure that access to shared resources is properly handled and that simultaneous actions lead to a reasonable state.
2. Structuring your application and handling the system state:
   - You can have multiple servers that communicate with each other or a single central one that manages all the system state
3. Dealing with networked communication:
   - Decide when/what messages are sent across the network.
   - Use Json to exchange messages that establishes which messages are sent in which situation and the replies that they should generate.
   - If you use RMI, then you need to design your remote interface(s) and servants.
4. Implementing the GUI:
   - The functionality can resemble tools like MS Paint.
   - You can use any tool/API/library you want. e.g.: Java2D drawing package
   - Shapes: at least your white board should support for line, circle, oval, and rectangle.
   - Text inputting– allow user to type text anywhere inside the white board.
   - User should be able choose their favourite colour to draw the above features. At least 16 colours should be available.
5. Database:
   - Use .json file as database for storage.


# Advanced Features:

1. Chat Window (text based): To allow users to communicate with each other by typing a text.
2. A “File” menu with new, open, save, saveAs and close should be provided (only the manager can control this)
3. Allow the manager to kick out a certain peer/user


# Guidelines on Usage/Operation
1. Users must provide a username when joining the whiteboard. There should be a way of uniquely identifying users, either by enforcing unique usernames or automatically generating a unique identifier and associating it with each username.
2. All the users should see the same image of the whiteboard and should have the privilege of doing all the drawing operations.
3. When displaying a whiteboard, the client user interface should show the usernames of other users who are currently editing the same whiteboard.
4. Clients may connect and disconnect at any time. When a new client joins the system, the client should obtain the current state of the whiteboard so that the same objects are always displayed to every active client.
5. Only the manager of the whiteboard should be allowed to create a new whiteboard, open a previously saved one, save the current one, and close the application.
6. Users should be able to work on a drawing together in real time, without appreciable delays between making and observing edits.


# Proposed Startup/Operational Model
1. The first user creates a whiteboard and becomes the whiteboard’s manager
   - java CreateWhiteBoard <serverIPAddress> <serverPort> username
2. Other users can ask to join the whiteboard application any time by inputting server’s IP address and port number
   - java JoinWhiteBoard <serverIPAddress> <serverPort> username
3. A notification will be delivered to the manager if any peer wants to join. The peer can join in only after the manager approves
   - A dialog showing “someone wants to share your whiteboard”.
4. An online peer list should be maintained and displayed
5. All the peers will see the identical image of the whiteboard, as well as have the privilege of doing all the operations.
6. Online peers can choose to leave whenever they want. The manager can kick someone out at any time.
7. When the manager quits, the application will be terminated. All the peers will get a message notifying them.


# Suggestion for implementation
Phase 1 (Whiteboard):
   - As a starting point: Single-user standalone whiteboard (OR) You are most welcome to implement a single user and single server.
   - Task A: Implement a client that allows a user to draw all the expected elements.
   - Task B: Implement a server so that client and server are able to communicate entities created in Task A

Phase 2 (user management skeleton):
   - Allow the manager to create a whiteboard
   - Allow other peers to connect and join in by getting approval from the manager
   - Allow the manager to choose whether a peer can join in. Join in means the peer's name will appear in the user list
   - Allow the joined peer to choose quit
   - Allow the manager to close the application, and all peers get notified
   - Allow the manager to kick out a certain peer/user

Phase 3 (Final):
   - Integrate the whiteboard with the user management skeleton (phases 1 and 2)
   - Design issues:
     - What communication mechanism will be used?
       - Socket, RMI, or any other frameworks of your choice.
     - How to propagate the modification from one peer to other peers?
       - You may need an event-based mechanism
     - How many threads do we need per peer?
       - At least one for drawing, one for messaging


# ClientGUI:
1. ^ Support for line, circle, oval, and rectangle. Basic
2. ^ Text inputting– allow user to type text anywhere inside the white board. Basic
3. ^ At least 16 colours should be available. Basic
4. ^ Type a unique username to join the whiteboard. Basic
5. Show other user's name in the same board. Basic
6. Connect and Disconnect at any time. Basic
7. ^ Drawing together in real time. Basic
8. Chat window. Advanced


# ServerGUI
1. Can create a new whiteboard, open and save whiteboard. Advance
2. ^ Approve for clients to join in. Basic
3. ^ List all connected clients usernames. Basic
4. Close app, all clients get notified. Basic
5. Kick out a client. Advanced