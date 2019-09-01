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

import de.themoep.utils.lang.LanguageManagerCore;

import java.io.File;
import java.util.Properties;

public class LanguageManager extends LanguageManagerCore<Languaged.User, Properties> {
    private final Languaged languaged;

    public LanguageManager(Languaged languaged, String defaultLocale, SimpleLanguageConfig... configs) {
        this(languaged, "languages", defaultLocale, configs);
    }

    public LanguageManager(Languaged languaged, String defaultLocale, boolean saveFiles, SimpleLanguageConfig... configs) {
        this(languaged, "languages", "languages", defaultLocale, saveFiles, configs);
    }

    public LanguageManager(Languaged languaged, String folder, String defaultLocale, SimpleLanguageConfig... configs) {
        this(languaged, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(Languaged languaged, String resourceFolder, String folder, String defaultLocale, SimpleLanguageConfig... configs) {
        this(languaged, resourceFolder, folder, defaultLocale, true, configs);
    }

    public LanguageManager(Languaged languaged, String resourceFolder, String folder, String defaultLocale, boolean saveFiles, SimpleLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(languaged.getDataFolder(), folder), sender -> null, "lang.", ".properties", saveFiles, configs);
        this.languaged = languaged;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(languaged.getClass(), languaged.getLogger(), locale ->
                new SimpleLanguageConfig(languaged, getResourceFolder(),
                        new File(getFolder(), filePrefix + locale + fileSuffix), locale, saveFiles));
    }
}
