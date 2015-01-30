package com.zoonie.InteractionSounds.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.io.Files;
import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.helper.SoundHelper;
import com.zoonie.InteractionSounds.network.packet.client.CheckPresencePacket;
import com.zoonie.InteractionSounds.network.packet.server.SoundRemovedPacket;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class SoundHandler
{
	private static File soundsFolder;
	private static ArrayList<Sound> sounds;

	public static File getSoundsFolder()
	{
		if(soundsFolder == null)
		{
			findSounds();
		}
		return soundsFolder;
	}

	public static ArrayList<Sound> getSounds()
	{
		if(sounds == null)
		{
			findSounds();
		}
		return sounds;
	}

	public static ArrayList<Sound> getLocalSounds()
	{
		ArrayList<Sound> localSounds = new ArrayList<Sound>();
		for(Sound sound : getSounds())
		{
			if(sound.hasLocal())
				localSounds.add(sound);
		}
		return localSounds;
	}

	public static ArrayList<Sound> getRemoteSounds()
	{
		ArrayList<Sound> remoteSounds = new ArrayList<Sound>();
		for(Sound sound : getSounds())
		{
			if(sound.hasRemote())
				remoteSounds.add(sound);
		}
		return remoteSounds;
	}

	public static Sound getSound(String fileName, String category)
	{
		for(Sound sound : getSounds())
		{
			if(sound.getSoundName().equals(fileName) && sound.getCategory().equals(category))
			{
				return sound;
			}
		}
		return null;
	}

	public static void findSounds()
	{
		soundsFolder = new File("sounds");
		if(!soundsFolder.exists())
		{
			soundsFolder.mkdir();
		}
		sounds = new ArrayList<Sound>();
		addSoundsFromDir(soundsFolder);
	}

	public static void removeSound(Sound sound)
	{
		if(sound != null)
		{
			if(!sound.getSoundLocation().delete())
			{
				sound.getSoundLocation().deleteOnExit();
			}
			sounds.remove(sound);
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			{
				NetworkHelper.sendMessageToAll(new SoundRemovedPacket(sound.getSoundName()));
			}
		}
	}

	private static void addSoundsFromDir(File dir)
	{
		for(File file : dir.listFiles())
		{
			if(file.isFile())
			{
				if(file.getName().endsWith(".ogg") || file.getName().endsWith(".wav") || file.getName().endsWith(".mp3"))
				{
					sounds.add(new Sound(file));
				}
			}
			else if(file.isDirectory())
			{
				addSoundsFromDir(file);
			}
		}
	}

	public static void addRemoteSound(String soundName, String remoteCategory)
	{
		Sound sound = getSound(soundName, remoteCategory);
		if(sound != null)
		{
			if(sound.hasLocal())
			{
				sound.onSoundUploaded();
			}
		}
		else
		{
			sounds.add(new Sound(soundName, remoteCategory));
		}
	}

	public static void addLocalSound(String soundName, String category, File soundFile)
	{
		Sound sound = getSound(soundName, category);
		if(sound != null)
		{
			if(sound.getState() != Sound.SoundState.SYNCED)
			{
				sound.onSoundDownloaded(soundFile);
			}
		}
		else
		{
			sounds.add(new Sound(soundFile));
		}
	}

	public static void remoteRemovedSound(Sound sound)
	{
		if(!sound.hasLocal())
		{
			sounds.remove(sound);
		}
		else
		{
			sound.setState(Sound.SoundState.LOCAL_ONLY);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(String soundName, String category, String identifier, int x, int y, int z, float volume)
	{

		Sound sound = SoundHandler.getSound(soundName, category);
		if(sound != null)
		{
			if(sound.hasLocal())
			{
				SoundPlayer.playSound(sound.getSoundLocation(), identifier, x, y, z, true, volume);
			}
			else if(sound.getState() != Sound.SoundState.DOWNLOADING)
			{
				sound.setState(Sound.SoundState.DOWNLOADING);
				DelayedPlayHandler.addDelayedPlay(soundName, category, identifier, x, y, z, volume);
				ChannelHandler.network.sendToServer(new CheckPresencePacket(soundName, category));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static Sound setupSound(File file)
	{
		File category = new File("sounds" + File.separator + "Interaction Sounds" + File.separator + Minecraft.getMinecraft().thePlayer.getDisplayNameString());
		if(!category.exists())
		{
			category.mkdirs();
		}

		File newFile = new File(category.getAbsolutePath() + File.separator + file.getName());
		try
		{
			// TODO: FIXXXX
			if((!newFile.exists() || !Files.equal(file, newFile)) && !SoundHelper.isSoundInSoundsFolder(file))
			{
				Files.copy(file, newFile);
				findSounds();
			}
			else
			{
				Sound sound = new Sound(file);
			}
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		return new Sound(newFile);
	}

	public static Sound getSoundByName(String name)
	{
		for(Sound sound : sounds)
		{
			if(sound.getSoundName().equals(name))
				return sound;
		}
		return null;
	}
}
