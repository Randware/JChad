## JChad 🗿
A free and open-source, cross-platform chatting platform where you can host servers yourself. Written in Java.

## Features
#### This project includes three components:
- The JChad **_server_**
- A **_client_** for connecting to a JChad server
- An **_installer_** which can be used to install the newest server or client software

Each of these components can be either run through a graphical user interface or just on the command-line.
Simply use the `-cl` argument on start.

> [!NOTE]
> Keep in mind that the client GUI is currently still being worked on.

#### The main component, the **_server_**, includes the following features:
- An almost entirely configurable server.
- Full message and communication encryption.
- Password protected servers.
- A whitelist and blacklist feature.
- Multiple chats per server with their own configuration.
- Anonymous chat functionality, where neither the username nor IP address of clients will be stored.
- A kick command, live configuration updating, impressive performance, and more!

## Installation
#### Keep in mind, that JChad is written in Java. That means you need to have a Java runtime installed on your machine.

The JChad installer makes it easy to install the desired components on your machine!
It supports both a graphical and cli installation. 

Simply head to the [releases](https://github.com/Randware/JChad/releases) page to get it. You can also get the other components as standalone .jar files from there, if desired.

To run in GUI mode, simply double click the .jar file. To run it in the terminal, use the `-cl` launch argument:

`java -jar JChad-server.jar -cl`

> [!TIP]
> There is an official docker image for the JChad server available. 
> Visit [our Docker repository](https://hub.docker.com/r/randware/jchad) for step-by-step installation instructions.

## Configuration
#### Configuring your JChad server instance is easy:
The server will automatically generate all config files with sensible default values when you run it for the first time. They are stored in the same directory the .jar file is in. Most values can be modified during runtime.
The server detects changes made in those files, and automatically reloads them. There are some exceptions though,
like the "port" and encryption related settings.


**There are 2 folders:**
- configs
- chats

#### The "configs" folder includes configuration files for the server. 

The "server-settings.yml" file stores general server settings, which you are likely to change.

The "internal-settings.yml" file stores values the server uses internally to operate. 
You don't have to change those, as they are sensible by default, but you can. But be careful,
some values could make the server act in unexpected ways.

Additionally, if you enable the "whitelist" or "blacklist" feature in the "server-settings.yml" file, the server will generate separate "whitelisted-ips.yml" and "blacklisted-ips.yml" files. You can put IP addresses in those, to either whitelist or blacklist them.

#### The "chats" folder stores the chats of a server.

To create a new chat, simply create a folder in this "chats" directory. The server will then load every folder as a seperate chat. This also works when the server is running.

As soon as the server loads a chat from its folder, it will create 2 files inside it:
- The "config.yml" file
- The "messages.json" file

The "config.yml" file stores the configuration for this specific chat. It can also be modified during runtime.

The "messages.json" file stores messages and their related data sent by clients. 
**_It is not recommended to modify this file, unless you know what you are doing!_**

### TODO: Create table with all configuration values

## About JChad
JChad was developed by the Randware organisation and licensed under the [MIT license](https://choosealicense.com/licenses/mit/).

Feel free to contribute to this project with your own code, or by reporting bugs and issues on our GitHub [Issues](https://github.com/Randware/JChad/issues) page.

> [!NOTE]
> This project is very new, so expect some bugs and issues. Make sure to report them to us, so we can fix them.
