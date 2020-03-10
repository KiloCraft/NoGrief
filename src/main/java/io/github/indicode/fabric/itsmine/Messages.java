package io.github.indicode.fabric.itsmine;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Messages {
    public static final Text INVALID_CLAIM = new LiteralText("Can not find a claim with that name").formatted(Formatting.RED);

    public static final Text INVALID_SETTING = new LiteralText("Invalid Claim Setting!").formatted(Formatting.RED);

    public static final Text NO_PERMISSION = new LiteralText(ChatColor.translate(Config.msg_no_perm));

    public static final Text INVALID_MESSAGE_EVENT = new LiteralText("Invalid Message Event!");

    public static final Text INVALID_PLAYER = new LiteralText("Can not find a Player with that Name!");

    public static final Text TOO_MANY_SELECTIONS = new LiteralText("Only one selection is allowed!");

    public static final Text[] GET_STARTED = new Text[]{
            header("How to Claim (Basics)")
                    .append(line(1, "Type &6/claim stick&e then Left click with a stick on a block to set the &6first&e corner of your claim"))
                    .append(line(2, "Right click to set the other corner"))
                    .append(line(3, "Type &6/claim create <name> &e to create your claim!"))
                    .append(line(4, "To trust a player in your claim type &6/claim trust <player>"))
                    .append(line(5, "To untrust a player in your claim type &6/claim untrust <player>")),
            header("How to Claim (Settings)")
                    .append(line("Settings allow you to change some properties of your claim").formatted(Formatting.LIGHT_PURPLE))
                    .append(line(1, "To change a setting, type ").append(text("/claim settings <setting> [true | false]").formatted(Formatting.GOLD)))
                    .append(line(2, "To check a setting, type ").append(text("/claim settings <setting>").formatted(Formatting.GOLD)))
                    .append(line(3, "To see a list of settings, type ").append(text("/claim settings").formatted(Formatting.GOLD))),
            header("How to Claim (Player Permissions)")
                    .append(line("You can set different permissions for each trusted player!").formatted(Formatting.LIGHT_PURPLE))
                    .append(line(1, "To set a permission, type ").append(text("/claim permissions <claim> player <player> <permission> [true | false]").formatted(Formatting.GOLD)))
                    .append(line(2, "To check someone's permission, type ").append(text("/claim permissions <claim> player <player> <permission>").formatted(Formatting.GOLD)))
                    .append(line(3, "To see a list of trusted players, type ").append(text("/claim trusted").formatted(Formatting.GOLD))),
            header("How to Claim (Global Permissions)")
                    .append(line("Global Permissions are just like player permissions and as the name suggests they are global").formatted(Formatting.LIGHT_PURPLE))
                    .append(line(1, "To set a global permission, type ").append(text("/claim permissions <claim> global <player> <permission> [true | false]").formatted(Formatting.GOLD)))
                    .append(line(2, "To check someone's permission, type ").append(text("/claim permissions <claim> global <player> <permission>").formatted(Formatting.GOLD)))
                    .append(line(3, "To see a list of trusted players, type ").append(text("/claim trusted").formatted(Formatting.GOLD))),
            header("How to resize claim")
                    .append(line("You can always change the size of your claim if you aren't happy with it!").formatted(Formatting.LIGHT_PURPLE))
                    .append(line(1, "To expand your claim in a direction, type ").append(text("/claim expand <distance>").formatted(Formatting.GOLD)))
                    .append(line(2, "If you want to specify a direction, you can type ").append(text("/claim expand <distance> <direction>").formatted(Formatting.GOLD)))
                    .append(line(3, "To shrink a claim you do the same thing but replace \"expand\" with \"shrink\""))
    };

    public static Text[] HELP = new Text[]{

    };

    private static Text header(String title) {
        return new LiteralText(title + ":").formatted(Formatting.AQUA, Formatting.UNDERLINE).append("\n\n");
    }

    private static Text line(int num, String string) {
        return line(num + ". " + string);
    }

    private static Text line(String string) {
        return new LiteralText(ChatColor.translate(string)).formatted(Formatting.YELLOW).append("\n");
    }

    private static Text text(String text) {
        return new LiteralText(text);
    }
}
