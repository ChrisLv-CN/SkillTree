package zdoctor.skilltree.skills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ISkillHandler;

public class CapabilitySkillHandler {

	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(ISkillHandler.class, new Capability.IStorage<ISkillHandler>() {
			@Override
			public NBTBase writeNBT(Capability<ISkillHandler> capability, ISkillHandler instance, EnumFacing side) {
				return new NBTTagCompound();
			}

			@Override
			public void readNBT(Capability<ISkillHandler> capability, ISkillHandler instance, EnumFacing side,
					NBTBase base) {
				// instance.deserializeNBT((NBTTagCompound) base);
			}
		}, SkillHandler::new);

	}

	@SubscribeEvent
	public void initCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (!(e.getObject() instanceof EntityPlayer))
			return;
		e.addCapability(new ResourceLocation(ModMain.MODID, "SkillCapability"), new SkillCapabilityProvider());

	}

	@SubscribeEvent
	public void reloadCap(PlayerEvent.Clone e) {
		ModMain.proxy.config.open();
		ModMain.proxy.config.sync();
		if (e.isWasDeath() && !ModMain.proxy.keepSkillsOnDeath.getValue()) {
			ModMain.proxy.log.debug("KeepSkillsOnDeath is diabled, not sysncing {} skills",
					e.getEntityPlayer().getDisplayName());
			return;
		}

		ISkillHandler capOrginal = e.getOriginal().getCapability(SKILL_CAPABILITY, null);
		e.getEntityPlayer().getCapability(SKILL_CAPABILITY, null).deserializeNBT(capOrginal.serializeNBT());
	}

	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking e) {
		Entity target = e.getTarget();
		if (target instanceof EntityPlayerMP)
			SkillTreeApi.syncServerSkillsAll(e.getEntityPlayer());
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			EntityPlayer player = event.player;
			SkillTreeApi.getSkillHandler(player).tickEvent();
		}
	}

}