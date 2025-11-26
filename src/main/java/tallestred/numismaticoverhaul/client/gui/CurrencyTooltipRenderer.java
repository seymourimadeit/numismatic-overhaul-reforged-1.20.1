package tallestred.numismaticoverhaul.client.gui;

import tallestred.numismaticoverhaul.currency.CurrencyConverter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CurrencyTooltipRenderer {

    public static void renderTooltip(long value, GuiGraphics matrices, Screen screen, Component title, int x, int y) {

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(title);

        y += 10;

        // IMPORTANT : ici on utilise la version RAW (affichage logique),
        // donc pas de split en 64 + reste, juste "1 or, 64 argent, 25 cuivre".
        List<ItemStack> coins = CurrencyConverter.getAsItemStackListRaw(value);

        matrices.pose().translate(0, 0, 500);

        for (int i = 0; i < coins.size(); i++) {
            renderStack(matrices, coins.get(i), tooltip, i, x, y);
        }
        matrices.pose().translate(0, 0, -500);

        if (tooltip.size() == 1) {
            tooltip.add(Component.translatable("numismaticoverhaul.empty").withStyle(ChatFormatting.GRAY));
        }

        matrices.renderComponentTooltip(Minecraft.getInstance().font, tooltip, x, y - 15);
    }

    private static void renderStack(GuiGraphics graphics, ItemStack stack, List<Component> tooltip, int index, int x, int y) {
        tooltip.add(createPlaceholder(String.valueOf(stack.getCount())));

        ItemStack toRender = stack.copy();
        toRender.setCount(1);

        int localX = x + 8;
        int localY = y - (2 - index) * 10;

        graphics.renderFakeItem(toRender, localX, localY);
    }

    private static Component createPlaceholder(String text) {
        String placeholder = "§7   " + text + " ";
        return Component.nullToEmpty(placeholder);
    }

}
