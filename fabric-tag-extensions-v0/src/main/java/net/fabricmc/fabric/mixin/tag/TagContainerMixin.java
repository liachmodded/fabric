package net.fabricmc.fabric.mixin.tag;

import net.fabricmc.fabric.impl.tag.DisjointSet;
import net.fabricmc.fabric.impl.tag.FabricTagBuilderInternal;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(TagContainer.class)
public abstract class TagContainerMixin<T> {

	@Unique
	private Map<Identifier, Identifier> remaps;
	private DisjointSet<Identifier> disjointSet;

	@Shadow
	// @Nullable
	public abstract Tag<T> get(Identifier identifier_1);

	public void onInit(CallbackInfo ci) {
		remaps = new HashMap<>();
		disjointSet = new DisjointSet<>();
	}

	@ModifyArg(method = "get(Lnet/minecraft/util/Identifier;)Lnet/minecraft/tag/Tag;",
		at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object fabric$modifyGetId(Object arg) {
		return remap((Identifier) arg);
	}

	@ModifyArg(method = "getOrCreate(Lnet/minecraft/util/Identifier;)Lnet/minecraft/tag/Tag;",
		at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object fabric$modifyGetOrCreateId(Object arg) {
		return remap((Identifier) arg);
	}

	@Redirect(method = "applyReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/tag/Tag$Builder;build(Lnet/minecraft/util/Identifier;)Lnet/minecraft/tag/Tag;"))
	public Tag<T> onApplyReload(Tag.Builder<T> builder, Identifier id) {
		for (Identifier sibling : ((FabricTagBuilderInternal) builder).getMerges()) {
			disjointSet.merge(id, sibling);
		}
		return builder.build(id);
	}

	@Unique
	private Identifier remap(Identifier old) {
		final Identifier group = disjointSet.get(old);
		return remaps.getOrDefault(group, group);
	}
}
