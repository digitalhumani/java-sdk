# DigitalHumani Java SDK

This repository hosts the DigitalHumani Java SDK. It can be used for interacting with the DigitalHumani RaaS (Reforestation-as-a-Service) API.

## Design

This SDK is broken down by feature. Each feature is surfaced to the consumer of the SDK via the `RaaS` class. 

At the time of writing, only the 'Tree' feature is supplied but eventually will be expanded to include 'Enterprise', 'Project' etc. 

There are shared classes and interfaces that all features use in the root of the project folder. These include the `config`, `exceptions` and `interfaces` folders.

One of the main shared interfaces is the `com.digitalhumani.interfaces.HTTPHelper<U, T>`. For each feature, it is intended to be used as a way of providing consistency for making and receiving HTTP requests/responses from the RaaS API itself. 

The generic parameter `<U>` represents the HTTP request made to the API, while the generic parameter `<T>` represents the response. 

Please study the `com.digitalhumani.tree.TreePlanterHTTPHelper` to get a better idea of how this works.

## Installation

The Java SDK for RaaS is hosted in the Central Repository of Open Source Software Repository Hosting (OSSRH). If you are using Maven, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.digitalhumani</groupId>
    <artifactId>digitalhumani-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

For further details on how to setup OSSRH, refer to the [official documentation](https://central.sonatype.org/consume/).

## Preparation

- Create an account on [DigitalHumani,com](https://my.digitalhumani.com/register) and grab your API key from the **Developer** menu item. Also take note of your enterprise Id as you will also need that.
- Review the [DigitalHumani documentation](https://docs.digitalhumani.com) to familiarize yourself with the environments, requests, projects, etc

## Usage

### Configuration

There are two ways to configure the SDK:

**`.properties` file**

Create a `raas.properties` file in your `resources` folder with the the following content:

```
ENTERPRISE_ID = <your unique enterprise Id>
API_KEY = <your unique API key>
ENVIRONMENT = <'sandbox' or 'production'>
```

**supply configuration in code**

You can also pass the configuration details in the constructor of the `RaaS` class:

```java
String apiKey = <your unique API key>;
String enterpriseId = <your unique enterprise Id>;
String environment = <'sandbox' or 'production'>;

RaaS raas = new Raas(apiKey, environment, enterpriseId);
```

If you are unsure what your enterpise Id and API keys are, please visit the Digital Humani Dashboard at: https://my.digitalhumani.com/

### Trees

**Request a single tree**

A single tree planting request can be made with the `plantATree` method:

```java
var future = raas.plantATree(projectId, user).thenAccept(treePlanted -> {
        doSomethingWith(treePlanted);
    });
future.get();
```

**Request multiple trees**

Several trees can be requested to be planted with the `plantSomeTrees` method (for example ten):

```java
var future = raas.plantSomeTrees(projectId, user, 10).thenAccept(treesPlanted -> {
        doSomethingWith(treesPlanted);
    });
future.get();
```

_Please note; at this time it not possible to use the SDK to query for valid Project Ids that are required to make the above calls. It is however trivial to query the Projects endpoint directly, for example with cURL:_

```
curl --location --request GET 'https://api.digitalhumani.com/project' \
--header 'Accept: application/json' \
--header 'X-Api-Key: <your unique API key>'
```

**Get a tree request**

A tree request can be retrieving using it's Id (see `TreesPlanted.getUUId()`) via the `getATreePlanted` method:

```java
var future = raas.getATreePlanted(uuid).thenAccept(treesPlanted -> {
        doSomethingWith(treesPlanted);
    });
future.get();
```

All the 'Tree' methods return a `TreesPlanted` object via the [`CompletableFuture`](https://www.baeldung.com/java-completablefuture) API.


## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/digitalhumani/java-sdk.

## License

This SDK is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).