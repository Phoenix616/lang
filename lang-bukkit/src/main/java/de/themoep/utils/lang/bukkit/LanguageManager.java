package de.themoep.utils.lang.bukkit;

/*
 * lang - lang-bukkit
 * Copyright (c) 2018 Max Lee aka Phoenix616 (mail@moep.tv)
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

import de.themoep.utils.lang.LanguageConfig;
import de.themoep.utils.lang.LanguageManagerCore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.EnumSet;
import java.util.logging.Level;

public class LanguageManager extends LanguageManagerCore<Player> {

    public LanguageManager(Plugin plugin, String defaultLocale, LanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String folder, String defaultLocale, LanguageConfig... configs) {
        super(defaultLocale, new File(plugin.getDataFolder(), folder), Player::getLocale, configs);
        try {
            URI uri = plugin.getClass().getResource(folder.isEmpty() ? "" : "/" + folder).toURI();
            try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()) : null)) {
                Path myPath = Paths.get(uri);
                Files.walkFileTree(myPath, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String fileName = file.getFileName().toString();
                        if (fileName.startsWith(LanguageConfig.FILE_PREFIX) && fileName.endsWith(LanguageConfig.FILE_SUFFIX)) {
                            String locale = fileName.substring(LanguageConfig.FILE_PREFIX.length(), fileName.length() - LanguageConfig.FILE_SUFFIX.length());
                            addConfig(new BukkitLanguageConfig(plugin, folder, locale));
                            plugin.getLogger().log(Level.INFO, "Found locale " + locale + "!");
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (URISyntaxException | IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to automatically load all available languages! " + e.getMessage());
        }
    }
}
