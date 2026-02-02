package commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class Spawn extends Command {
    public Spawn() {
        super("spawn");
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player)) return;
            sender.sendMessage("Returning to spawn!");
            ((Player) sender).teleport(new Pos(0.0, 42.0, 0.0));
        });
    }
}