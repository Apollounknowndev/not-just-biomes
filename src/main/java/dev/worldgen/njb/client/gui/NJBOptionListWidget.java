package dev.worldgen.njb.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class NJBOptionListWidget extends ElementListWidget<NJBOptionListWidget.Entry> {
    private String hoveredWidgetModule = "none";
    public NJBOptionListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.centerListVertically = false;
    }
    public void addCategoryEntry(Text text) {
        this.addEntry(new CategoryEntry(text));
    }

    public void addSingleOptionEntry(SimpleOption<?> option, String module) {
        this.addEntry(WidgetEntry.create(this.client.options, this.width, option, module));
    }

    public String getHoveredWidgetModule(double mouseX, double mouseY) {
        for (Entry entry : this.children().stream().filter((entry) -> entry instanceof WidgetEntry).collect(Collectors.toSet())) {
            NJBOptionListWidget.WidgetEntry widgetEntry = (WidgetEntry)entry;
            for(ClickableWidget widget : widgetEntry.widgets) {
                if(widget.isMouseOver(mouseX, mouseY) && mouseY > 32) {
                    this.hoveredWidgetModule = widgetEntry.module;
                    return widgetEntry.module;
                }
            }
        }
        return this.hoveredWidgetModule;
    }
    public int getRowWidth() {
        return 400;
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }
    @Environment(EnvType.CLIENT)
    protected static class WidgetEntry extends Entry {
        final Map<SimpleOption<?>, ClickableWidget> optionsToWidgets;
        final List<ClickableWidget> widgets;
        final String module;

        private WidgetEntry(Map<SimpleOption<?>, ClickableWidget> optionsToWidgets, String module) {
            this.optionsToWidgets = optionsToWidgets;
            this.widgets = ImmutableList.copyOf(optionsToWidgets.values());
            this.module = module;
        }

        public static NJBOptionListWidget.WidgetEntry create(GameOptions options, int width, SimpleOption<?> option, String module) {
            return new NJBOptionListWidget.WidgetEntry(ImmutableMap.of(option, option.createWidget(options, width / 2 + 5, 0, 150)), module);
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.widgets.forEach((widget) -> {
                widget.setY(y);
                widget.render(context, mouseX, mouseY, tickDelta);
            });
        }

        public List<? extends Element> children() {
            return this.widgets;
        }

        public List<? extends Selectable> selectableChildren() {
            return this.widgets;
        }
    }

    @Environment(EnvType.CLIENT)
    protected class CategoryEntry extends Entry {
        final Text text;
        private final int textWidth;

        public CategoryEntry(Text text) {
            this.text = text;
            this.textWidth = NJBOptionListWidget.this.client.textRenderer.getWidth(this.text);
        }

        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Objects.requireNonNull(NJBOptionListWidget.this.client.currentScreen);
            context.drawText(
                NJBOptionListWidget.this.client.textRenderer,
                this.text,
                NJBOptionListWidget.this.client.currentScreen.width / 2 - this.textWidth / 2 + 80,
                y + entryHeight  - 10,
                16777215,
                true
            );
        }

        @Nullable
        public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            return null;
        }
        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable() {
                public Selectable.SelectionType getType() {
                    return SelectionType.HOVERED;
                }

                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, CategoryEntry.this.text);
                }
            });
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {
        public Entry() {
        }
    }
}
