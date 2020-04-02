package org.togetherjava.discordbot.commands.commands;

import de.ialistannen.commandprocrastination.autodiscovery.ActiveCommand;
import de.ialistannen.commandprocrastination.command.tree.CommandNode;
import javax.inject.Inject;
import org.togetherjava.discordbot.commands.CommandContext;

/**
 * A simple ping command.
 */
@ActiveCommand(name = "ping", parentClass = BasePrefixCommand.class)
public class PingCommand extends CommandNode<CommandContext> {

  /* Computation inside of a constructor could be dangerous as the
   * object is still under initialization inside of a constructor, a probable issue in TjBot
   * */
  @SuppressWarnings("method.invocation.invalid")
  @Inject
  public PingCommand() {
    super("ping");

    setCommand(context -> context.getRequestContext().getChannel().sendMessage("Pong!").queue());
  }
}
