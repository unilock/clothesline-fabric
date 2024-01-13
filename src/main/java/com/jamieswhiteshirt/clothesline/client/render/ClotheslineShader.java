package com.jamieswhiteshirt.clothesline.client.render;

import java.io.IOException;
import java.util.function.Consumer;

import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderLayers;

import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceManager;

public class ClotheslineShader extends Shader implements Consumer<Shader> {
	private static Shader instance;

	public ClotheslineShader(ResourceManager manager) throws IOException {
		super(manager, "rendertype_clothesline", ClotheslineRenderLayers.getClothesline().getVertexFormat());
	}

	@Override
	public void accept(Shader instance) {
		ClotheslineShader.instance = instance;
	}

	public static Shader getInstance() {
		return instance;
	}
}