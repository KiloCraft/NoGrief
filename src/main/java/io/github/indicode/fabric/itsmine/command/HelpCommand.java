package io.github.indicode.fabric.itsmine.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.indicode.fabric.itsmine.ChatColor;
import io.github.indicode.fabric.itsmine.Claim;
import io.github.indicode.fabric.itsmine.Messages;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static io.github.indicode.fabric.itsmine.util.ArgumentUtil.getHelpId;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HelpCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        command.executes((context) -> sendPage(context.getSource(), Messages.GET_STARTED, 1, "Get Started", "/claim help getStarted %page%"));

        LiteralArgumentBuilder<ServerCommandSource> help = literal("help");
        help.executes((context) -> sendPage(context.getSource(), Messages.HELP, 1, "Its Mine!", "/claim help commands %page%"));

        RequiredArgumentBuilder<ServerCommandSource, String> id = getHelpId();
        RequiredArgumentBuilder<ServerCommandSource, Integer> page = argument("page", IntegerArgumentType.integer(1));

        page.executes((context) -> {
            Claim.HelpBook book = Claim.HelpBook.getById(getString(context, "id"));
            if (book == null) {
                context.getSource().sendError(new LiteralText("Invalid Book!"));
                return -1;
            }
            int p = IntegerArgumentType.getInteger(context, "page");

            if (p > book.texts.length) p = 1;
            return sendPage(context.getSource(), book.texts, p, book.title, book.getCommand());
        });

        id.then(page);
        help.then(id);
        command.then(help);
    }

    public static int sendPage(ServerCommandSource source, Text[] text, int page, String title, String command) {
        int prevPage = page - 2;
        int thisPage = page - 1;
        int nextPage = page + 1;
        final String SEPARATOR = "-----------------------------------------------------";
        Text header =  new LiteralText("")
                .append(new LiteralText("- [ ").formatted(Formatting.GRAY))
                .append(new LiteralText(title).formatted(Formatting.GOLD))
                .append(" ] ")
                .append(SEPARATOR.substring(ChatColor.removeAlternateColorCodes('&', title).length() + 4))
                .formatted(Formatting.GRAY);

        Text button_prev = new LiteralText("")
                .append(new LiteralText("<-").formatted(Formatting.WHITE, Formatting.BOLD))
                .append(" ").append(new LiteralText("Prev").formatted(Formatting.GOLD))
                .styled((style) -> {
                    style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText((prevPage >= 0) ? "<<<" : "|<").formatted(Formatting.GRAY)));
                    if (prevPage >= 0)
                        style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command.replace("%page%",  String.valueOf(page - 1))));
                });

        Text button_next = new LiteralText("")
                .append(new LiteralText("Next").formatted(Formatting.GOLD))
                .append(" ").append(new LiteralText("->").formatted(Formatting.WHITE, Formatting.BOLD)).append(" ")
                .styled((style) -> {
                    style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText((nextPage <= text.length) ? ">>>" : ">|").formatted(Formatting.GRAY)));
                    if (nextPage <= text.length)
                        style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command.replace("%page%",  String.valueOf(nextPage))));
                });

        Text buttons = new LiteralText("")
                .append(new LiteralText("[ ").formatted(Formatting.GRAY))
                .append(button_prev)
                .append(" ")
                .append(
                        new LiteralText(String.valueOf(page)).formatted(Formatting.GREEN)
                                .append(new LiteralText("/").formatted(Formatting.GRAY))
                                .append(new LiteralText(String.valueOf(text.length)).formatted(Formatting.GREEN))
                )
                .append(" ")
                .append(button_next)
                .append(new LiteralText("] ").formatted(Formatting.GRAY));

        Text footer = new LiteralText("- ")
                .formatted(Formatting.GRAY)
                .append(buttons).append(new LiteralText(" ------------------------------").formatted(Formatting.GRAY));

        header.append("\n").append(text[thisPage]).append("\n").append(footer);
        source.sendFeedback(header, false);
        return 1;
    }


}