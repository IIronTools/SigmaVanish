package io.github.iirontools.sigmaVanish.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.iirontools.sigmaVanish.SigmaVanish;
import io.github.iirontools.sigmalib.message.ComponentUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class VanishCommand {

    private static final String PERMISSION = "dzialki.admin.vanish";
    private static final String PERMISSION_OTHERS = "dzialki.admin.vanish.others";

    public static LiteralCommandNode<CommandSourceStack> registerCommand(SigmaVanish plugin) {
        return createCommand("vanish", plugin).build();
    }

    public static LiteralCommandNode<CommandSourceStack> registerVanishCommandAlias(SigmaVanish plugin) {
        return createCommand("v", plugin).build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createCommand(String name, SigmaVanish plugin) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(name);

        command.requires(source -> source.getSender().hasPermission(PERMISSION));

        command.executes(context -> {
            CommandSourceStack source = context.getSource();

            if (!(source.getSender() instanceof Player player)) {
                source.getSender().sendMessage(ComponentUtils.consoleUsageWarning());
                return Command.SINGLE_SUCCESS;
            }

            boolean vanished = plugin.getVanishManager().isVanished(player);
            if (!vanished) {
                plugin.getVanishManager().vanish(player);
                player.sendMessage(ComponentUtils.successWithPrefix(
                        Component.text("Vanish został ", NamedTextColor.GRAY)
                                .append(Component.text("włączony", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.GRAY))
                ));
            } else {
                plugin.getVanishManager().unvanish(player);
                player.sendMessage(ComponentUtils.errorWithPrefix(
                        Component.text("Vanish został ", NamedTextColor.GRAY)
                                .append(Component.text("wyłączony", NamedTextColor.RED))
                                .append(Component.text(".", NamedTextColor.GRAY))
                ));
            }

            return Command.SINGLE_SUCCESS;
        });

        command.then(Commands.argument("target", ArgumentTypes.player())
                .requires(source -> source.getSender().hasPermission(PERMISSION_OTHERS))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
                    Player target = targetResolver.resolve(source).getFirst();

                    if (!plugin.getVanishManager().isVanished(target)) {
                        target.sendMessage(ComponentUtils.successWithPrefix(
                                Component.text("Vanish został ", NamedTextColor.GRAY)
                                        .append(Component.text("włączony", NamedTextColor.GREEN))
                                        .append(Component.text(".", NamedTextColor.GRAY))
                        ));

                        if (!target.equals(source.getSender())) {
                            source.getSender().sendMessage(ComponentUtils.successWithPrefix(
                                    Component.text("Vanish został ", NamedTextColor.GRAY)
                                            .append(Component.text("włączony", NamedTextColor.GREEN))
                                            .append(Component.text(" dla ", NamedTextColor.GRAY))
                                            .append(Component.text(target.getName(), NamedTextColor.GOLD))
                                            .append(Component.text(".", NamedTextColor.GRAY))
                            ));
                        }

                        plugin.getVanishManager().vanish(target);
                    } else {
                        target.sendMessage(ComponentUtils.errorWithPrefix(
                                Component.text("Vanish został ", NamedTextColor.GRAY)
                                        .append(Component.text("wyłączony", NamedTextColor.RED))
                                        .append(Component.text(".", NamedTextColor.GRAY))
                        ));

                        if (!target.equals(source.getSender())) {
                            source.getSender().sendMessage(ComponentUtils.errorWithPrefix(
                                    Component.text("Vanish został ", NamedTextColor.GRAY)
                                            .append(Component.text("wyłączony", NamedTextColor.RED))
                                            .append(Component.text(" dla ", NamedTextColor.GRAY))
                                            .append(Component.text(target.getName(), NamedTextColor.GOLD))
                                            .append(Component.text(".", NamedTextColor.GRAY))
                            ));
                        }
                        plugin.getVanishManager().unvanish(target);
                    }

                    return Command.SINGLE_SUCCESS;
                }));

        return command;
    }
}
