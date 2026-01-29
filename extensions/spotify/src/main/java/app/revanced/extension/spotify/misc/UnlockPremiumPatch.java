package app.revanced.extension.spotify.misc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.revanced.extension.shared.Logger;

@SuppressWarnings("unused")
public class UnlockPremiumPatch {

    /**
     * Override the account attributes to enable premium features.
     * Injection point: com/spotify/remoteconfig/internal/ProductStateProto->getAttributesMap()
     */
    public static void overrideAttributes(Map<String, String> attributes) {
        if (attributes == null) return;

        // Unlock premium features
        attributes.put("type", "premium");
        attributes.put("can_seek", "true");
        attributes.put("unlimited_skips", "true");
        attributes.put("ad-free", "true");
        attributes.put("on-demand-enabled", "true");
        attributes.put("can_play_any_track", "true");
        attributes.put("can_shuffle", "true");
        attributes.put("can_repeat_track", "true");
        attributes.put("can_repeat_context", "true");
        attributes.put("streaming-quality-level", "very-high");
        attributes.put("filter-explicit-content", "false");

        // Disable some restrictions
        attributes.put("is-advertisement", "false");

        Logger.printInfo(() -> "Spotify attributes overridden for premium unlock.");
    }

    /**
     * Remove the ":station" suffix from Spotify URIs to avoid being forced into radio mode.
     * Injection point: voiceassistants/playermodels/ContextJsonAdapter->fromJson(...)
     */
    public static String removeStationString(String s) {
        if (s == null) return null;
        if (s.contains(":station")) {
            String modified = s.replace(":station", "");
            Logger.printInfo(() -> "Removed :station from URI: " + modified);
            return modified;
        }
        return s;
    }

    /**
     * Check if a context menu item should be removed (e.g., if it's an ad or "Go to artist" which is sometimes an ad).
     */
    public static boolean isFilteredContextMenuItem(Object item) {
        if (item == null) return false;
        String itemString = item.toString().toLowerCase();
        return itemString.contains("ad") || itemString.contains("sponsored") || itemString.contains("promotion");
    }

    /**
     * Filter a list of context menu items.
     */
    public static List filterContextMenuItems(List items) {
        if (items == null) return null;
        Iterator it = items.iterator();
        while (it.hasNext()) {
            if (isFilteredContextMenuItem(it.next())) {
                it.remove();
            }
        }
        return items;
    }

    /**
     * Remove ad sections from the home structure.
     */
    public static void removeHomeSections(List sections) {
        if (sections == null) return;
        Iterator it = sections.iterator();
        while (it.hasNext()) {
            Object section = it.next();
            // In Spotify, section types are handled via protobuf enums or strings.
            // A common way is to check the toString() or a specific field.
            String sectionString = section.toString().toLowerCase();
            if (sectionString.contains("ad") || sectionString.contains("sponsored") || sectionString.contains("promo")) {
                it.remove();
            }
        }
    }

    /**
     * Remove ad sections from the browse structure.
     */
    public static void removeBrowseSections(List sections) {
        removeHomeSections(sections);
    }
}
