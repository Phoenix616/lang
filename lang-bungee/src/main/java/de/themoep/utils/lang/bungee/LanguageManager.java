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

import de.themoep.utils.lang.LanguageManagerCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;

public class LanguageManager extends LanguageManagerCore<CommandSender, Configuration> {
    private final Plugin plugin;

    public LanguageManager(Plugin plugin, String defaultLocale, BungeeLanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String folder, String defaultLocale, BungeeLanguageConfig... configs) {
        this(plugin, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(Plugin plugin, String resourceFolder, String folder, String defaultLocale, BungeeLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(plugin.getDataFolder(), folder), sender -> {
            if (sender instanceof ProxiedPlayer) {
                return ((ProxiedPlayer) sender).getLocale().getLanguage().replace('-', '_');
            }
            return null;
        }, "lang.", ".yml", configs);
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(plugin.getClass(), plugin.getLogger(), locale -> new BungeeLanguageConfig(plugin, getResourceFolder(),
                new File(getFolder(), filePrefix + locale + fileSuffix), locale));
    }
}
