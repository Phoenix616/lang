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

import de.themoep.utils.lang.LangLogger;
import de.themoep.utils.lang.LanguageManagerCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.logging.Level;

public class LanguageManager extends LanguageManagerCore<CommandSender, Configuration> {
    private final Plugin plugin;

    public LanguageManager(Plugin plugin, String defaultLocale, BungeeLanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String defaultLocale, boolean saveFiles, BungeeLanguageConfig... configs) {
        this(plugin, "languages", "languages", defaultLocale, saveFiles, configs);
    }

    public LanguageManager(Plugin plugin, String folder, String defaultLocale, BungeeLanguageConfig... configs) {
        this(plugin, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String resourceFolder, String folder, String defaultLocale, BungeeLanguageConfig... configs) {
        this(plugin, resourceFolder, folder, defaultLocale, true, configs);
    }

    public LanguageManager(Plugin plugin, String resourceFolder, String folder, String defaultLocale, boolean saveFiles, BungeeLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(plugin.getDataFolder(), folder), sender -> {
            if (sender instanceof ProxiedPlayer && ((ProxiedPlayer) sender).getLocale() != null) {
                return ((ProxiedPlayer) sender).getLocale().getLanguage().replace('-', '_');
            }
            return null;
        }, "lang.", ".yml", saveFiles, configs);
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(plugin.getClass(), new LangLogger() {
            @Override
            public void log(Level level, String message) {
                plugin.getLogger().log(level, message);
            }

            @Override
            public void log(Level level, String message, Throwable e) {
                plugin.getLogger().log(level, message, e);
            }
        }, locale -> new BungeeLanguageConfig(plugin, getResourceFolder(),
                new File(getFolder(), filePrefix + locale + fileSuffix), locale, saveFiles));
    }
}
