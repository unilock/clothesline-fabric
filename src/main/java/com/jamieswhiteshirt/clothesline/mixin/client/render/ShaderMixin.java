package com.jamieswhiteshirt.clothesline.mixin.client.render;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gl.Program.Type;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import com.jamieswhiteshirt.clothesline.client.render.ClotheslineShader;

@Mixin(Shader.class)
abstract class ShaderMixin {
	@Shadow
	private @Final String name;

	@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), allow = 1)
	private String modifyProgramId(String id) {
		return (Object) this instanceof ClotheslineShader ? "clothesline" + Identifier.NAMESPACE_SEPARATOR + id : id;
	}

	@ModifyVariable(method = "loadProgram", at = @At("STORE"), ordinal = 1)
	private static String modifyStageId(String id, ResourceFactory factory, Type type, String name) {
		if (name.indexOf(Identifier.NAMESPACE_SEPARATOR) > 0) {
			Identifier contained = new Identifier(name);
			return contained.getNamespace() + Identifier.NAMESPACE_SEPARATOR + id.replace(name, contained.getPath());
		}

		return id;
	}
}