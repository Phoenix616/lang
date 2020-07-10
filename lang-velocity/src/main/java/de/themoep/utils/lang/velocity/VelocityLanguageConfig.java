package de.themoep.utils.lang.velocity;

/*
 * lang - lang-velocity
 * Copyright (c) 2020 Max Lee aka Phoenix616 (mail@moep.tv)
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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import de.themoep.utils.lang.LanguageConfig;
import net.kyori.text.format.TextColor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class VelocityLanguageConfig extends LanguageConfig<Config> {

    private final Languaged plugin;

    public VelocityLanguageConfig(Languaged plugin, String resourceFolder, File configFile, String locale) {
        this(plugin, resourceFolder, configFile, locale, true);
    }

    public VelocityLanguageConfig(Languaged plugin, String resourceFolder, File configFile, String locale, boolean saveFile) {
        super(resourceFolder, configFile, locale, saveFile);
        this.plugin = plugin;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        if (configFile != null && configFile.exists()) {
            config = ConfigFactory.parseFile(configFile)
                    .withFallback(ConfigFactory.load(defaultConfig));
        }
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = plugin.getResourceAsStream(resourcePath);) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found in " + plugin.getName() + "!");
                return false;
            }
            defaultConfig = config = ConfigFactory.load(resourcePath);
            if (saveFile && !configFile.exists()) {
                File parent = configFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    Files.copy(in, configFile.toPath());
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
        return config.hasPath(key) || (checkDefault && defaultConfig != null && defaultConfig.hasPath(key));
    }

    @Override
    public String get(String key) {
        ConfigValue o = config.getValue(key);
        String string = null;
        if (o.valueType() == ConfigValueType.STRING) {
            string = (String) o.unwrapped();
        } else if (o.valueType() == ConfigValueType.LIST) {
            List<String> stringList = (List<String>) o.unwrapped();
            if (stringList != null) {
                string = String.join("\n", stringList);
            }
        }
        if (string == null) {
            return TextColor.RED + "Missing language key " + TextColor.YELLOW + key + TextColor.RED + " for locale " + TextColor.YELLOW + getLocale();
        }
        return string;
    }

    @Override
    public Config getRawConfig() {
        return config;
    }
}
