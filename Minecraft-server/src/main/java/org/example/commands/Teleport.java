package org.example.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class Teleport extends Command {

    public Teleport() {
        super("tp");
        // tp <player>
        var targetArg = new ArgumentEntity("target").onlyPlayers(true);
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            EntityFinder finder = context.get(targetArg);

            var entities = finder.find(sender);
            var it = entities.iterator();
            if (!it.hasNext()) return;

            Player target = (Player) it.next();
            target.teleport(player.getPosition());
        }, targetArg);

        // tp ~ ~ ~
        var x = new ArgumentDouble("x");
        var y = new ArgumentDouble("y");
        var z = new ArgumentDouble("z");
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;
            player.teleport(new Pos(
                context.get(x),
                context.get(y),
                context.get(z)
            ));
        });
    }

}
