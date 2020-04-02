package org.togetherjava.discordbot.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The pojo for the main config file for this bot.
 */
public class TjBotConfig {

  private List<String> prefixes;
  private String botToken;
  private CommandConfig commands;

  @JsonCreator
  public TjBotConfig(List<String> prefixes, String botToken, CommandConfig commands) {
    this.prefixes = Objects.requireNonNull(prefixes, "prefixes can not be null!");
    this.botToken = Objects.requireNonNull(botToken, "botToken can not be null!");
    this.commands = Objects.requireNonNull(commands, "commands can not be null!");
  }

  /**
   * Returns all bot command prefixes.
   *
   * @return the prefixes the bot listens to
   */
  public List<String> getPrefixes() {
    return prefixes;
  }

  /**
   * Returns the configuration section for commands.
   *
   * @return the configuration section for commands
   */
  public CommandConfig getCommands() {
    return commands;
  }

  /**
   * Returns the token and deletes it from the config.
   *
   * @return the bot token
   */
  /* This function is used to reset the access token of the user hence resetting `botToken` to `null`
   * makes sense, but `botToken` can't be made `@Nullable` as at other points in the system
   * it needs to have a value
   * */
  @SuppressWarnings("assignment.type.incompatible")
  public String getAndDeleteBotToken() {
    String token = botToken;
    botToken = null;
    return token;
  }

  /**
   * Loads the config from the given input stream.
   *
   * @param inputStream the input stream
   * @return the loaded config
   * @throws IOException if an error occurs when reading
   */
  public static TjBotConfig loadFromStream(InputStream inputStream) throws IOException {
    try (inputStream) {
      return new ObjectMapper(new YAMLFactory())
          .registerModule(new ParameterNamesModule())
          .readValue(inputStream, TjBotConfig.class);
    }
  }
}
