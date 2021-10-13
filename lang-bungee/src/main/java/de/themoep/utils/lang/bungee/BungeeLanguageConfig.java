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
import java.util.List;
import java.util.logging.Level;

public class BungeeLanguageConfig extends LanguageConfig<Configuration> {
    protected final static ConfigurationProvider yml = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Plugin plugin;

    public BungeeLanguageConfig(Plugin plugin, String resourceFolder, File configFile, String locale) {
        this(plugin, resourceFolder, configFile, locale, true);
    }

    public BungeeLanguageConfig(Plugin plugin, String resourceFolder, File configFile, String locale, boolean saveFile) {
        super(resourceFolder, configFile, locale, saveFile);
        this.plugin = plugin;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        if (configFile != null && configFile.exists()) {
            try {
                config = yml.load(configFile, defaultConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = plugin.getResourceAsStream(resourcePath)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found in " + plugin.getFile().getName() + "!");
                return false;
            }
            defaultConfig = config = yml.load(in);
            if (saveFile && !configFile.exists()) {
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
        return config.contains(key) || (checkDefault && defaultConfig != null && defaultConfig.contains(key));
    }

    @Override
    public String get(String key) {
        Object o = config.get(key);
        String string = null;
        if (o instanceof String) {
            string = (String) o;
        } else if (o instanceof List) {
            List<String> stringList = (List<String>) o;
            if (stringList != null) {
                string = String.join("\n", stringList);
            }
        }
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return string;
    }

    @Override
    public Configuration getRawConfig() {
        return config;
    }
}
