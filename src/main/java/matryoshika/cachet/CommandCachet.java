package matryoshika.cachet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandCachet extends CommandBase{

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
		EntityPlayerMP wanted = null;
		String ifarg = "";
		if(args.length != 0) {
			ifarg = args[0];
			wanted = server.getPlayerList().getPlayerByUsername(ifarg);
		}
		else {
			if(sender instanceof EntityPlayerMP)
				wanted = (EntityPlayerMP) sender;
			else
				sender.sendMessage(new TextComponentString("Error: Cannot check Cachet time for Server"));
		}
		
		if(wanted == null && !ifarg.equals(""))
			sender.sendMessage(new TextComponentString("Error: Cannot find online player by name:" + ifarg));
			

		long totalseconds = Cachet.getNBT(wanted) / 20; //convert everything into seconds
		long hours = (long) Math.floor(totalseconds / 3600);
		long minutes = (totalseconds % 3600) / 60;
		long seconds = totalseconds % 60;
		String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		
		
		sender.sendMessage(new TextComponentString(time + " / " + Properties.config.configuration.timer));
	}
	
	public void registerSelf() {
		((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(this);
	}

}
