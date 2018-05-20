package zdoctor.skilltree.api.skills.requirements;

import net.minecraft.entity.player.EntityPlayer;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.skills.ISkillRequirment;
import zdoctor.skilltree.skills.SkillBase;

public class PreviousSkillRequirement implements ISkillRequirment {

	private SkillBase skill;

	public PreviousSkillRequirement(SkillBase skill) {
		this.skill = skill;
	}

	@Override
	public boolean test(EntityPlayer player) {
		return SkillTreeApi.hasSkill(player, skill);
	}

	@Override
	public void onFufillment(EntityPlayer player) {
	}

	@Override
	public String getDescription() {
		return "requirement." + ModMain.MODID + ".requireSkill.desc";
	}

	@Override
	public Object[] getDescriptionValues() {
		return new Object[] { skill.getDisplayName() };
	}

}