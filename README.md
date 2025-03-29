# Chat App

A Java Swing-Based Chat Application developed as part of the Advanced Programming course at the University of Greenwich.

## Overview

ChatApp is a desktop chat application built using Java Swing that allows users to communicate in real-time. The application provides a user-friendly interface for text-based conversations with features commonly found in modern chat applications.

## System Workflow

   ```mermaid
   flowchart TD
       %% User
       U[User]
   
       %% Client Side
       subgraph "Client Side"
         C[Client]
       end
   
       %% Server Side
       subgraph "Server Side"
         S[Server]
       end
   
       %% GUI View Components
       subgraph "GUI View Components"
           CD[Chat Display]
           CL[Chat List]
           AUL[Active Users List]
           MF[Message Field]
           BTN[Buttons]
       end
   
       %% GUI Components
       subgraph "GUI Components"
           CTR[Controller]
           V[View]
           M[Model]
       end
   
       %% Event Listeners
       subgraph "Event Listeners"
           L1[Window Listener]
           L2[Private Chat Button Listener]
           L3[Group Chat Button Listener]
           L4[Send Button Listener]
           L5[Chat List Listener]
           L6[Get Details Button Listener]
           L7[Message Field Action Listener]
       end
   
       %% Message Types
       subgraph "Message Types"
           TM[Serialized Text Message]
           UM[Serialized User Update Message]
           SM[Serialized System Message]
       end
   
       V --> CD
       V --> CL
       V --> AUL
       V --> MF
       V --> BTN
   
       %% User Interactions
       U --> L1 --> CTR
       U --> L2 --> CTR
       U --> L3 --> CTR
       U --> L4 --> CTR
       U --> L5 --> CTR
       U --> L6 --> CTR
       U --> L7 --> CTR
   
       %% MVC Interactions
       CTR <--> M
       CTR <--> V
       CTR <--> C
   
       %% Server-Client Communication
       C <--> TM <--> S
       C <--> UM <--> S
       C <--> SM <--> S
   ```

## Features

- Real-time messaging
- Private and group chat
- Chat history and message persistence
- User-friendly graphical interface built with Swing

## Technologies

- **Java**: Core programming language
- **Swing**: GUI framework for creating the user interface
- **Maven**: For dependency management and build automation
- **Gson**: For JSON serialization/deserialization
- **Socket Programming**: For network communication
- **Multithreading**: For handling concurrent connections
- **JUnit 5 & Mockito**: For unit testing

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 23 or higher
- Maven 3.6+ (for dependency management and building)

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/UoG-Advanced-Programming/ChatApp.git
    ```
2. Navigate to the project directory:
    ```bash
    cd ChatApp
    ```
3. Build the application with Maven:
    ```bash
   mvn clean install
   ```
4. Compile the application:
    ```bash
    javac -d bin src/*.java
    ```
5. Run the application:
    ```bash
    java -cp bin ChatApp
    ```

## Dependencies

The project uses the following main dependencies:

- **Gson (2.12.1):** For JSON processing
- **Gson-extras (0.2.2):** Additional Gson functionality
- **JUnit 5 (5.9.2):** For unit testing
- **Mockito (5.3.1):** For mocking in tests

All dependencies are managed through Maven and specified in the pom.xml file.

## Usage

- Launch the application
- Create a new account or log in with existing credentials
- Add contacts to start chatting
- Select a contact and start sending messages
- Enjoy real-time communication!

## Project Structure

- `src/main/java/` - Source code files
- `src/test/java/` - Test source files

## License

This project is licensed under the MIT License - see the LICENSE file for details.
