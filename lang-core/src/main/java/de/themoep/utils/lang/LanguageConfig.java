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
import java.util.Map;

public abstract class LanguageConfig<C> {
    public static final String FILE_PREFIX = "lang.";
    public static final String FILE_SUFFIX = ".yml";

    private final String locale;
    protected final String resourcePath;
    protected final File configFile;

    private String placeholderPrefix = "%";
    private String placeholderSuffix = "%";

    protected C config;
    protected C defaultConfig;

    protected LanguageConfig(String resourceFolder, File folder, String locale) {
        this.locale = locale;
        this.configFile = new File(folder, FILE_PREFIX + locale + FILE_SUFFIX);
        this.resourcePath = resourceFolder.isEmpty() ? configFile.getName() : (resourceFolder + "/" + configFile.getName());
    }

    /**
     * Load the config from the file system
     */
    public abstract void loadConfig();

    /**
     * Save the default config resource from the jar file to the file system
     * @return Whether or not the file was saved. If it already existed this will return false
     */
    public abstract boolean saveConfigResource();

    /**
     * Check if the config contains a certain key
     * @param key   The key of the string
     * @return <tt>true</tt> if this config (and not the defaults) has this key, <tt>false</tt> if not
     */
    public abstract boolean contains(String key);

    /**
     * Get a string from the config by its key
     * @param key   The key of the string
     * @return The message or an error message if it doesn't exist; never null! (use {@link #contains(String)} to check existance)
     */
    public abstract String get(String key);

    /**
     * Get the raw config object
     * @return The raw config object
     */
    public abstract C getRawConfig();

    /**
     * Get a string from the config by its key
     * @param key           The key of the string
     * @param replacements  What to replace the placeholders with. The n-th index is the placeholder, the n+1-th the value.
     * @return The message with the placeholders replaced
     */
    public String get(String key, String... replacements) {
        return replace(get(key), replacements);
    }

    /**
     * Get a string from the config by its key
     * @param key           The key of the string
     * @param replacements  What to replace the placeholders with
     * @return The message with the placeholders replaced
     */
    public String get(String key, Map<String, String> replacements) {
        String[] replacementArray = new String[replacements.size() * 2];
        int i = 0;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            replacementArray[i] = entry.getKey();
            i++;
            replacementArray[i] = entry.getValue();
            i++;
        }
        return replace(get(key), replacementArray);
    }

    /**
     * Replace placeholders in a string
     * @param string        The string to replace in
     * @param replacements  What to replace the placeholders with. The n-th index is the placeholder, the n+1-th the value.
     * @return The string with all placeholders replaced (using the configured placeholder prefix and suffix)
     */
    private String replace(String string, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i+=2) {
            string = string.replace(placeholderPrefix + replacements[i] + placeholderSuffix, replacements[i+1]);
        }
        return string;
    }

    /**
     * Get the locale that this language config is for
     * @return The string representation of the locale of this config
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Set the default config of this config
     * @param defaults The default or config or null if it should not have one
     */
    public void setDefaults(LanguageConfig<? extends C> defaults) {
        if (defaults == null) {
            defaultConfig = null;
        } else {
            defaultConfig = defaults.getRawConfig();
        }
    }

    /**
     * Set the prefix of placeholders for replacements
     * @param placeholderPrefix The placeholder prefix
     */
    void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    /**
     * Set the suffix of placeholders for replacements
     * @param placeholderSuffix The placeholder suffix
     */
    void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }
}
