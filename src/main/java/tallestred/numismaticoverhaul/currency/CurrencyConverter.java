package tallestred.numismaticoverhaul.currency;

import tallestred.numismaticoverhaul.init.ItemInit;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyConverter {

    /**
     * @return An array of 3 {@link ItemStack}, format ItemStack[]{BRONZE, SILVER, GOLD},
     *         stack sizes here can exceed normal limits, this is a raw representation.
     */
    public static ItemStack[] getAsItemStackArray(long value) {
        ItemStack[] output = new ItemStack[]{null, null, null};

        long[] values = CurrencyResolver.splitValues(value);

        output[2] = new ItemStack(ItemInit.GOLD_COIN.get(), asInt(values[2]));
        output[1] = new ItemStack(ItemInit.SILVER_COIN.get(), asInt(values[1]));
        output[0] = new ItemStack(ItemInit.BRONZE_COIN.get(), asInt(values[0]));

        return output;
    }

    /**
     * RAW version (pour l’affichage) :
     * Wrapper for {@link #getAsItemStackArray(long)} that only includes non-zero {@link ItemStack}.
     *
     * This returns logical amounts per currency type and DOES NOT split stacks.
     */
    public static List<ItemStack> getAsItemStackListRaw(long value) {
        List<ItemStack> list = new ArrayList<>();

        Arrays.stream(getAsItemStackArray(value)).forEach(itemStack -> {
            if (itemStack != null && itemStack.getCount() != 0) {
                // On garde l’ordre "pièces les plus fortes en haut"
                list.add(0, itemStack);
            }
        });

        return list;
    }

    /**
     * RAW version (pour l’affichage) avec un tableau de valeurs.
     */
    public static List<ItemStack> getAsItemStackListRaw(long[] values) {
        List<ItemStack> list = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            if (values[i] <= 0) continue;
            list.add(0, new ItemStack(Currency.values()[i], asInt(values[i])));
        }

        return list;
    }

    /**
     * SAFE version (pour donner des pièces au joueur) :
     * retourne une liste d'ItemStack SPLITTÉS pour que jamais un stack
     * ne dépasse la vraie limite d’inventaire.
     */
    public static List<ItemStack> getAsItemStackList(long value) {
        return splitAtMaxCount(getAsItemStackListRaw(value));
    }

    /**
     * SAFE version (pour donner des pièces au joueur) avec un tableau de valeurs.
     */
    public static List<ItemStack> getAsItemStackList(long[] values) {
        return splitAtMaxCount(getAsItemStackListRaw(values));
    }

    /**
     * @return The amount of currency types required to represent this stack's raw value
     */
    public static int getRequiredCurrencyTypes(long value) {
        // Ici on veut le nombre de types logiques, donc on regarde la version RAW
        return getAsItemStackListRaw(value).size();
    }

    /**
     * Splits the provided list into another list where no stacks are over their max size
     * (en pratique, on clamp à 64 pour éviter toute perte dans l’inventaire).
     *
     * @param input A list of {@link ItemStack} that could contain some with illegal sizes
     * @return A list where no stacks have illegal sizes
     */
    public static List<ItemStack> splitAtMaxCount(List<ItemStack> input) {
        List<ItemStack> output = new ArrayList<>();

        for (ItemStack stack : input) {
            if (stack == null || stack.isEmpty()) continue;

            // On force une limite "inventaire" à 64, même si l'item annonce plus.
            int itemMax = stack.getMaxStackSize();
            int max = itemMax > 0 ? Math.min(itemMax, 64) : 64;

            if (stack.getCount() <= max) {
                output.add(stack);
            } else {
                int fullStacks = stack.getCount() / max;
                int remainder = stack.getCount() % max;

                for (int i = 0; i < fullStacks; i++) {
                    ItemStack copy = stack.copy();
                    copy.setCount(max);
                    output.add(copy);
                }

                if (remainder > 0) {
                    ItemStack copy = stack.copy();
                    copy.setCount(remainder);
                    output.add(copy);
                }
            }
        }

        return output;
    }

    /**
     * SAFE helpers déjà utilisés ailleurs.
     */
    public static List<ItemStack> getAsValidStacks(long value) {
        return splitAtMaxCount(getAsItemStackListRaw(value));
    }

    public static List<ItemStack> getAsValidStacks(long[] values) {
        return splitAtMaxCount(getAsItemStackListRaw(values));
    }

    public static int asInt(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

}
