package tallestred.numismaticoverhaul.client;

import com.mojang.datafixers.util.Either;
import io.wispforest.owo.mixin.ui.layers.HandledScreenAccessor;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.layers.Layers;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import tallestred.numismaticoverhaul.NumismaticOverhaul;
import tallestred.numismaticoverhaul.client.gui.CurrencyTooltipComponent;
import tallestred.numismaticoverhaul.client.gui.PiggyBankScreen;
import tallestred.numismaticoverhaul.client.gui.ShopScreen;
import tallestred.numismaticoverhaul.client.gui.purse.PurseLayerElement;
import tallestred.numismaticoverhaul.config.NOClientConfig;
import tallestred.numismaticoverhaul.init.BlockInit;
import tallestred.numismaticoverhaul.init.ItemInit;
import tallestred.numismaticoverhaul.init.MenuInit;
import tallestred.numismaticoverhaul.item.CurrencyTooltipData;
import tallestred.numismaticoverhaul.item.MoneyBagItem;
import tallestred.numismaticoverhaul.client.gui.purse.PurseLayerContainer;
import tallestred.numismaticoverhaul.mixin.LayerInstanceAccessor;

@Mod(value = NumismaticOverhaul.MODID, dist = Dist.CLIENT)
public class NumismaticOverhaulClient {

    public NumismaticOverhaulClient(IEventBus modEventBus, Dist dist, ModContainer container) {
        modEventBus.addListener(this::onInitializeClient);
        modEventBus.addListener(this::registerBlockEntity);
        modEventBus.addListener(this::registerMenus);
    }


    public void onInitializeClient(FMLClientSetupEvent event) {
        ItemProperties.register(ItemInit.BRONZE_COIN.get(), ResourceLocation.parse("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(ItemInit.SILVER_COIN.get(), ResourceLocation.parse("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(ItemInit.GOLD_COIN.get(), ResourceLocation.parse("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);

        ItemProperties.register(ItemInit.MONEY_BAG.get(), ResourceLocation.parse("size"), (stack, world, entity, seed) -> {
            long[] values = ((MoneyBagItem) ItemInit.MONEY_BAG.get()).getCombinedValue(stack);
            if (values.length < 3) return 0;

            if (values[2] > 0) return 1;
            if (values[1] > 0) return .5f;

            return 0;
        });
        Layers.add(
                PurseLayerContainer::new,
                new PurseLayerElement<>((instance, component) -> {
                    instance.aggressivePositioning = true;
                    ((LayerInstanceAccessor) instance).numismatic$getLayoutUpdaters().add(() -> {
                        if (instance.screen.isInventoryOpen()) {
                            component.positioning(Positioning.absolute(
                                    ((HandledScreenAccessor) instance.screen).owo$getRootX() + 38 + NOClientConfig.CLIENT.creativePursePositionX.get(),
                                    ((HandledScreenAccessor) instance.screen).owo$getRootY() + 4 + NOClientConfig.CLIENT.creativePursePositionY.get())
                            );
                        } else {
                            component.positioning(Positioning.absolute(-50, -50));
                        }
                    });
                }),
                CreativeModeInventoryScreen.class
        );
        Layers.add(
                PurseLayerContainer::new,
                new PurseLayerElement<>((instance, component) -> {
                    instance.aggressivePositioning = true;
                    instance.alignComponentToHandledScreenCoordinates(
                            component,
                            160 + NOClientConfig.CLIENT.survivalPursePositionX.get(),
                            5 + NOClientConfig.CLIENT.survivalPursePositionY.get()
                    );
                }),
                InventoryScreen.class
        );
        Layers.add(
                PurseLayerContainer::new,
                new PurseLayerElement<>((instance, component) -> instance.alignComponentToHandledScreenCoordinates(
                        component,
                        260 + NOClientConfig.CLIENT.merchantPursePositionX.get(),
                        5 + NOClientConfig.CLIENT.merchantPursePositionY.get()
                )),
                MerchantScreen.class
        );
    }


    public void registerMenus(RegisterMenuScreensEvent event) {
        event.register(MenuInit.SHOP.get(), ShopScreen::new);
        event.register(MenuInit.PIGGY_BANK.get(), PiggyBankScreen::new);
    }


    public void registerBlockEntity(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockInit.SHOP_BE.get(), ShopBlockEntityRender::new);
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    static class ForgeEvents {
        @SubscribeEvent
        public static void onToolTip(RenderTooltipEvent.GatherComponents event) {
            for (int i = 0; i < event.getTooltipElements().size(); i++) {
                if (event.getTooltipElements().get(i).right().isPresent()) {
                    if (event.getTooltipElements().get(i).right().get() instanceof CurrencyTooltipData component) {
                        Either<FormattedText, TooltipComponent> bope = event.getTooltipElements().get(i);
                        bope.mapRight((tooltipComponent) -> new CurrencyTooltipComponent(component));
                        event.getTooltipElements().set(i, bope);
                        break;
                    }
                }
            }
        }
    }
}
