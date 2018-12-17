package de.themoep.utils.lang.simple;

/*
 * lang - lang-simple
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class SimpleLanguageConfig extends LanguageConfig<Properties> {
    private final Languaged languaged;

    public SimpleLanguageConfig(Languaged program, String folder, String locale) {
        this(program, folder, folder.isEmpty() ? program.getDataFolder() : new File(program.getDataFolder(), folder), locale);
    }

    public SimpleLanguageConfig(Languaged program, String resourceFolder, File folder, String locale) {
        super(resourceFolder, folder, locale);
        this.languaged = program;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        try {
            config = new Properties();
            config.load(new FileReader(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = languaged.getResourceAsStream(resourcePath)) {
            if (in == null) {
                languaged.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found for " + languaged.getName() + "!");
                return false;
            }
            defaultConfig = config = new Properties();
            defaultConfig.load(in);
            if (!configFile.exists()) {
                File parent = configFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    config.store(new FileWriter(configFile), languaged.getName() + " - " + getClass().getSimpleName() + " " + getLocale());
                    return true;
                } catch (IOException ex) {
                    languaged.getLogger().log(Level.SEVERE, "Could not save " + configFile.getName() + " to " + configFile, ex);
                }
            }
        } catch (IOException ex) {
            languaged.getLogger().log(Level.SEVERE, "Could not load default config from " + resourcePath, ex);
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return config.contains(key);
    }

    @Override
    public String get(String key) {
        Object o = config.get(key);
        String string = null;
        if (o instanceof String) {
            string = config.getProperty(key, defaultConfig != null ? defaultConfig.getProperty(key) : null);
        }
        if (string == null) {
            return "Missing language key " + key + " for locale " + getLocale();
        }
        return string;
    }

    @Override
    public Properties getRawConfig() {
        return config;
    }
}
