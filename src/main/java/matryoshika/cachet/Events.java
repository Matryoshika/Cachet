package matryoshika.cachet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@Mod.EventBusSubscriber
public class Events {
	
	private static int ticks = 0;

	@SubscribeEvent
	public static void ticker(ServerTickEvent event) {
		if(event.phase == TickEvent.Phase.START)
			return;
		
		ticks++;

		if (ticks % Properties.config.configuration.counter == 0) {
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().forEach(player -> Cachet.addTicks(player));
		}
	}
	
	@SubscribeEvent
	public static void persistData(PlayerEvent.Clone event) {
		if(!event.isWasDeath())
			return;
		
		long oldData = Cachet.getNBT((EntityPlayerMP)event.getOriginal());
		Cachet.overwriteNBT((EntityPlayerMP)event.getEntityPlayer(), oldData);
	}

}
