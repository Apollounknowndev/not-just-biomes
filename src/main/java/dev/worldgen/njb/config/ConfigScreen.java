package dev.worldgen.njb.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Map;

public class ConfigScreen extends Screen implements ConfigScreenFactory<Screen> {
    private ButtonListWidget list;
    private final Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.translatable("config.njb.title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        list = new ButtonListWidget(client, width, height, 32, height - 32, 25);
        for (Map.Entry<String, Boolean> defaultConfigValues : ConfigHandler.DEFAULT_CONFIG_VALUES.entrySet()) {
            String key = defaultConfigValues.getKey();
            list.addSingleOptionEntry(
                SimpleOption.ofBoolean("config.njb.button."+key,
                ConfigHandler.getConfigValue(key),
                (value) -> ConfigHandler.flipValue(key)));
        }
        addSelectableChild(list);

        addDrawableChild(
            new ButtonWidget(
                width/2 - 100, height - 28,
                200, 20,
                Text.translatable("gui.done"),
                (button) -> this.close()
            ));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        list.render(matrices, mouseX, mouseY, delta);
        drawCenteredTextWithShadow(matrices, textRenderer, title.asOrderedText(), width / 2, 5, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
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
