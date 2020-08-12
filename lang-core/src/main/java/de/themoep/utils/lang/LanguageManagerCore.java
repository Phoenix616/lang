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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core language manager
 * @param <S> The command sender type
 * @param <C> The configuration type
 */
public abstract class LanguageManagerCore<S, C> {
    private final String resourceFolder;
    private final File folder;
    protected final String filePrefix;
    protected final String fileSuffix;
    protected final boolean saveFiles;
    private String defaultLocale;
    private LanguageConfig<C> defaultConfig = null;

    private LanguageProvider<S> provider;

    private Map<String, LanguageConfig<C>> languages = new LinkedHashMap<>();
    private String placeholderPrefix = "%";
    private String placeholderSuffix = "%";

    protected LanguageManagerCore(String defaultLocale, String resourceFolder, File folder, LanguageProvider<S> provider, String filePrefix, String fileSuffix, boolean saveFiles, LanguageConfig<C>... configs) {
        this.defaultLocale = defaultLocale;
        this.filePrefix = filePrefix;
        this.fileSuffix = fileSuffix;
        if (resourceFolder == null || resourceFolder.isEmpty()) {
            resourceFolder = "languages";
        }
        this.resourceFolder = resourceFolder;
        this.folder = folder;
        this.provider = provider;
        this.saveFiles = saveFiles;
        for (LanguageConfig config : configs) {
            addConfig(config);
        }
        setDefaultLocale(defaultLocale);
    }

    public abstract void loadConfigs();

    protected void loadConfigs(Class<?> pluginClass, Logger logger, Function<String, LanguageConfig<C>> configCreator) {
        try {
            URL url = pluginClass.getResource("/" + resourceFolder);
            if (url != null) {
                URI uri = url.toURI();
                try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()) : null)) {
                    loadInTree(Paths.get(uri), logger, configCreator);
                }
                if (getConfigs().isEmpty()) {
                    logger.log(Level.WARNING, "No language files found in folder '/" + resourceFolder + "' inside the jar!");
                }
            } else {
                logger.log(Level.WARNING, "Could not find folder '/" + resourceFolder + "' in jar!");
            }
        } catch (URISyntaxException | IOException e) {
            logger.log(Level.WARNING, "Failed to automatically load languages from the jar!", e);
        }

        // Load all files in plugin data folder that aren't already loaded
        if (saveFiles) {
            loadInTree(folder.toPath(), logger, locale -> {
                if (!languages.containsKey(locale.toLowerCase(Locale.ENGLISH))) {
                    return configCreator.apply(locale);
                }
                return null;
            });
        }
    }

    private void loadInTree(Path path, Logger logger, Function<String, LanguageConfig<C>> configCreator) {
        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();
                    if (fileName.startsWith(filePrefix) && fileName.endsWith(fileSuffix)) {
                        String locale = fileName.substring(filePrefix.length(), fileName.length() - fileSuffix.length());
                        LanguageConfig<C> config = configCreator.apply(locale);
                        if (config != null) {
                            addConfig(config);
                            logger.log(Level.INFO, "Found locale " + locale + "!");
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to automatically load languages from " + path + "!", e);
        }
    }

    /**
     * Add a new language config for a specific locale
     * @param config    The language config that holds all messages for the specified locale
     * @return The previous language config if it existed or <code>null</code> if not
     */
    public LanguageConfig<C> addConfig(LanguageConfig<C> config) {
        config.setPlaceholderPrefix(placeholderPrefix);
        config.setPlaceholderSuffix(placeholderSuffix);
        return languages.put(config.getLocale().toLowerCase(Locale.ENGLISH), config);
    }

    /**
     * Get a language config for a specific locale
     * @param locale    The string representation of the locale to add the config for
     * @return  The language config that holds all messages for the specified locale.
     *          If no config is defined for that locale it will return the default locale.
     */
    public LanguageConfig<C> getConfig(String locale) {
        if (locale == null) {
            return getDefaultConfig();
        }
        locale = locale.toLowerCase(Locale.ENGLISH);
        LanguageConfig config = languages.get(locale);
        if (config == null && locale.contains("_")) {
            config = languages.get(locale.split("_")[0]);
            if (config == null) {
                config = languages.get(locale.split("_")[1]);
            }
        }
        if (config == null) {
            for (LanguageConfig c : getConfigs()) {
                if (locale.startsWith(c.getLocale()) || locale.endsWith(c.getLocale())) {
                    config = c;
                    break;
                }
            }
        }
        return config != null ? config : getDefaultConfig();
    }

    /**
     * Get a language config for a sender object using the specified provider
     * @param sender    The sender to get the language config for
     * @return  The language config that holds all messages for the locale specified by the provider.
     *          If no config is defined for that locale it will return the default locale.
     */
    public LanguageConfig<C> getConfig(S sender) {
        return getConfig(provider.getLanguage(sender));
    }

    /**
     * Get all defined language configs
     * @return A collection of defined language configs
     */
    public Collection<LanguageConfig<C>> getConfigs() {
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
     * If set to null it will use the first define language config.
     * @param locale The default locale string
     */
    public void setDefaultLocale(String locale) {
        defaultLocale = locale;
    }

    /**
     * Get the prefix of placeholders for replacements
     * @return The placeholder prefix
     */
    public String getPlaceholderPrefix() {
        return placeholderPrefix;
    }

    /**
     * Set the prefix of placeholders for replacements
     * @param placeholderPrefix The placeholder prefix
     */
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
        languages.values().forEach(c -> c.setPlaceholderPrefix(placeholderPrefix));
        defaultConfig = null;
    }

    /**
     * Get the suffix of placeholders for replacements
     * @return The placeholder suffix
     */
    public String getPlaceholderSuffix() {
        return placeholderSuffix;
    }

    /**
     * Set the suffix of placeholders for replacements
     * @param placeholderSuffix The placeholder suffix
     */
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
        languages.values().forEach(c -> c.setPlaceholderSuffix(placeholderSuffix));
        defaultConfig = null;
    }

    /**
     * Get the default language config
     * @return The default language config. If none was defined it will return the first found language. If none is found then it returns null.
     */
    public LanguageConfig<C> getDefaultConfig() {
        if (defaultConfig != null && defaultLocale != null && defaultLocale.equals(defaultConfig.getLocale())) {
            return defaultConfig;
        }
        if (defaultLocale == null) {
            defaultConfig = languages.isEmpty() ? null : languages.values().iterator().next();
        } else {
            defaultConfig = languages.get(defaultLocale.toLowerCase(Locale.ENGLISH));
        }
        return defaultConfig;
    }

    /**
     * Get the path to the folder that contains the default language configs inside of the jar
     * @return The path to the folder, should never be null or empty!
     */
    public String getResourceFolder() {
        return resourceFolder;
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
    public void setProvider(LanguageProvider<S> provider) throws IllegalArgumentException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null!");
        }
        this.provider = provider;
    }

    /**
     * Get the specified language provider
     * @return The specified provider or null if not set
     */
    public LanguageProvider<S> getProvider() {
        return provider;
    }

}
