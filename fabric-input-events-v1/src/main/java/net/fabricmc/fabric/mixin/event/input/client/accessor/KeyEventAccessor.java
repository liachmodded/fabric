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

package net.fabricmc.fabric.mixin.event.input.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.fabric.api.event.client.input.KeyEvent;

@Mixin(KeyEvent.class)
public interface KeyEventAccessor {
	@Accessor
	void setCode(int code);

	@Accessor
	void setScancode(int scancode);

	@Accessor
	void setAction(int action);

	@Accessor
	void setModKeys(int modKeys);

	@Accessor
	void setKey(Key key);
}
