package com.zoonie.InteractionSounds.helper;

import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.handler.ChannelHandler;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.network.packet.SoundChunkPacket;
import com.zoonie.InteractionSounds.network.packet.SoundUploadedPacket;
import com.zoonie.InteractionSounds.sound.Sound;

public class NetworkHelper
{
	public static final int PARTITION_SIZE = 30000;

	public static void sendMessageToPlayer(IMessage message, EntityPlayerMP player)
	{
		ChannelHandler.network.sendTo(message, player);
	}

	@SideOnly(Side.CLIENT)
	public static void clientSoundUpload(Sound sound)
	{
		sound.setState(Sound.SoundState.UPLOADING);
		uploadSound(sound, sound.getCategory());
	}

	public static void serverSoundUpload(Sound sound, EntityPlayerMP player)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
		{
			byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
			ChannelHandler.network.sendTo(new SoundChunkPacket(sound.getSoundName(), sound.getCategory(), bytes), player);
		}
		ChannelHandler.network.sendTo(new SoundUploadedPacket(sound.getSoundName(), sound.getCategory()), player);
	}

	private static void uploadSound(Sound sound, String category)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
		{
			byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
			ChannelHandler.network.sendToServer(new SoundChunkPacket(sound.getSoundName(), sound.getCategory(), bytes));
		}
		ChannelHandler.network.sendToServer(new SoundUploadedPacket(sound.getSoundName(), category));
	}

	public static byte[] convertFileToByteArr(File file)
	{
		if(file != null && file.exists())
		{
			try
			{
				return FileUtils.readFileToByteArray(file);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File createFileFromByteArr(byte[] byteFile, String category, String fileName)
	{
		if(byteFile != null && byteFile.length > 0 && !category.isEmpty() && !fileName.isEmpty())
		{
			File file = new File(SoundHandler.getSoundsFolder().getAbsolutePath() + File.separator + InteractionSounds.MOD_NAME + File.separator + category + File.separator + fileName);
			try
			{
				FileUtils.writeByteArrayToFile(file, byteFile);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
}
