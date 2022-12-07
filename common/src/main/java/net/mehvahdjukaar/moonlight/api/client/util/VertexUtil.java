package net.mehvahdjukaar.moonlight.api.client.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.mehvahdjukaar.moonlight.api.util.math.colors.BaseColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VertexUtil {

    private static int getFormatLength() {
        return DefaultVertexFormat.BLOCK.getIntegerSize();
    }


    /**
     * Replaces all the texture in te given model with the given sprite
     */
    public static List<BakedQuad> swapSprite(List<BakedQuad> quads, TextureAtlasSprite sprite) {
        List<BakedQuad> newList = new ArrayList<>();
        for (BakedQuad q : quads) {
            TextureAtlasSprite oldSprite = q.getSprite();
            int formatLength = getFormatLength();
            int[] v = Arrays.copyOf(q.getVertices(), q.getVertices().length);
            for (int i = 0; i < v.length / formatLength; i++) {
                float originalU = Float.intBitsToFloat(v[i * formatLength + 4]);
                float originalV = Float.intBitsToFloat(v[i * formatLength + 5]);
                v[i * formatLength + 4] = Float.floatToIntBits(originalU - oldSprite.getU0() + sprite.getU0());
                v[i * formatLength + 5] = Float.floatToIntBits(originalV - oldSprite.getV0() + sprite.getV0());
            }
            newList.add(new BakedQuad(v, q.getTintIndex(), q.getDirection(), sprite, q.isShade()));
        }
        return newList;
    }

    public static void transformVertices(int[] v, Matrix3f transform) {
        int formatLength = getFormatLength();
        for (int i = 0; i < v.length / formatLength; i++) {
            float originalX = Float.intBitsToFloat(v[i * formatLength]) - 0.5f;
            float originalY = Float.intBitsToFloat(v[i * formatLength + 1]) - 0.5f;
            float originalZ = Float.intBitsToFloat(v[i * formatLength + 2]) - 0.5f;
            Vector3f vector3f = new Vector3f(originalX, originalY, originalZ);
            vector3f.transform(transform);
            v[i * formatLength] = Float.floatToIntBits(vector3f.x() + 0.5f);
            v[i * formatLength + 1] = Float.floatToIntBits(vector3f.y() + 0.5f);
            v[i * formatLength + 2] = Float.floatToIntBits(vector3f.z() + 0.5f);
        }
    }

    public static void transformVertices(int[] v, PoseStack stack, TextureAtlasSprite sprite) {
        Vector4f vector4f = new Vector4f(0, 0, 0, 1.0F);
        vector4f.transform(stack.last().pose());
        moveVertices(v, vector4f.x(), vector4f.y(), vector4f.z());
    }


    public static void rotateVerticesY(int[] v, TextureAtlasSprite sprite, Rotation rot) {
        var matrix = rot.rotation().transformation();
        transformVertices(v, matrix);
    }

    /**
     * moves baked vertices in a direction by amount
     */
    public static void moveVertices(int[] v, Direction dir, float amount) {

        int formatLength = getFormatLength();

        //checkShaders();
        int axis = dir.getAxis().ordinal();
        float step = amount * dir.getAxisDirection().getStep();
        for (int i = 0; i < v.length / formatLength; i++) {
            float original = Float.intBitsToFloat(v[i * formatLength + axis]);
            v[i * formatLength + axis] = Float.floatToIntBits(original + step);
        }
    }

    /**
     * moves baked vertices by amount
     */
    public static void moveVertices(int[] v, float x, float y, float z) {
        int formatLength = getFormatLength();
        for (int i = 0; i < v.length / formatLength; i++) {
            float originalX = Float.intBitsToFloat(v[i * formatLength]);
            v[i * formatLength] = Float.floatToIntBits(originalX + x);

            float originalY = Float.intBitsToFloat(v[i * formatLength + 1]);
            v[i * formatLength + 1] = Float.floatToIntBits(originalY + y);

            float originalZ = Float.intBitsToFloat(v[i * formatLength + 2]);
            v[i * formatLength + 2] = Float.floatToIntBits(originalZ + z);
        }
    }


    /**
     * scale baked vertices by amount
     */
    public static void scaleVertices(int[] v, float scale) {
        int formatLength = getFormatLength();
        for (int i = 0; i < v.length / formatLength; i++) {
            float originalX = Float.intBitsToFloat(v[i * formatLength]);
            v[i * formatLength] = Float.floatToIntBits(originalX * scale);

            float originalY = Float.intBitsToFloat(v[i * formatLength + 1]);
            v[i * formatLength + 1] = Float.floatToIntBits(originalY * scale);

            float originalZ = Float.intBitsToFloat(v[i * formatLength + 2]);
            v[i * formatLength + 2] = Float.floatToIntBits(originalZ * scale);
        }
    }

}