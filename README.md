# Project Overview

## Introduction

This project is a Java-based application that utilizes the Maven build system. It is designed to demonstrate the use of Remote Procedure Call (RPC) in a client-server architecture. The project is divided into two main parts: the server and the client.

## Server

The server part of the application is responsible for providing services. It contains a `ServiceProvider` class that manages the services provided by the server. The `ServiceProvider` class has methods to add a service, provide a service interface, and retrieve a service implementation for a given interface name.

## Client

The client part of the application is responsible for making RPC calls to the server. It contains a `RPCClientProxy` class that acts as a proxy for the client to make RPC calls. The `RPCClientProxy` class has methods to invoke a method on the server and get a proxy for a service.

The client also contains a `RestClient` class that is a utility class for sending HTTP requests to a specified URL. The `RestClient` class has a method to send a request to a specified URL and path.

## Testing

The project also contains test classes for the `RPCClientProxy` and `RestClient` classes. These test classes use the JUnit 5 and Mockito frameworks for unit testing.

## Technologies Used

- **Java**: The main programming language used in this project.
- **Maven**: A build automation tool used primarily for Java projects.
- **JUnit 5**: A unit testing framework for Java.
- **Mockito**: A mocking framework for unit tests in Java.
- **Spring**: A framework that provides comprehensive infrastructure support for developing Java applications.

## Conclusion

This project demonstrates the use of RPC in a client-server architecture using Java and Maven. It provides a good starting point for understanding how to implement and test RPC calls in Java.