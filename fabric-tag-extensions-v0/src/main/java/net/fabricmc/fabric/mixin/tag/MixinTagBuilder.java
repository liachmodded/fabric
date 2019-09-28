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

package net.fabricmc.fabric.mixin.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.tag.FabricTagBuilder;
import net.fabricmc.fabric.impl.tag.FabricTagBuilderInternal;
import net.fabricmc.fabric.impl.tag.FabricTagHooks;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder<T> implements FabricTagBuilder<T>, FabricTagBuilderInternal {
	@Shadow @Final
	private Set<Tag.Entry<T>> entries;
	@Unique
	private Set<Identifier> merges;
	@Unique
	private int fabric_clearCount;
	
	@Inject(method = "<init>()V", at = @At("RETURN"))
	public void onConstructor(CallbackInfo ci) {
		merges = new HashSet<>();
	}

	@Inject(at = @At("RETURN"), method = "build")
	public void onBuildFinished(Identifier id, CallbackInfoReturnable<Tag<T>> info) {
		((FabricTagHooks) info.getReturnValue()).fabric_setClearCount(fabric_clearCount);
	}

	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"), method = "fromJson")
	public void onFromJsonClear(Function<Identifier, T> function_1, JsonObject jsonObject_1, CallbackInfoReturnable<Tag.Builder<T>> info) {
		fabric_clearCount++;
	}
	
	@Inject(method = "fromJson", at = @At("HEAD"))
	public void onLoadFromJson(Function<Identifier, T> objectGetter, JsonObject json, CallbackInfoReturnable<Tag.Builder<T>> info) {
		if (JsonHelper.hasArray(json, "fabric:merge")) {
			for (JsonElement each : JsonHelper.getArray(json, "fabric:merge")) {
				merges.add(new Identifier(JsonHelper.asString(each, "the array element")));
			}
		}
		if (JsonHelper.hasString(json, "fabric:merge")) {
			merges.add(new Identifier(JsonHelper.getString(json, "fabric:merge")));
		}
	}

	@Override
	public void clearTagEntries() {
		entries.clear();
		fabric_clearCount++;
	}

	@Override
	public Set<Identifier> getMerges() {
		return merges;
	}
}
