# JeDI (Java explicit Dependency-Injection)

Jedi is simple, lightweight, modular and extensible dependency injection tools for java. There is no any annotation and any magic every dependency defined explicitly.
Jedi has small core that is in charge of resolving dependencies throw constructor. all other stuff defined in extensions and are separate from core module.

## How to Use:

<Maven repo goes here>

At core there is DependencyCollection, all dependencies register throw this. for creating new instance of that see below:

<DependencyCollection Creation goes here>

Register dependency like this:

<Singleton and transient dep goes here>

Most important method in DependencyCollection is build. this is in charge of validating registered dependencies, check constructurs, validate available class ant etc.
Call build method creates and returns new instance of DependencyResolver interface:

<build depeCollection goes here>

##### NOTE: Each in Jedi can throws exception that is checked exception and should care of those explicitly. but each method has a XXXUnsafe equvalienc that does not throw checkd exception. instead wrapped exceptions in RuntimeException.

With access to DependencyResolver there is ability to resolve each registered dependency:

<Resolve sample goes here>


### Extension:
To extend Jedi behaviour use Extension abstract class. see <extesnion link here> and <extesnion link here> for example.
You can Use extendWith() method add new extension to DependencyCollection:

<Extension sample goes here>

There are usefull extension :
* <Json Config>
* <Properties Config>
* <Long Running Service>
* <Scheduled Service>

See each module for more details.

## TODO:


