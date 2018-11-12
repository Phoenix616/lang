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

public class BukkitLanguageConfig extends LanguageConfig {
    private final Plugin plugin;
    private FileConfiguration config;
    private FileConfiguration defaultConfig;

    public BukkitLanguageConfig(Plugin plugin, String folder, String locale) {
        this(plugin, folder, folder.isEmpty() ? plugin.getDataFolder() : new File(plugin.getDataFolder(), folder), locale);
    }

    public BukkitLanguageConfig(Plugin plugin, String resourceFolder, File folder, String locale) {
        super(resourceFolder, folder, locale);
        this.plugin = plugin;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found in " + plugin.getName() + "'s jar file!");
                return false;
            }
            defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
            if (!configFile.exists()) {
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
            plugin.getLogger().log(Level.SEVERE, "Could not load defaut config from " + resourcePath, ex);
        }
        return false;
    }

    @Override
    public void setDefaults(LanguageConfig defaults) {
        if (defaults == null) {
            defaultConfig = null;
        } else if (defaults instanceof BukkitLanguageConfig) {
            defaultConfig = ((BukkitLanguageConfig) defaults).config;
        }
    }

    @Override
    public boolean contains(String key) {
        return config.contains(key, true);
    }

    @Override
    public String get(String key) {
        String string = null;
        if (config.isString(key)) {
            string = config.getString(key, defaultConfig != null ? defaultConfig.getString(key) : null);
        } else if (config.isList(key)) {
            List<String> stringList = config.getStringList(key);
            if (stringList == null) {
                stringList = defaultConfig.getStringList(key);
            }
            if (stringList != null) {
                string = String.join("\n", stringList);
            }
        }
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
