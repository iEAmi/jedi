# JeDI (Java explicit Dependency-Injection)

Jedi is simple, lightweight, modular and extensible dependency injection tools for java. There is no any annotation and any magic every dependency defined explicitly.
Jedi has small core that is in charge of resolving dependencies throw constructor. all other stuff defined in extensions and are separate from core module. Jedi need java 11 at least.

## How to Use:

<Maven repo goes here>

At core there is ```DependencyCollection```, all dependencies register throw this. for creating new instance of that see below:

```java
final var depCollection = DependencyCollection.newDefault();
```

Register dependency like this:

```java
// Register singleton dependency
depCollection.addSingleton(Repository.class, RepositoryImpl.class);

// Register using builder function
depCollection.addSingleton(Repository.class, dependencyResolver -> {
  // You can use dependencyResolver to resolve required dependencies.
  final var dao = dependencyResolver.resolveRequiredUnsafe(Dao.class);
  
  return new RepositoryImpl(dao);
});

final var repositoryInstance = new RepositoryImpl();
depCollection.addSingleton(Repository.class, repositoryInstance);

// Register transient dependency
depCollection.addTransient(Repository.class, RepositoryImpl.class);

// Register using builder function
depCollection.addTransient(Repository.class, dependencyResolver -> {
  // You can use dependencyResolver to resolve required dependencies.
  final var dao = dependencyResolver.resolveRequiredUnsafe(Dao.class);
  
  return new RepositoryImpl(dao);
});

final var repositoryInstance = new RepositoryImpl();
depCollection.addTransient(Repository.class, repositoryInstance);
```

Most important method in DependencyCollection is ```build()```. this is in charge of validating registered dependencies, check constructurs, validate available class ant etc.
Calling build method creates and returns new instance of ```DependencyResolver``` interface:

```java
final var depResolver = depCollection.build();
```

##### NOTE: Each in Jedi can throws exception that is checked exception and should care of those explicitly. but each method has a XXXUnsafe equvalienc that does not throw checkd exception. instead wrapped exceptions in ```RuntimeException```.

With access to ```DependencyResolver``` there is ability to resolve each registered dependency:

```java
// resovle returns null if given class not registered before.
final var repoInstance = depResolver.resolve(Repository.class); // Contains check exception that should be handled
final var repoInstance = depResolver.resolveUnsafe(Repository.class); // without any checked exception. but can throw runtime exception


// resovleRequired thorws IllegalStateException if given class not registered before.
final var repoInstance = depResolver.resolveRequired(Repository.class); // Contains check exception that should be handled
final var repoInstance = depResolver.resolveRequiredUnsafe(Repository.class); // without any checked exception. but can throw runtime exception


// resovleOptional returns Optional that coudl be empty if given class not registered before.
final var repoInstance = depResolver.resolveOptional(Repository.class); // Contains check exception that should be handled
final var repoInstance = depResolver.resolveOptionalUnsafe(Repository.class); // without any checked exception. but can throw runtime exception
```


### Extension:
To extend Jedi behaviour use ```Extension``` abstract class. see [extension-config-json](extension-config-json) and [extension-long-running](extension-long-running) for example.
You can Use ```extendWith()``` method add new extension to ```DependencyCollection```:

```java
final var objectMapper = new ObjectMapper();
final var jsonConfigExtension = new JsonConfigExtension(depCollection, "appsetting.json", objectMapper);

depCollection.extendWith(jsonConfigExtension, extension -> {
  configRegistry.addConfigUnsafe(DatabaseConfig.class, DatabaseConfig.KEY);
});
```

There are usefull extension :
* [Read json config](extension-config-json) reading json file and bind to POJO classes. supports Jackson annotations
* [Read java properties config](extension-config-properties) reading java properties file and bind to POJO classes.
  supports Jackson annotations
* [Background service](extension-long-running-service) running service in background on dedicate thread.
* [Scheduled Service](extension-scheduled-service) schedule a function to invoked priodically

See each module for more details.

## TODO:
- [ ] Implement health check
- [ ] Check duplicate extension registering
- [ ] Scoped dependency
- [ ] Better documentation 

