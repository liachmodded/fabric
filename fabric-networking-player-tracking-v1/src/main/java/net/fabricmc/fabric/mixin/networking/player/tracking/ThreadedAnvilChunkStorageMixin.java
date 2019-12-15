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

package net.fabricmc.fabric.mixin.networking.player.tracking;

import java.util.Collection;
import java.util.Collections;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

import net.fabricmc.fabric.impl.networking.player.tracking.EntityTrackerStorageAccessor;
import net.fabricmc.fabric.impl.networking.player.tracking.EntityTrackerStreamAccessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin implements EntityTrackerStorageAccessor {
	@Shadow
	@Final
	private Int2ObjectMap<EntityTrackerStreamAccessor> entityTrackers;

	@Override
	public Collection<ServerPlayerEntity> fabric_getTrackingPlayers(Entity entity) {
		EntityTrackerStreamAccessor accessor = entityTrackers.get(entity.getEntityId());
		return accessor != null ? accessor.fabric_getTrackingPlayers() : Collections.emptySet();
	}
}