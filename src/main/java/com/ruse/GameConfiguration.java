package com.ruse;

/**
 * Defines runtime configuration.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class GameConfiguration {

    /**
     * Gets the default game configuration.
     * @return the default configuration.
     */
    public static GameConfiguration getDefault() {
        GameConfiguration configuration = new GameConfiguration();
        configuration.port = 13377;
        configuration.discordBotEnabled = false;
        configuration.encryptPasswords = false;
        configuration.debug = false;
        return configuration;
    }

    private int port;
    private boolean discordBotEnabled;
    private boolean encryptPasswords;
    private boolean debug;

    private GameConfiguration() { }

    public int getPort() {
        return port;
    }

    public boolean isDiscordBotEnabled() {
        return discordBotEnabled;
    }

    public boolean isEncryptPasswords() {
        return encryptPasswords;
    }

    public boolean isDebug() {
        return debug;
    }
}
