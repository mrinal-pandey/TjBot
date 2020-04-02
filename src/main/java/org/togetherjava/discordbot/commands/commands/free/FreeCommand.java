package org.togetherjava.discordbot.commands.commands.free;

import de.ialistannen.commandprocrastination.autodiscovery.ActiveCommand;
import de.ialistannen.commandprocrastination.command.tree.CommandNode;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.togetherjava.discordbot.commands.CommandContext;
import org.togetherjava.discordbot.commands.commands.BasePrefixCommand;
import org.togetherjava.discordbot.config.TjBotConfig;
import org.togetherjava.discordbot.events.free.LastMessageInChannelListener;
import org.togetherjava.discordbot.events.free.LastMessageInChannelListener.LastSentMessage;
import org.togetherjava.discordbot.util.DurationFormatter;

@ActiveCommand(name = "free", parentClass = BasePrefixCommand.class)
public class FreeCommand extends CommandNode<CommandContext> {

  private final TjBotConfig config;
  private final LastMessageInChannelListener lastMessageInChannelListener;
  private final DurationFormatter durationFormatter;

  /* Calling `execute()`, an instance method, inside of a constructor could be dangerous as the
   * object is still under initialization inside of a constructor, a probable issue in TjBot
   * */
  @SuppressWarnings({"methodref.receiver.bound.invalid", "method.invocation.invalid"})
  @Inject
  public FreeCommand(JDA jda, TjBotConfig config) {
    super("free");

    this.lastMessageInChannelListener = new LastMessageInChannelListener(jda);
    this.config = config;
    this.durationFormatter = new DurationFormatter();

    jda.addEventListener(lastMessageInChannelListener);

    setCommand(this::execute);
  }

  private void execute(CommandContext context) {
    List<LastSentMessage> messages = lastMessageInChannelListener.getLastMessages().stream()
        // Only retrieve channels in the same guild as the command was triggered in
        .filter(it -> it.getMessage().getGuild().equals(context.getRequestContext().getGuild()))
        .filter(this::isInHelpChannel)
        .sorted(Comparator.comparing(LastSentMessage::getDurationToNow))
        .collect(Collectors.toList());

    StringJoiner description = new StringJoiner("\n");

    for (LastSentMessage message : messages) {
      description.add(
          "<#" + message.getMessage().getChannel().getId() + ">: "
              + "**" + durationFormatter.format(message.getDurationToNow()) + "**"
      );
    }

    if (!description.toString().isBlank()) {
      Message message = new MessageBuilder()
          .setEmbed(
              new EmbedBuilder()
                  .setTitle("Time since last message")
                  .setDescription(description.toString())
                  .build()
          ).build();

      context.getRequestContext().getChannel().sendMessage(message).queue();
    } else {
      context.getRequestContext().getChannel().sendMessage(
          new MessageBuilder("I do not have enough data yet :(").build()
      ).queue();
    }
  }

  private boolean isInHelpChannel(LastSentMessage it) {
    Pattern helpChannelRegex = config.getCommands().getFree().getHelpChannelRegex();
    String channel = it.getMessage().getChannel().getName();
    return helpChannelRegex.matcher(channel).matches();
  }
}
