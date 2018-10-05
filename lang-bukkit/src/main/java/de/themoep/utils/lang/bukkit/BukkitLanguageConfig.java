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
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class BukkitLanguageConfig extends LanguageConfig {
    private final Plugin plugin;
    private final String resourcePath;
    private FileConfiguration config;

    public BukkitLanguageConfig(Plugin plugin, String folder, String locale) {
        super(new File(plugin.getDataFolder(), folder), locale);
        this.plugin = plugin;
        this.resourcePath = folder.isEmpty() ? configFile.getName() : (folder + "/" + configFile.getName());
        saveConfigResource();
    }

    @Override
    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public boolean saveConfigResource() {
        if (!configFile.exists()) {
            plugin.saveResource(resourcePath, false);
            return true;
        }
        return false;
    }

    @Override
    public void setDefaults(LanguageConfig defaults) {
        if (defaults == null) {
            config.setDefaults(new MemoryConfiguration());
        } else if (defaults instanceof BukkitLanguageConfig) {
            config.setDefaults(((BukkitLanguageConfig) defaults).config);
        }
    }

    @Override
    public boolean contains(String key) {
        return config.contains(key, true);
    }

    @Override
    public String get(String key) {
        String string = config.getString(key);
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
