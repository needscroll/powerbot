package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.WidgetNode;
import org.powerbot.bot.rt4.tools.HashTable;
import org.powerbot.script.Validatable;

public class Component extends ClientAccessor implements Validatable {
	public static final Color TARGET_STROKE_COLOR = new Color(0, 255, 0, 150);
	public static final Color TARGET_FILL_COLOR = new Color(0, 0, 0, 50);

	private final Widget widget;
	private final Component component;
	private final int index;

	private Component[] sparseCache;

	Component(final ClientContext ctx, final Widget widget, final int index) {
		this(ctx, widget, null, index);
	}

	Component(final ClientContext ctx, final Widget widget, final Component component, final int index) {
		super(ctx);
		this.widget = widget;
		this.component = component;
		this.index = index;

		sparseCache = new Component[0];
	}

	public Widget widget() {
		return widget;
	}

	public int index() {
		return index;
	}

	public Point screenPoint() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget widget = getInternal();
		if (client == null || widget == null) {
			return new Point(-1, -1);
		}
		final int uid = parentId();
		if (uid != -1) {
			final Component c = ctx.widgets.get(uid >> 16).component(uid & 0xffff);
			final Point p = c.screenPoint();
			if (p.x != -1 && p.y != -1) {
				return new Point(p.x + widget.getX(), p.y + widget.getY());
			}
		}
		final int[] boundsX = client.getWidgetBoundsX(), boundsY = client.getWidgetBoundsY();
		final int bounds = boundsIndex();
		if (boundsX != null && boundsY != null && bounds >= 0 && bounds < boundsX.length && bounds < boundsY.length) {
			final int x = boundsX[bounds], y = boundsY[bounds];
			return new Point(x - widget.getScrollX(), y - widget.getScrollY());
		}
		return new Point(-1, -1);
	}

	public int relativeX() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getX() : -1;
	}

	public int relativeY() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getY() : -1;
	}

	public int width() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getWidth() : -1;
	}

	public int height() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getHeight() : -1;
	}

	public Rectangle getRect() {
		final Point p = screenPoint();
		return new Rectangle(p.x, p.y, width(), height());
	}

	public int borderThickness() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getBorderThickness() : -1;
	}

	public int type() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getType() : -1;
	}

	public int id() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getId() : -1;
	}

	public int parentId() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		if (client == null || w == null) {
			return -1;
		}
		final int p = w.getParentId();
		if (p != -1) {
			return p;
		}

		final int uid = id() >>> 16;
		for (final WidgetNode node : new HashTable<WidgetNode>(client.getWidgetTable(), WidgetNode.class)) {
			if (uid == node.getUid()) {
				return (int) node.getId();
			}
		}
		return -1;
	}

	public synchronized Component component(final int index) {
		if (index < sparseCache.length && sparseCache[index] != null) {
			return sparseCache[index];
		}
		final Component c = new Component(ctx, widget, this, index);
		final int l = sparseCache.length;
		if (index >= l) {
			sparseCache = Arrays.copyOf(sparseCache, index + 1);
			for (int i = l; i < index + 1; i++) {
				sparseCache[i] = new Component(ctx, widget, this, i);
			}
		}
		return sparseCache[index] = c;
	}

	public synchronized int componentCount() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		final org.powerbot.bot.rt4.client.Widget[] arr = w != null ? w.getChildren() : null;
		return arr != null ? arr.length : 0;
	}

	public Component[] components() {
		final int len = componentCount();
		if (len <= 0) {
			return new Component[0];
		}
		component(len - 1);
		return Arrays.copyOf(sparseCache, len);
	}

	public int contentType() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getContentType() : -1;
	}

	public int modelId() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getModelId() : -1;
	}

	public int modelType() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getModelType() : -1;
	}

	public int modelZoom() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getModelZoom() : -1;
	}

	public String[] actions() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		final String[] arr = w != null ? w.getActions() : new String[0];
		for (int i = 0; i < (arr != null ? arr.length : 0); i++) {
			if (arr[i] == null) {
				arr[i] = "";
			}
		}
		return arr != null ? arr : new String[0];
	}

	public int angleX() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getAngleX() : -1;
	}

	public int angleY() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getAngleY() : -1;
	}

	public int angleZ() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getAngleZ() : -1;
	}

	public String text() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		final String str = w != null ? w.getText() : "";
		return str != null ? str : "";
	}

	public int textColor() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		//return w != null ? w.getTextColor() : -1;
		return -1;//TODO
	}

	public int scrollX() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getScrollX() : -1;
	}

	public int scrollY() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getScrollY() : -1;
	}

	public int scrollWidth() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getScrollWidth() : -1;
	}

	public int scrollHeight() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getScrollHeight() : -1;
	}

	public int boundsIndex() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getBoundsIndex() : -1;
	}

	public int textureId() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		return w != null ? w.getTextureId() : -1;
	}

	public int[] itemIds() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		final int[] a = w != null ? w.getItemIds() : new int[0];
		return a != null ? a : new int[0];
	}

	public int[] itemStackSizes() {
		final org.powerbot.bot.rt4.client.Widget w = getInternal();
		final int a[] = w != null ? w.getItemStackSizes() : new int[0];
		return a != null ? a : new int[0];
	}

	@Override
	public boolean valid() {
		final org.powerbot.bot.rt4.client.Widget internal = getInternal();
		return internal != null && (component == null || component.visible()) &&
				id() != -1 && internal.getBoundsIndex() != -1;
	}

	public boolean visible() {
		final org.powerbot.bot.rt4.client.Widget internal = getInternal();
		int id = 0;
		if (internal != null && valid() && !internal.isHidden()) {
			id = parentId();
		}
		return id == -1 || (id != 0 && ctx.widgets.get(id >> 16).component(id & 0xffff).visible());
	}

	private org.powerbot.bot.rt4.client.Widget getInternal() {
		final int wi = widget.index();
		if (component != null) {
			final org.powerbot.bot.rt4.client.Widget _i = component.getInternal();
			final org.powerbot.bot.rt4.client.Widget[] arr = _i != null ? _i.getChildren() : null;
			if (arr != null && index < arr.length) {
				return arr[index];
			}
			return null;
		}
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Widget[][] arr = client != null ? client.getWidgets() : null;
		if (arr != null && wi < arr.length) {
			final org.powerbot.bot.rt4.client.Widget[] comps = arr[wi];
			return comps != null && index < comps.length ? comps[index] : null;
		}
		return null;
	}
}