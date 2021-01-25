package de.themoep.utils.lang.sponge;

/*
 * lang - lang-sponge
 * Copyright (c) 2021 Max Lee aka Phoenix616 (mail@moep.tv)
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
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;

import java.io.File;

public class LanguageManager extends LanguageManagerCore<CommandSource, ConfigurationNode> {
    private final Languaged plugin;

    public LanguageManager(Languaged plugin, String defaultLocale, SpongeLanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(Languaged plugin, String defaultLocale, boolean saveFiles, SpongeLanguageConfig... configs) {
        this(plugin, "languages", "languages", defaultLocale, saveFiles, configs);
    }

    public LanguageManager(Languaged plugin, String folder, String defaultLocale, SpongeLanguageConfig... configs) {
        this(plugin, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(Languaged plugin, String resourceFolder, String folder, String defaultLocale, SpongeLanguageConfig... configs) {
        this(plugin, resourceFolder, folder, defaultLocale, true, configs);
    }

    public LanguageManager(Languaged plugin, String resourceFolder, String folder, String defaultLocale, boolean saveFiles, SpongeLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(plugin.getDataFolder(), folder), sender -> sender.getLocale().getLanguage().replace('-', '_'), "lang.", ".yml", saveFiles, configs);
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(plugin.getClass(), plugin.getLangLogger(), locale -> new SpongeLanguageConfig(plugin, getResourceFolder(),
                new File(getFolder(), filePrefix + locale + fileSuffix), locale, saveFiles));
    }
}
