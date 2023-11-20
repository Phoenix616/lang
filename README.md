# lang
Language config framework for Bukkit, Sponge, Bungee, Velocity and standalone Java programs.

Has the ability to select the correct locale depending on the user.

## Maven Info

```xml
<repository>
    <id>minebench-repo</id>
    <url>https://repo.minebench.de/</url>
</repository>

<dependency>
    <groupId>de.themoep.utils</groupId>
    <artifactId>lang-[bukkit|bungee|velocity|simple]</artifactId>
    <version>check-for-latest</version>
    <scope>compile</scope>
</dependency>
```

## Usage
See this examples for [Bukkit](https://wiki.phoenix616.dev/library:lang:example:bukkit), [Sponge](https://wiki.phoenix616.dev/library:lang:example:sponge), 
[Bungee](https://wiki.phoenix616.dev/library:lang:example:bungee) and 
[Velocity](https://wiki.phoenix616.dev/library:lang:example:standalone) 
plugins as well [standalone applications](https://wiki.phoenix616.dev/library:lang:example:standalone).

Of course you can implement `lang-core` yourself on additional platforms. PRs welcome!

Take a look at the examples in the [wiki](https://wiki.phoenix616.dev/library:lang:) and the [Javadocs](https://docs.phoenix616.dev/lang/) for more fine-grained control.
(Almost everything is configurable!)

The system property `de.themoep.utils.lang.debug` is provided to enable some debug logging.

## License
This library is licensed under the [GPLv3](https://github.com/Phoenix616/lang/blob/master/LICENSE)!
```
 lang
 Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
