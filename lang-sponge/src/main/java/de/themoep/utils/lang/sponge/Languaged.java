package de.themoep.utils.lang.sponge;

/*
 * lang - lang-sponge
 * Copyright (c) 2021 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.themoep.utils.lang.LangLogger;

import java.io.File;
import java.io.InputStream;

public interface Languaged {

    /**
     * Get the folder in which the languaged object stores it's config and data files in
     * @return The folder
     */
    File getDataFolder();

    /**
     * Get the logger of the languaged object
     * @return The logger
     */
    LangLogger getLangLogger();

    /**
     * Get a resource corresponding to this languaged object.
     * Returns {@link ClassLoader#getResourceAsStream(String)} by default.
     * @param path  The path of the resource as a string
     * @return The {@link InputStream} of that resource
     */
    default InputStream getResourceAsStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    /**
     * The name of this languaged object. Returns {@link Class#getSimpleName()} by default
     * @return The name of the languaged object.
     */
    default String getName() {
        return getClass().getSimpleName();
    }

}
