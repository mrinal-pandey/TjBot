package org.togetherjava.discordbot.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A collection of messages, read from a file and maybe translated.
 */
public class Messages {

  private Map<String, Object> map;

  /**
   * Creates a new messages instance.
   */
  /* The constructor is initializing the field `map` but the checker framework says otherwise
   * Also `inputStream`, if becomes `null`, throws an exception which is catched properly by the
   * code hence it is safe
   * */
  @SuppressWarnings({"initialization.fields.uninitialized", "argument.type.incompatible"})
  public Messages() {
    try (InputStream inputStream = getClass().getResourceAsStream("/messages.yml")) {
      map = new ObjectMapper(new YAMLFactory())
          .readValue(inputStream, new TypeReference<Map<String, Object>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Translates a string.
   *
   * @param key the key
   * @param formatArgs the format arguments
   * @return tbe translated string
   * @throws IllegalArgumentException if the key was not found
   */
  public String tr(String key, Object... formatArgs) {
    String storedValue = implGet(key, map);
    if (storedValue == null) {
      throw new IllegalArgumentException("Path not found: '" + key + "'");
    }
    return String.format(storedValue, formatArgs);
  }

  /**
   * Translates a string.
   *
   * @param key the key
   * @param formatArgs the format arguments
   * @return tbe translated string
   */
  public Optional<String> trOptional(String key, Object... formatArgs) {
    String storedValue = implGet(key, map);
    if (storedValue == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(String.format(storedValue, formatArgs));
  }

  /**
   * Checks if the messages has a given key.
   *
   * @param key the key
   * @return true if the kex exists.
   */
  public boolean hasKey(String key) {
    return implGet(key, map) != null;
  }

  private @Nullable String implGet(String path, Map<String, Object> root) {
    String firstPart = path.split("\\.")[0];

    if (!root.containsKey(firstPart)) {
      return null;
    }
    Object fetched = root.get(firstPart);

    if (path.length() == firstPart.length()) {
      return fetched.toString();
    }

    String restPath = path.substring(path.indexOf('.') + 1);

    @SuppressWarnings("unchecked")
    Map<String, Object> nestedMap = (Map<String, Object>) fetched;
    return implGet(restPath, nestedMap);
  }
}
