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

package net.fabricmc.fabric.api.event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.impl.base.event.EventFactoryImpl;

/**
 * Helper for creating {@link Event} classes.
 */
public final class EventFactory {
	private static boolean profilingEnabled = true;

	private EventFactory() { }

	/**
	 * @return True if events are supposed to be profiled.
	 */
	public static boolean isProfilingEnabled() {
		return profilingEnabled;
	}

	/**
	 * Invalidate and re-create all existing "invoker" instances across
	 * events created by this EventFactory. Use this if, for instance,
	 * the profilingEnabled field changes.
	 */
	// TODO: Turn this into an event?
	public static void invalidate() {
		EventFactoryImpl.invalidate();
	}

	/**
	 * Create an "array-backed" Event instance.
	 *
	 * @param type           The listener class type.
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param <T>            The listener type.
	 * @return The Event instance.
	 */
	public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		return EventFactoryImpl.createArrayBacked(type, invokerFactory);
	}

	/**
	 * Create an "array-backed" Event instance with a custom empty invoker.
	 *
	 * <p>Having a custom empty invoker (of type (...) -&gt; {}) increases performance
	 * relative to iterating over an empty array; however, it only really matters
	 * if the event is executed thousands of times a second.
	 *
	 * @param type           The listener class type.
	 * @param emptyInvoker   The custom empty invoker.
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param <T>            The listener type.
	 * @return The Event instance.
	 */
	// TODO: Deprecate this once we have working codegen
	public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		return EventFactoryImpl.createArrayBacked(type, emptyInvoker, invokerFactory);
	}

	/**
	 * Create a simple "array-backed" Event instance.
	 *
	 * @param eventT         The event class type.
	 * @param listenerT      The listener class type.
	 * @param make           Factory function for turning consuemrs into listeners
	 * @param execute        Function for executing listeners
	 * @param <T>            The event type.
	 * @param <F>            The listener type.
	 * @return The Event instance.
	 */
	public static <T, F> Event<F>
	createSimpleArrayBacked(Class<T> eventT, Class<F> listenerT, Function<Consumer<T>, F> make, Function<F, Consumer<T>> execute) {
		return EventFactoryImpl.createArrayBacked(listenerT, callbacks -> {
			return make.apply(event -> {
				for (F callback : callbacks) {
					execute.apply(callback).accept(event);
				}
			});
		});
	}

	/**
	 * Create a simple "array-backed" Event instance.
	 *
	 * @param eventA         The first event class type.
	 * @param eventB         The second event class type.
	 * @param listenerT      The listener class type.
	 * @param make           Factory function for turning consuemrs into listeners
	 * @param execute        Function for executing listeners
	 * @param <A>            The first event type.
	 * @param <B>            The second event type.
	 * @param <F>            The listener type.
	 * @return The Event instance.
	 */
	public static <A, B, F> Event<F>
	createSimpleArrayBacked(Class<A> eventA, Class<B> eventB, Class<F> listenerT, Function<BiConsumer<A, B>, F> make, Function<F, Function<A, Consumer<B>>> execute) {
		return EventFactoryImpl.createArrayBacked(listenerT, callbacks -> {
			return make.apply((a, b) -> {
				for (F callback : callbacks) {
					execute.apply(callback).apply(a).accept(b);
				}
			});
		});
	}

	/**
	 * Get the listener object name. This can be used in debugging/profiling
	 * scenarios.
	 *
	 * @param handler The listener object.
	 * @return The listener name.
	 */
	public static String getHandlerName(Object handler) {
		return handler.getClass().getName();
	}
}
