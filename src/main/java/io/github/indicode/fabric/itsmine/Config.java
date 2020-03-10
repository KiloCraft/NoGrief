package io.github.indicode.fabric.itsmine;

import io.github.indicode.fabric.tinyconfig.ModConfig;

/**
 * @author Indigo Amann
 */
public class Config {
    public static int baseClaimBlocks3d = 15625;
    public static int baseClaimBlocks2d = 2500;
    public static boolean claims2d = true;
    public static String msg_interact_entity = "Hey! Sorry but you can't interact with Entities here!";
    public static String msg_interact_block = "Hey! Sorry but you can't interact with Blocks here!";
    public static String msg_break_block = "Hey! Sorry but you can't Break Blocks here!";
    public static String msg_place_block = "Hey! Sorry but you can't Place Blocks here!";
    public static String msg_attack_entity = "Hey! Sorry but you can't Attack Entities here!";
    private static ModConfig modConfig = new ModConfig("itsmine");
    static void sync(boolean overwrite) {
        modConfig.configure(overwrite, config -> {
            claims2d = config.getBool("2D claims", claims2d, "Claims extending from y 0 to y 256");
            baseClaimBlocks2d = config.getInt("2D base claim blocks", baseClaimBlocks2d, "Area Filled: " + ItsMine.blocksToAreaString2d(baseClaimBlocks2d));
            baseClaimBlocks3d = config.getInt("3D base claim blocks", baseClaimBlocks3d, "Area Filled: " + ItsMine.blocksToAreaString3d(baseClaimBlocks3d));
            msg_interact_entity = config.getString("msg.interact.entity", msg_interact_entity, "");
        });
    }

}
