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

# How to Use the RPC Jar Package

This guide will walk you through the steps to use the RPC jar package in your Spring Boot project. This jar package is designed to facilitate RPC (Remote Procedure Call) between different microservices.

## Step 1: Add the Jar Package to Your Project

First, you need to add the RPC jar package to your project. You can do this by adding a dependency in your `pom.xml` file if you are using Maven, or in your `build.gradle` file if you are using Gradle.

For Maven, add the following lines to your `pom.xml`:

```xml
<dependencies>
    <!-- Other dependencies -->
    <dependency>
        <groupId>com.github</groupId>
        <artifactId>rpc-jar</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

For Gradle, add the following lines to your `build.gradle`:

```groovy
dependencies {
    // Other dependencies
    implementation 'com.github:rpc-jar:1.0.0'
}
```

Please replace `com.github`, `rpc-jar`, and `1.0.0` with the actual groupId, artifactId, and version of the RPC jar package.

## Step 2: Use the @ServiceRegister Annotation on the Server Side

On the server side, you need to add the `@ServiceRegister` annotation to the classes or methods that you want to expose as services. This annotation will automatically register the services when the application starts.

Here is an example:

```java
@ServiceRegister
public class MyService implements IMyService {
    // ...
}
```

In this example, `MyService` is a service implementation that will be automatically registered when the application starts.

## Step 3: Add the Server Interfaces on the Client Side

On the client side, you need to add the interfaces of the services that you want to call. The class name, method name, and parameters of the interfaces should be exactly the same as those on the server side.  You also need to add the @Service annotation to the methods of the interfaces. This annotation is used to specify the URL for the RPC call. The URL of the service is read from the application.yaml file.  Here is an example:
```java
public interface IMyService {
    @Service(url = "${services.service1}")
    void getUserByUserId();

    @Service(url = "${services.service1}")
    void insertUserId();

    @Service(url = "${services.service2}")
    void getUserName();

    @Service(url = "${services.service2}")
    void insertUserName();
}
```

In this example, `IMyService` is an interface that defines the methods of the services that you want to call. The `@Service` annotation specifies the URL for the RPC call.
```yaml

services:
  service1: http://localhost:8080/service1
  service2: http://localhost:8080/service2
  service3: http://localhost:8080/service3
  service4: http://localhost:8080/service4
```
In this example, the URL for the service1 method is http://localhost:8080/service1, and the URL for the service2 method is http://localhost:8080/service2.
## Conclusion

This project demonstrates the use of RPC in a client-server architecture using Java and Maven. It provides a good starting point for understanding how to implement and test RPC calls in Java.