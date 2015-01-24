package com.zoonie.InteractionSounds.handler;

import java.util.HashMap;
import java.util.Map;

import com.zoonie.InteractionSounds.sound.SoundPlayInfo;

public class DelayedPlayHandler
{
	private static Map<String, SoundPlayInfo> map = new HashMap<String, SoundPlayInfo>();

	public static void addDelayedPlay(String soundName, String identifier, int x, int y, int z, float volume)
	{
		map.put(soundName, new SoundPlayInfo(identifier, x, y, z, volume));
	}

	public static void onSoundReceived(String soundName)
	{
		SoundPlayInfo info = map.get(soundName);
		if(info != null)
		{
			SoundHandler.playSound(soundName, info.identifier, info.x, info.y, info.z, info.volume);
			map.remove(soundName);
		}
	}

	public static void removeSound(String soundName)
	{
		map.remove(soundName);
	}
}
