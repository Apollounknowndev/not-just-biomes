package dev.worldgen.njb.client.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.worldgen.njb.NotJustBiomes;
import dev.worldgen.njb.config.ConfigHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ConfigScreen extends Screen implements ConfigScreenFactory<Screen> {
    private NJBOptionListWidget list;
    private final Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.translatable("config.njb.title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        list = new NJBOptionListWidget(client, width, height, 32, height - 32, 25);
        list.addCategoryEntry(Text.translatable("config.njb.category.standard_modules"));
        addModuleList(ConfigHandler.DEFAULT_CONFIG.getStandardModules());
        list.addCategoryEntry(Text.translatable("config.njb.category.special_modules"));
        addModuleList(ConfigHandler.DEFAULT_CONFIG.getSpecialModules());
        addSelectableChild(list);

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            (button) -> this.close())
                .position(width/2 - 100, height - 28)
                .size(200, 20)
                .build()
        );
    }

    public void addModuleList(Map<String, Boolean> map) {
        for (Map.Entry<String, Boolean> modules : map.entrySet()) {
            String key = modules.getKey();
            list.addSingleOptionEntry(
                SimpleOption.ofBoolean(
                    "module.njb."+key,
                    ConfigHandler.isModuleEnabled(key),
                    (value) -> ConfigHandler.getConfig().flipModuleValue(key)
                ),
                key
            );
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        list.render(drawContext, mouseX, mouseY, delta);

        drawContext.drawTexture(
            new Identifier(NotJustBiomes.MOD_ID, "textures/gui/config/"+list.getHoveredWidgetModule(mouseX, mouseY)+".png"),
            width / 2 - 155,
            height / 2 - 80,
            0,
            0,
            128,
            128,
            128,
            128
        );
        MutableText descriptionText = MutableText.of(TextContent.EMPTY);
        if (I18n.hasTranslation("module.njb."+list.getHoveredWidgetModule(mouseX, mouseY)+".desc")) {
            descriptionText.append(Text.translatable("module.njb."+list.getHoveredWidgetModule(mouseX, mouseY)+".desc"));
            if (I18n.hasTranslation("module.njb."+list.getHoveredWidgetModule(mouseX, mouseY)+".desc_extra")) {
                descriptionText.append(Text.literal("\n")).append(Text.translatable("module.njb." + list.getHoveredWidgetModule(mouseX, mouseY) + ".desc_extra").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }
        }
        drawContext.drawTextWrapped(textRenderer, descriptionText, width/2-155, height/2+52, 128, 0xbbbbbb);
        drawContext.drawCenteredTextWithShadow(textRenderer, title, width / 2, 5, 0xffffff);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
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
