# HiveMind
Multiplayer board game Hive Mind

Created by: Jonathan Cheng
6/11/2020

## How to Play:
Right click on the HiveMind.jar file and press open.
If no server is running or the server you are trying to join is full, the game will close.
Follow on-screen instructions and press the instructions button to see in-game rules.


## How to Change Server:
Get the PUBLIC IP address from whoever will be running the server and paste it into IP.txt. Note that the game
will only try to connect to that IP address while it is in the file. If you want to go back to the default server
(Jonathan's server), simply remove any text from IP.txt.


## Running a Server:
1. Make your private IP static (not required, but necessary if you don't want to repeat these steps every time you
    reconnect to wifi)
2. Find your network IP address and type it into the address bar of any browser
3. Login (usually it's username: "admin" and password: "password", but it could also be your router ID)
4. Go to Port Fowarding
5. (Only necessary some routers) search for your computer's IP address in the menu
6. Add a new port forward under the following settings:
    Application to forward: Custom ports
    Protocol: TCP
    Source Ports: Any
    Destination Ports: 9012
    WAN Connection type: all
    Forward to Port: Same as Incoming Port
    Schedule: Always
7. Add new port forward, and make sure status is active

If you don't know how to perform any of the above steps, just Google that step and there should be clear instructions

Note that each router will have different menus/options for Port Fowarding. For more specific instructions, Google
port fowarding + the company of your router


If there are any questions or comments about the game, please contact joncheng@seas.upenn.edu