package matryoshika.cachet;

import org.apache.logging.log4j.Logger;

import com.jarhax.prestige.data.GlobalPrestigeData;
import com.jarhax.prestige.data.PlayerData;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@Mod(modid = Cachet.MODID, name = Cachet.NAME, version = Cachet.VERSION, dependencies = "required-after:bookshelf;required-after:prestige", serverSideOnly = true, acceptableRemoteVersions = "*")
public class Cachet {
	public static final String MODID = "cachet";
	public static final String NAME = "Cachet";
	public static final String VERSION = "1.0.4";

	private static Logger logger;
	private static String NBT_TAG = "cachet:tick_timer";
	public static final String CACHET_PERMISSION_SELF = MODID + ".timer.self";
	public static final String CACHET_PERMISSION_ALL = MODID + ".timer.all";
	
	
	protected static final TextComponentString base = new TextComponentString("");
	private static final TextComponentString one = new TextComponentString("Due to how Prestige handles points, you will lose points from ");
	private static final TextComponentString two = new TextComponentString("[Cachet] ");
	private static final TextComponentString three = new TextComponentString("when respeccing!");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		
		one.getStyle().setColor(TextFormatting.RED);
		two.getStyle().setColor(TextFormatting.GOLD);
		three.getStyle().setColor(TextFormatting.RED);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PermissionAPI.registerNode(CACHET_PERMISSION_SELF, DefaultPermissionLevel.ALL, "Allows the player to check their own Cachet time");
		PermissionAPI.registerNode(CACHET_PERMISSION_ALL, DefaultPermissionLevel.OP, "Allows the player to check anyone's Cachet time");
		new CommandCachet().registerSelf();
	}

	public static long getAdvancements(EntityPlayerMP player) {
		long done = 0;

		for (String adv : Properties.config.configuration.advancementList) {
			Advancement aAdv = FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancement(new ResourceLocation(adv));
			if (player.getAdvancements().getProgress(aAdv).isDone())
				done++;

		}
		return done;
	}

	public static void addTicks(EntityPlayerMP player) {
		
		if(getNBT(player) >= getTimer()) {
			
			long adv = getAdvancements(player);
			overwriteNBT(player, 0);
			
			if(adv == 0)
				return;
			
			PlayerData data = GlobalPrestigeData.getPlayerData(player);
			data.addPrestige(adv);
			GlobalPrestigeData.save(player);
			
			player.sendMessage(new TextComponentString(String.format(Properties.config.configuration.notice, adv)));
			
			player.sendMessage(base.createCopy().appendSibling(one).appendSibling(two).appendSibling(three));
		}
		else {
			setNBT(player, Properties.config.configuration.counter);
		}
	}

	public static int getTimer() {
		String[] split = Properties.config.configuration.timer.split(":");
		int hoursToTicks = Integer.valueOf(split[0]) * 72000;
		int minutesToTicks = Integer.valueOf(split[1]) * 1200;
		int secondsToTicks = Integer.valueOf(split[2]) * 20;

		return hoursToTicks + minutesToTicks + secondsToTicks;
	}
	
	public static long getNBT(EntityPlayerMP player) {
		if(!player.getEntityData().hasKey(NBT_TAG))
			player.getEntityData().setLong(NBT_TAG, 0);
		
		return player.getEntityData().getLong(NBT_TAG);
	}
	
	public static void setNBT(EntityPlayerMP player, long ticks) {
		player.getEntityData().setLong(NBT_TAG, getNBT(player) + ticks);
	}
	
	public static void overwriteNBT(EntityPlayerMP player, long ticks) {
		player.getEntityData().setLong(NBT_TAG, ticks);
	}
}
