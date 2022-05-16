package net.mehvahdjukaar.selene.resourcepack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mehvahdjukaar.selene.Selene;
import net.mehvahdjukaar.selene.client.asset_generators.LangBuilder;
import net.mehvahdjukaar.selene.client.asset_generators.textures.TextureImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.nio.file.Files;
import java.nio.file.Path;

public class DynamicTexturePack extends DynamicResourcePack {

    public DynamicTexturePack(ResourceLocation name, PackType type, Pack.Position position, boolean fixed, boolean hidden) {
        super(name, PackType.CLIENT_RESOURCES, position, fixed, hidden);
    }

    public DynamicTexturePack(ResourceLocation name) {
        super(name, PackType.CLIENT_RESOURCES);
    }

    void addPackLogo() {
        ModList.get().getModContainerById(this.mainNamespace).ifPresent(m -> {

            IModInfo mod = m.getModInfo();
            IModFile file = mod.getOwningFile().getFile();

            mod.getLogoFile().ifPresent(logo -> {
                try {
                    if (file != null) {
                        Path logoPath = file.findResource(logo);
                        if (Files.exists(logoPath)) {
                            this.addRootResource("pack.png", Files.readAllBytes(logoPath));
                        }
                    }
                } catch (Exception e) {
                    Selene.LOGGER.error("Failed to add dynamic pack logo. Why?");
                }
            });
        });
    }

    /**
     * Adds a new textures and closes the passed native image
     */
    public void addAndCloseTexture(ResourceLocation path, TextureImage image) {
        try (image) {
            this.addBytes(path, image.getImage().asByteArray(), ResType.TEXTURES);
            JsonObject mcmeta = image.serializeMcMeta();
            if(mcmeta != null) this.addJson(path, mcmeta, ResType.MCMETA);
        } catch (Exception e) {
            LOGGER.warn("Failed to add image {} to resource pack {}.", path, this.resourcePackName, e);
        }
    }

    public void addBlockModel(ResourceLocation modelLocation, JsonElement model) {
        this.addJson(modelLocation, model, ResType.BLOCK_MODELS);
    }

    public void addItemModel(ResourceLocation modelLocation, JsonElement model) {
        this.addJson(modelLocation, model, ResType.ITEM_MODELS);
    }

    public void addBlockState(ResourceLocation modelLocation, JsonElement model) {
        this.addJson(modelLocation, model, ResType.BLOCKSTATES);
    }

    public void addLang(ResourceLocation langName, JsonElement language) {
        this.addJson(langName, language, ResType.LANG);
    }

    public void addLang(ResourceLocation langName, LangBuilder builder) {
        this.addJson(langName, builder.build(), ResType.LANG);
    }
}
