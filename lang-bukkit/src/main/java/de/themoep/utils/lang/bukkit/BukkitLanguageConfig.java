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
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

public class BukkitLanguageConfig extends LanguageConfig<FileConfiguration> {
    private final Plugin plugin;

    public BukkitLanguageConfig(Plugin plugin, String resourceFolder, File configFile, String locale) {
        this(plugin, resourceFolder, configFile, locale, true);
    }

    public BukkitLanguageConfig(Plugin plugin, String resourceFolder, File configFile, String locale, boolean saveFile) {
        super(resourceFolder, configFile, locale, saveFile);
        this.plugin = plugin;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        if (configFile != null && configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
            if (defaultConfig != null) {
                config.setDefaults(defaultConfig);
            }
        }
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found in " + plugin.getName() + "'s jar file!");
                return false;
            }
            defaultConfig = config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
            if (saveFile && !configFile.exists()) {
                File parent = configFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    defaultConfig.save(configFile);
                    return true;
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile.getName() + " to " + configFile, ex);
                }
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load default config from " + resourcePath, ex);
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return contains(key, false);
    }

    @Override
    public boolean contains(String key, boolean checkDefault) {
        return config.contains(key, !checkDefault);
    }

    @Override
    public String get(String key) {
        String string = null;
        if (config.isString(key)) {
            string = config.getString(key);
        } else if (config.isList(key)) {
            List<String> stringList = config.getStringList(key);
            if (stringList != null) {
                string = String.join("\n", stringList);
            }
        }
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Override
    public FileConfiguration getRawConfig() {
        return config;
    }
}
