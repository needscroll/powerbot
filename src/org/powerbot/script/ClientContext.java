package org.powerbot.script;

import java.util.Collection;
import java.util.EventListener;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptController;
import org.powerbot.bot.ScriptEventDispatcher;

/**
 * A context class which interlinks all core classes for a {@link org.powerbot.script.Bot}.
 *
 * @param <C> the bot client
 */
public abstract class ClientContext<C extends Client> {
	private final AtomicReference<Bot<? extends ClientContext<C>>> bot;
	private final AtomicReference<C> client;

	/**
	 * The script controller.
	 */
	public final Script.Controller controller;
	/**
	 * A table of key/value pairs representing environmental properties.
	 */
	public final Properties properties;
	/**
	 * A collection representing the event listeners attached to the {@link org.powerbot.script.Bot}.
	 */
	public final Collection<EventListener> dispatcher;
	/**
	 * The input simulator for sending keyboard and mouse events.
	 */
	public final Input input;

	/**
	 * Creates a new context with the given {@link org.powerbot.script.Bot}.
	 *
	 * @param bot the bot
	 */
	protected ClientContext(final Bot<? extends ClientContext<C>> bot) {
		this.bot = new AtomicReference<Bot<? extends ClientContext<C>>>(bot);
		client = new AtomicReference<C>(null);
		@SuppressWarnings("unchecked")
		final ScriptController c = new ScriptController(this);
		controller = c;
		properties = new Properties();
		dispatcher = new ScriptEventDispatcher<C, EventListener>(this);
		input = new InputSimulator(bot.chrome);
	}

	/**
	 * Creates a chained context.
	 *
	 * @param ctx the parent context
	 */
	protected ClientContext(final ClientContext<C> ctx) {
		bot = ctx.bot;
		client = ctx.client;
		controller = ctx.controller;
		properties = ctx.properties;
		dispatcher = ctx.dispatcher;
		input = ctx.input;
	}

	/**
	 * Returns the client version.
	 *
	 * @return the client version, which is {@code 6} for {@code rt6} and {@code} 4 for {@code rt4}
	 */
	public final String rtv() {
		final Class<?> c = getClass();
		if (org.powerbot.script.rt6.ClientContext.class.isAssignableFrom(c)) {
			return "6";
		}
		if (org.powerbot.script.rt4.ClientContext.class.isAssignableFrom(c)) {
			return "4";
		}
		return "";
	}

	/**
	 * Returns the bot.
	 *
	 * @return the bot
	 */
	public final Bot<? extends ClientContext<C>> bot() {
		return bot.get();
	}

	/**
	 * Returns the client.
	 *
	 * @return the client.
	 */
	public final C client() {
		return client.get();
	}

	/**
	 * Sets the client.
	 *
	 * @param c the new client
	 * @return the previous value, which may be {@code null}
	 */
	public final C client(final C c) {
		return client.getAndSet(c);
	}
}
