---
title: Basic Dependency Injection using Jersey's HK2
description: Helping application with resource binding in Athena
sidebar_position: 16
---

Jersey uses [HK2](https://qubitpi.github.io/glassfish-hk2/) as its dependency injection (DI) system. We can use other
injection systems, but its infrastructure is built with HK2, and allows us to also use it within our applications.

Setting up simple dependency injection with Jersey takes just a few lines of code. Let say for example we have a service
we would like to inject into our resources.

```java
public class GreetingService {

    public String getGreeting(String name) {
        return "Hello " + name + "!";
    }
}
```

And we want to inject this service into a Jersey resource

```java
@Path("greeting")
public class GreetingResource {

    @Inject
    public GreetingService greetingService;

    @GET
    public String get(@QueryParam("name") String name) {
        return this.greetingService.getGreeting(name);
    }
}
```

In order for the injection to work, all we need is a simple configuration

```java
@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        register(GreetingResource.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(GreetingService.class);
            }
        });
    }
}
```

Here we are saying that we want to bind the `GreetingService` to the injection system, and advertise it as injectable by
the same class. What the last statement means is that we can only inject it as `GreetingService` and (probably
obviously) not by any other class. As you will see later, it is possible to change this.

> Note: The injection above is field injection, where the service is injected into the field of the resource. Another
> type of injection is constructor injection, where the service is injected into the constructor. Athena uses
> constructor injection ubiquitously. An example of the constructor injection is shown below:
>
> ```java
> private final GreetingService greetingService;
>
> @Inject
> public GreetingResource(GreetingService greetingService) {
> this.greetingService = greetingService;
> }
> ```
>
> Athena chooses constructor injection as opposed to field injection, as it _makes the resource easier to unit test_.
> Constructor injection doesn't require any different configuration.

Lets say that instead of a class, the `GreetingService` is an interface, and we have an implementation of it (which is
very common). To configure that, we would use the following syntax in the above `configure` method

```java
@Override
protected void configure() {
    bind(NiceGreetingService.class).to(GreetingService.class);
}
```

This reads as "bind `NiceGreetingService`, and advertise it as `GreetingService`". This means we can use the exact same
code in the `GreetingResource` above, because we advertise the contract as `GreetingService` and not
`NiceGreetingService`. But the actual implementation, when injected, will be the `NiceGreetingService`.

If you've ever worked with any injection framework, you will have came across the concept of **scope**, which determines
the lifespan of the service. You may have heard of a "Request Scope", where the service is alive only for the life of the
request. Or a "Singleton Scope", where there is only one instance of the service. We can configure these scopes also
using the following syntax.

```java
@Override
protected void configure() {
    bind(NiceGreetingService.class)
            .to(GreetingService.class)
            .in(RequestScoped.class);
}
```

The default scope is **`PerLookup`**, which means that every time this service is requested, a new one will be created.
In the example above, using the **`RequestScoped`**, a new service will be created for a single request. This may or may
not be the same as the `PerLookup`, depending on how many places we are trying to inject it. We may be trying to inject
it into a filter and into a resource. If this were `PerLookup`, then two instances would be created for each request. In
this case, we only want one.

The other two scopes available are **`Singleton`** (only one instance created) and **`Immediate`** (like Singleton) but is
created on startup (whereas with `Singleton`, it's not created until the first request).

Aside from binding classes, we could also just use an instance. This would gives us a _default singleton_, so we don't
need to use the `in` syntax.

```java
@Override
protected void configure() {
    bind(new NiceGreetingService())
            .to(GreetingService.class);
}
```

What if we have some complex creation logic or need some request context information for the service. In this case there
are Factorys. For example

```java
public class GreetingServiceFactory implements Factory<GreetingService> {

    @Context
    UriInfo uriInfo;

    @Override
    public GreetingService provide() {
        return new GreetingService(
                uriInfo.getQueryParameters().getFirst("name"));
    }

    @Override
    public void dispose(GreetingService service) {
        /* noop */
    }
}
```

Here we have a factory, that gets request information from the `UriInfo`, in this case a query parameters, and we create
the `GreetingService` from it. To configure it, we use the following syntax

```java
@Override
protected void configure() {
    bindFactory(GreetingServiceFactory.class)
            .to(GreetingService.class)
            .in(RequestScoped.class);
}
```
