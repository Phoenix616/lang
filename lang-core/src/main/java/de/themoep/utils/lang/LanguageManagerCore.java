package de.themoep.utils.lang;

/*
 * lang - lang-core
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

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LanguageManagerCore<T> {
    private final File folder;
    private String defaultLocale;
    private LanguageConfig defaultConfig = null;

    private LanguageProvider<T> provider;

    private Map<String, LanguageConfig> languages = new ConcurrentHashMap<>();

    public LanguageManagerCore(String defaultLocale, File folder, LanguageProvider<T> provider, LanguageConfig... configs) {
        this.defaultLocale = defaultLocale;
        this.folder = folder;
        this.provider = provider;
        for (LanguageConfig config : configs) {
            languages.put(config.getLocale().toLowerCase(Locale.ENGLISH), config);
        }
        setDefaultLocale(defaultLocale);
    }

    /**
     * Add a new language config for a specific locale
     * @param config    The language config that holds all messages for the specified locale
     * @return The previous language config if it existed or <tt>null</tt> if not
     */
    public LanguageConfig addConfig(LanguageConfig config) {
        config.setDefaults(getDefaultConfig());
        return languages.put(config.getLocale().toLowerCase(Locale.ENGLISH), config);
    }

    /**
     * Get a language config for a specific locale
     * @param locale    The string representation of the locale to add the config for
     * @return  The language config that holds all messages for the specified locale.
     *          If no config is defined for that locale it will return the default locale.
     */
    public LanguageConfig getConfig(String locale) {
        if (locale == null) {
            return getDefaultConfig();
        }
        return languages.getOrDefault(locale.toLowerCase(Locale.ENGLISH), getDefaultConfig());
    }

    /**
     * Get a language config for a sender object using the specified provider
     * @param sender    The sender to get the language config for
     * @return  The language config that holds all messages for the locale specified by the provider.
     *          If no config is defined for that locale it will return the default locale.
     */
    public LanguageConfig getConfig(T sender) {
        return getConfig(provider.getLanguage(sender));
    }

    /**
     * Get all defined language configs
     * @return A collection of defined language configs
     */
    public Collection<LanguageConfig> getConfigs() {
        return languages.values();
    }

    /**
     * Get the default locale to use when no special one was specified or the one requested doesn't exist.
     * Also used as the default config when querying a message by its key.
     * @return The default locale string
     */
    public String getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Set the default locale to use when no special one was specified or the one requested doesn't exist.
     * Also used as the default config when querying a message by its key.
     * @param locale The default locale string
     */
    public void setDefaultLocale(String locale) {
        defaultLocale = locale;
        defaultConfig = languages.get(defaultLocale.toLowerCase(Locale.ENGLISH));
        for (LanguageConfig config : languages.values()) {
            config.setDefaults(getDefaultConfig());
        }
    }

    /**
     * Get the default language config
     * @return The default language config (or null if no default was configured)
     */
    public LanguageConfig getDefaultConfig() {
        if (defaultConfig != null && (defaultLocale == null || defaultLocale.equals(defaultConfig.getLocale()))) {
            return defaultConfig;
        }
        if (defaultLocale == null) {
            defaultConfig = languages.isEmpty() ? null : languages.values().iterator().next();
        } else {
            defaultConfig = languages.get(defaultLocale);
        }
        return defaultConfig;
    }

    /**
     * Get the folder that the language files are in
     * @return The folder that is used to store this manager's config files
     */
    public File getFolder() {
        return folder;
    }

    /**
     * Set the provider for the player's language
     * @param provider The provider
     * @throws IllegalArgumentException when provider is null
     */
    public void setProvider(LanguageProvider<T> provider) throws IllegalArgumentException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null!");
        }
        this.provider = provider;
    }

    /**
     * Get the specified language provider
     * @return The specified provider or null if not set
     */
    public LanguageProvider<T> getProvider() {
        return provider;
    }
}
