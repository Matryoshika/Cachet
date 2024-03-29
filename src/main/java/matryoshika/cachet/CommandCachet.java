package matryoshika.cachet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.PermissionAPI;

public class CommandCachet extends CommandBase {
	
	private static final TextComponentString commandErrorServer = new TextComponentString("Error: Cannot check Cachet time for Server");
	private static final TextComponentString commandErrorPerms = new TextComponentString("Error: sender has no relevant permissions, aborting command");
	private static final TextComponentString commandErrorPermsOther = new TextComponentString("Error: You do not have the required permissions to check other's time");
	private static final TextComponentString slash = new TextComponentString("/");
	private static final TextComponentString timer = new TextComponentString(Properties.config.configuration.timer);

	@Override
	public String getName() {
		return "cachet";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Returns time until the next Cachet cycle. usage: /cachet [name] where [name] is optional; Defaults to Command-Sender.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		boolean isPlayer = false;
		EntityPlayerMP wanted = null;

		if (sender instanceof EntityPlayerMP)
			isPlayer = true;

		// Check if second argument was used, if so, check if sender is allowed to check other's time
		if (args.length != 0) {

			//If checking name that isn't self
			if (isPlayer && !args[0].toLowerCase().equals(((EntityPlayerMP) sender).getName().toLowerCase()))
				//if player and doesn't have permission to check other's
				if (isPlayer && !PermissionAPI.hasPermission((EntityPlayerMP) sender, Cachet.CACHET_PERMISSION_ALL)) {
					sender.sendMessage(commandErrorPermsOther);
					return;
				}

			wanted = server.getPlayerList().getPlayerByUsername(args[0]);
		}
		// check if sender is server. If so, make sure they check a player
		else if (!isPlayer && args.length == 0) {
			sender.sendMessage(commandErrorServer);
			return;
		}
		// check if player is allowed to check their own time
		else if (isPlayer && PermissionAPI.hasPermission((EntityPlayerMP) sender, Cachet.CACHET_PERMISSION_SELF))
			wanted = (EntityPlayerMP) sender;
		else {
			sender.sendMessage(commandErrorPerms);
			return;
		}

		long totalseconds = Cachet.getNBT(wanted) / 20; // convert everything into seconds
		long hours = (long) Math.floor(totalseconds / 3600);
		long minutes = (totalseconds % 3600) / 60;
		long seconds = totalseconds % 60;
		String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		
		TextComponentString pTime = new TextComponentString(time);
		pTime.getStyle().setColor(TextFormatting.YELLOW);

		sender.sendMessage(Cachet.base.createCopy().appendSibling(pTime).appendSibling(slash).appendSibling(timer));
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	public void registerSelf() {
		((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(this);
		
		commandErrorServer.getStyle().setColor(TextFormatting.RED);
		commandErrorPerms.getStyle().setColor(TextFormatting.RED);
		commandErrorPermsOther.getStyle().setColor(TextFormatting.RED);
		slash.getStyle().setColor(TextFormatting.WHITE);
		timer.getStyle().setColor(TextFormatting.GREEN);
	}

}
