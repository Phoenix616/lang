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

import de.themoep.utils.lang.LangLogger;
import de.themoep.utils.lang.LanguageManagerCore;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

public class LanguageManager extends LanguageManagerCore<CommandSender, FileConfiguration> {
    private final Plugin plugin;

    public LanguageManager(Plugin plugin, String defaultLocale, BukkitLanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String defaultLocale, boolean saveFiles, BukkitLanguageConfig... configs) {
        this(plugin, "languages", "languages", defaultLocale, saveFiles, configs);
    }

    public LanguageManager(Plugin plugin, String folder, String defaultLocale, BukkitLanguageConfig... configs) {
        this(plugin, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String resourceFolder, String folder, String defaultLocale, BukkitLanguageConfig... configs) {
        this(plugin, resourceFolder, folder, defaultLocale, true, configs);
    }

    public LanguageManager(Plugin plugin, String resourceFolder, String folder, String defaultLocale, boolean saveFiles, BukkitLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(plugin.getDataFolder(), folder), sender -> {
            if (sender instanceof Player) {
                try {
                    return ((Player) sender).getLocale();
                } catch (NoSuchMethodError ignored) {}
            }
            return null;
        }, "lang.", ".yml", saveFiles, configs);
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(plugin.getClass(), new LangLogger() {
            @Override
            public void log(Level level, String message) {
                plugin.getLogger().log(level, message);
            }

            @Override
            public void log(Level level, String message, Throwable e) {
                plugin.getLogger().log(level, message, e);
            }
        }, locale -> new BukkitLanguageConfig(plugin, getResourceFolder(),
                new File(getFolder(), filePrefix + locale + fileSuffix), locale, saveFiles));
    }
}
