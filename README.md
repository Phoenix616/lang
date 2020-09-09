# lang
Language config framework for Bukkit, Bungee, Velocity and standalone Java programs.

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
See this examples for [Bukkit](https://github.com/Phoenix616/lang/wiki/Bukkit-Example), 
[Bungee](https://github.com/Phoenix616/lang/wiki/Bungee-Example) and 
[Velocity](https://github.com/Phoenix616/lang/wiki/Velocity-Example) 
plugins as well [standalone applications](https://github.com/Phoenix616/lang/wiki/Standalone-Example).

Of course you can implement `lang-core` yourself on additional platforms. PRs welcome!

Take a look at the examples in the [wiki](https://github.com/Phoenix616/lang/wiki) and the [Javadocs](https://docs.phoenix616.dev/lang/) for more fine-grained control.
(Almost everything is configurable!)

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
