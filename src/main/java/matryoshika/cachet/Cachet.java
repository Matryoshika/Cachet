package matryoshika.cachet;

import org.apache.logging.log4j.Logger;

import com.jarhax.prestige.data.GlobalPrestigeData;
import com.jarhax.prestige.data.PlayerData;

import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Cachet.MODID, name = Cachet.NAME, version = Cachet.VERSION, dependencies = "required-after:bookshelf;required-after:prestige", serverSideOnly = true, acceptableRemoteVersions = "*")
public class Cachet {
	public static final String MODID = "cachet";
	public static final String NAME = "Cachet";
	public static final String VERSION = "1.0.1";

	private static Logger logger;
	private static String NBT_TAG = "cachet:tick_timer";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
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
		}
		else {
			setNBT(player, Properties.config.configuration.counter * 1);
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