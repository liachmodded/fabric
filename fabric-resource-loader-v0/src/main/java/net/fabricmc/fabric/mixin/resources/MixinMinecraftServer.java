/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.resources;

import net.fabricmc.fabric.api.event.resource.PackScannerRegistrationCallback;
import net.fabricmc.fabric.impl.resources.ModResourcePackCreator;
import net.minecraft.resource.DefaultResourcePackCreator;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Shadow
	@Final
	private ResourcePackContainerManager<ResourcePackContainer> dataPackContainerManager;

	@Inject(method = "loadWorldDataPacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackContainerManager;addCreator(Lnet/minecraft/resource/ResourcePackCreator;)V", ordinal = 1))
	public void appendFabricDataPacks(File file, LevelProperties properties, CallbackInfo info) {
		dataPackContainerManager.addCreator(new ModResourcePackCreator(ResourceType.SERVER_DATA));
		PackScannerRegistrationCallback.DATA.invoker().registerTo(dataPackContainerManager);
	}

	@Redirect(method = "reloadDataPacks", at = @At(value = "INVOKE",
		target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V", remap = false),
	slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false),
	to = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;beginReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;")))
	public void replaceForEach(Consumer<? super ResourcePackContainer> consumer) {

	}
}
