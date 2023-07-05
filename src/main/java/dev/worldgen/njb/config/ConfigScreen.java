package dev.worldgen.njb.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.worldgen.njb.NotJustBiomes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.Map;

public class ConfigScreen extends Screen implements ConfigScreenFactory<Screen> {
    private OptionListWidget list;
    private final Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.translatable("config.njb.title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        list = new OptionListWidget(client, width, height, 32, height - 32, 25);
        for (Map.Entry<String, Boolean> defaultConfigValues : ConfigHandler.DEFAULT_CONFIG_VALUES.entrySet()) {
            String key = defaultConfigValues.getKey();
            list.addSingleOptionEntry(
                SimpleOption.ofBoolean(
                "config.njb.button."+key,
                    ConfigHandler.getConfigValue(key),
                    (value) -> ConfigHandler.flipValue(key)
                )
            );
        }
        if (NotJustBiomes.isTectonicLoaded) {
            list.addSingleOptionEntry(
                SimpleOption.ofBoolean(
                    "config.njb.button.tectonic_trees",
                    SimpleOption.constantTooltip(Text.translatable("config.njb.button.tectonic_trees.tooltip")),
                    ConfigHandler.getConfigValue("tectonic_trees"),
                    (value) -> ConfigHandler.flipValue("tectonic_trees")
                )
            );
        }
        addSelectableChild(list);

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            (button) -> this.close())
                .position(width/2 - 100, height - 28)
                .size(200, 20)
                .build()
        );
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        list.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(textRenderer, title, width / 2, 5, 0xffffff);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public Screen create(Screen parent) {
        return this;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
