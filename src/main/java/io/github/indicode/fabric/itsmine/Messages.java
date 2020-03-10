package io.github.indicode.fabric.itsmine;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Messages {
    public static final Text INVALID_CLAIM = new LiteralText("Can not find a claim with that name").formatted(Formatting.RED);

    public static final Text NO_PERMISSION = new LiteralText(ChatColor.translate(Config.msg_no_perm));

    public static final Text INVALID_MESSAGE_EVENT = new LiteralText("Invalid Message Event!");

    public static final Text INVALID_PLAYER = new LiteralText("Can not find a Player with that Name!");

}
