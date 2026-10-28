package za.co.richfield.smartpantrymanager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class IngredientMatcher {
    private IngredientMatcher() {}

    public static boolean canMake(Recipe recipe, List<RecipeIngredient> requirements,
                                  List<PantryItem> pantry) {
        if (requirements == null || requirements.isEmpty()) return false;
        for (RecipeIngredient required : requirements) {
            String targetName = normalizeName(required.getName());
            double available = 0;
            boolean found = false;
            for (PantryItem item : pantry) {
                if (!normalizeName(item.getName()).equals(targetName)) continue;
                Conversion have = convert(item.getQuantity(), item.getUnit());
                Conversion need = convert(required.getQuantity(), required.getUnit());
                if (have == null || need == null || !have.dimension.equals(need.dimension)) {
                    if (normalizeUnit(item.getUnit()).equals(normalizeUnit(required.getUnit()))) {
                        available += item.getQuantity();
                        found = true;
                    }
                } else {
                    available += have.baseQuantity;
                    found = true;
                }
            }
            Conversion need = convert(required.getQuantity(), required.getUnit());
            if (!found) return false;
            if (need != null) {
                double baseAvailable = 0;
                for (PantryItem item : pantry) {
                    if (normalizeName(item.getName()).equals(targetName)) {
                        Conversion have = convert(item.getQuantity(), item.getUnit());
                        if (have == null || !have.dimension.equals(need.dimension)) {
                            if (normalizeUnit(item.getUnit()).equals(normalizeUnit(required.getUnit()))) {
                                baseAvailable += item.getQuantity();
                            }
                        } else {
                            baseAvailable += have.baseQuantity;
                        }
                    }
                }
                // If every matching unit was dimension-convertible, compare base quantities.
                boolean convertible = true;
                for (PantryItem item : pantry) {
                    if (normalizeName(item.getName()).equals(targetName)) {
                        Conversion have = convert(item.getQuantity(), item.getUnit());
                        if (have == null || !have.dimension.equals(need.dimension)) {
                            convertible = false;
                            break;
                        }
                    }
                }
                if (convertible && baseAvailable + 1e-9 < need.baseQuantity) return false;
                if (!convertible) {
                    double sameUnit = 0;
                    for (PantryItem item : pantry) {
                        if (normalizeName(item.getName()).equals(targetName)
                                && normalizeUnit(item.getUnit()).equals(normalizeUnit(required.getUnit()))) {
                            sameUnit += item.getQuantity();
                        }
                    }
                    if (sameUnit + 1e-9 < required.getQuantity()) return false;
                }
            } else if (available + 1e-9 < required.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String n = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        Map<String, String> aliases = new HashMap<>();
        aliases.put("tomatoes", "tomato");
        aliases.put("potatoes", "potato");
        aliases.put("onions", "onion");
        aliases.put("eggs", "egg");
        aliases.put("carrots", "carrot");
        aliases.put("apples", "apple");
        aliases.put("bananas", "banana");
        aliases.put("peppers", "pepper");
        aliases.put("chilies", "chili");
        aliases.put("chillies", "chili");
        aliases.put("garlic cloves", "garlic");
        aliases.put("cloves of garlic", "garlic");
        aliases.put("green peppers", "green pepper");
        aliases.put("cheeses", "cheese");
        aliases.put("beans", "bean");
        aliases.put("mushrooms", "mushroom");
        aliases.put("cucumbers", "cucumber");
        aliases.put("leaves of spinach", "spinach");
        return aliases.containsKey(n) ? aliases.get(n) : n;
    }

    public static String normalizeUnit(String raw) {
        if (raw == null) return "";
        String u = raw.trim().toLowerCase(Locale.ROOT);
        if (u.equals("pieces") || u.equals("piece") || u.equals("each") || u.equals("unit")) return "pcs";
        if (u.equals("kilogram") || u.equals("kilograms") || u.equals("kgs")) return "kg";
        if (u.equals("gram") || u.equals("grams")) return "g";
        if (u.equals("litre") || u.equals("litres") || u.equals("liter") || u.equals("liters")) return "l";
        if (u.equals("millilitre") || u.equals("millilitres") || u.equals("milliliter") || u.equals("milliliters")) return "ml";
        if (u.equals("tablespoon") || u.equals("tablespoons") || u.equals("tbsp")) return "tbsp";
        if (u.equals("teaspoon") || u.equals("teaspoons") || u.equals("tsp")) return "tsp";
        return u;
    }

    private static Conversion convert(double quantity, String rawUnit) {
        String unit = normalizeUnit(rawUnit);
        switch (unit) {
            case "kg": return new Conversion("mass", quantity * 1000);
            case "g": return new Conversion("mass", quantity);
            case "l": return new Conversion("volume", quantity * 1000);
            case "ml": return new Conversion("volume", quantity);
            case "tbsp": return new Conversion("volume-spoon", quantity * 3);
            case "tsp": return new Conversion("volume-spoon", quantity);
            case "pcs": return new Conversion("count", quantity);
            default: return null;
        }
    }

    private static class Conversion {
        final String dimension;
        final double baseQuantity;
        Conversion(String dimension, double baseQuantity) {
            this.dimension = dimension;
            this.baseQuantity = baseQuantity;
        }
    }
}
