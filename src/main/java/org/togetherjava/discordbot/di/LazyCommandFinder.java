package org.togetherjava.discordbot.di;

import de.ialistannen.commandprocrastination.autodiscovery.DiscoveryRootCommand;
import de.ialistannen.commandprocrastination.command.tree.CommandFinder;
import de.ialistannen.commandprocrastination.command.tree.CommandNode;
import de.ialistannen.commandprocrastination.util.StringReader;
import java.util.function.Supplier;
import org.togetherjava.discordbot.commands.CommandContext;

/**
 * A command finder that only looks up commands when it is first used.
 *
 * This delays a circular dependency from Commands to the CommandFinder to give guice a chance.
 *
 * @param <C> the type of the command context
 */
public class LazyCommandFinder<C extends CommandContext> extends CommandFinder<C> {

  private final Supplier<CommandFinder<C>> supplier;
  private CommandFinder<C> realized;

  /* `realized` is being checked to be `null` in the function `getRealized()` which further fetches
   * it, if it is `null`, hence according to the logic of program it need not be initialized in
   * the constructor
   * */
  @SuppressWarnings("initialization.fields.uninitialized")
  public LazyCommandFinder(Supplier<CommandFinder<C>> supplier) {
    super(new DiscoveryRootCommand<>());
    this.supplier = supplier;
  }

  @Override
  public FindResult<C> find(StringReader reader) {
    return getRealized().find(reader);
  }

  @Override
  public FindResult<C> find(CommandNode<C> root, StringReader reader) {
    return getRealized().find(reader);
  }

  private synchronized CommandFinder<C> getRealized() {
    if (realized == null) {
      realized = supplier.get();
    }
    return realized;
  }
}
