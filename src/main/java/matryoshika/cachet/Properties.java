package matryoshika.cachet;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Cachet.MODID, name = Cachet.NAME)
public class Properties {


	@Name("config")
	public static ConfigOptions config = new ConfigOptions();
	
	public static class ConfigOptions {
	
		public Configuration configuration = new Configuration();
	
		public static class Configuration {
			@Name("Timer")
			@Comment("A String (in HH:MM:SS format) that is used to determine the playtime needed for a player to recieve Prestige Points")
			public String timer = "01:00:00";
			
			
			@Name("Ticking Time")
			@Comment("How often Cachet should go through the player-list and add ticks to each player's data, depending on their playtime. \nAdds x tick-counts to each player every x ticks; The higher the value it is, the less often it happens, and thus less overhead")
			public int counter = 20;
			
			
			@Name("Advancement List")
			@Comment("Resourcenames of Advancements (One Per Line) that adds 1 Prestige point when points are given.\nFormat is (usually) modid:tab/name")
			public String[] advancementList = new String[] {};
			
			@Name("Notification")
			@Comment("The message that will be sent to the player when they recieve a prestige point.\nSupports chat-formatting using '§'. \n'%s' can be used as substitute for amount of points")
			public String notice = "§6§l[Cachet]§r§aYou have recieved §r§c%s §r§aPrestige points!";
			
		}
	}


	@Mod.EventBusSubscriber(modid = Cachet.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void EventOnConfigChanged(OnConfigChangedEvent event) {
			if (event.getModID().equals(Cachet.MODID)) {
				ConfigManager.sync(Cachet.MODID, Type.INSTANCE);
			}
		}
	}
}
