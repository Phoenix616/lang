package de.themoep.utils.lang.bungee;

/*
 * lang - lang-bungee
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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class BungeeLanguageConfig extends LanguageConfig {
    protected final static ConfigurationProvider yml = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Plugin plugin;
    private final String resourcePath;
    private Configuration config;
    private Configuration defaultConfig;

    public BungeeLanguageConfig(Plugin plugin, String folder, String locale) {
        this(plugin, folder, folder, locale);
    }

    public BungeeLanguageConfig(Plugin plugin, String resourceFolder, String folder, String locale) {
        super(folder.isEmpty() ? plugin.getDataFolder() : new File(plugin.getDataFolder(), folder), locale);
        this.plugin = plugin;
        this.resourcePath = resourceFolder.isEmpty() ? configFile.getName() : (resourceFolder + "/" + configFile.getName());
        loadFromJar();
        saveConfigResource();
    }

    private void loadFromJar() {
        InputStream in = plugin.getResourceAsStream(resourcePath);
        if (in == null) {
            plugin.getLogger().log(Level.SEVERE, "No resource '" + resourcePath + "' found in " + plugin.getFile().getName() + "!");
            return;
        }
        defaultConfig = config = yml.load(in);
    }

    @Override
    public void loadConfig() {
        try {
            config = yml.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveConfigResource() {
        if (!configFile.exists()) {
            File parent = configFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try {
                yml.save(config, configFile);
                return true;
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile.getName() + " to " + configFile, ex);
            }
        }
        return false;
    }

    @Override
    public void setDefaults(LanguageConfig defaults) {
        if (defaults == null) {
            defaultConfig = null;
        } else if (defaults instanceof BungeeLanguageConfig) {
            defaultConfig = ((BungeeLanguageConfig) defaults).config;
        }
    }

    @Override
    public boolean contains(String key) {
        return config.contains(key);
    }

    @Override
    public String get(String key) {
        String string = config.getString(key, defaultConfig != null ? defaultConfig.getString(key) : null);
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
