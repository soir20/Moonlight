package net.mehvahdjukaar.moonlight.api.map.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.moonlight.api.map.CustomMapDecoration;
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public class MapDecorationClientManager extends TextureAtlasHolder {

    public static final ResourceLocation LOCATION_MAP_MARKERS = Moonlight.res("textures/atlas/map_markers.png");
    public static final RenderType MAP_MARKERS_RENDER_TYPE = RenderType.text(LOCATION_MAP_MARKERS);
    private static MapDecorationClientManager instance;

    public MapDecorationClientManager() {
        super(Minecraft.getInstance().getTextureManager(), LOCATION_MAP_MARKERS, Moonlight.res("map_markers"));
        instance = this;
    }

    public static TextureAtlasSprite getAtlasSprite(ResourceLocation location) {
        return instance.getSprite(location);
    }

    /**
     * Registers a renderer for this decoration. Use it to add fancy ones
     */
    public static <T extends CustomMapDecoration> void registerCustomRenderer(MapDecorationType<T, ?> type, DecorationRenderer<T> renderer) {
        RENDERERS.put(type, renderer);
    }

    private static final Map<MapDecorationType<?, ?>, DecorationRenderer<?>> RENDERERS = Maps.newHashMap();


    private static <T extends CustomMapDecoration> DecorationRenderer<T> simpleRenderer(MapDecorationType<T, ?> type) {
        var id = Utils.getID(type);
        ResourceLocation texture = new ResourceLocation(id.getNamespace(), "map_marker/" + id.getPath());
        return new DecorationRenderer<>(texture);
    }

    @ApiStatus.Internal
    public static <E extends CustomMapDecoration> DecorationRenderer<E> getRenderer(E decoration) {
        return (DecorationRenderer<E>) RENDERERS.computeIfAbsent(decoration.getType(), t -> simpleRenderer(decoration.getType()));
    }

    @ApiStatus.Internal
    public static <T extends CustomMapDecoration> boolean render(T decoration, PoseStack matrixStack, VertexConsumer vertexBuilder, MultiBufferSource buffer, MapItemSavedData mapData, boolean isOnFrame, int light, int index) {
        DecorationRenderer<T> renderer = getRenderer(decoration);
        if (renderer != null) {
            return renderer.render(decoration, matrixStack, vertexBuilder, buffer, mapData, isOnFrame, light, index);
        }
        return false;
    }


}
