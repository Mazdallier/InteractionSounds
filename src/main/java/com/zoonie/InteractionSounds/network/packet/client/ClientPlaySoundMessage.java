package com.zoonie.InteractionSounds.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.network.packet.server.ServerPlaySoundPacket;

public class ClientPlaySoundMessage implements IMessage
{
	String soundName, identifier;
	int dimensionId;
	int x, y, z;
	float volume;

	public ClientPlaySoundMessage() {
	}

	public ClientPlaySoundMessage(String name, String identifier, int dimensionId, int x, int y, int z, float volume) {
		this.soundName = name;
		this.identifier = identifier;
		this.dimensionId = dimensionId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int nameLength = bytes.readInt();
		char[] nameChars = new char[nameLength];
		for(int i = 0; i < nameLength; i++)
		{
			nameChars[i] = bytes.readChar();
		}
		soundName = String.valueOf(nameChars);

		int identifierLength = bytes.readInt();
		char[] idChars = new char[identifierLength];
		for(int i = 0; i < identifierLength; i++)
		{
			idChars[i] = bytes.readChar();
		}
		identifier = String.valueOf(idChars);

		dimensionId = bytes.readInt();
		x = bytes.readInt();
		y = bytes.readInt();
		z = bytes.readInt();
		volume = bytes.readFloat();

		TargetPoint tp = new TargetPoint(dimensionId, x, y, z, 16);
		NetworkHelper.syncAllPlayerSounds();
		NetworkHelper.sendMessageToAllAround(new ServerPlaySoundPacket(soundName, identifier, x, y, z, volume), tp);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(soundName.length());
		for(char c : soundName.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(identifier.length());
		for(char c : identifier.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(dimensionId);
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);
		bytes.writeFloat(volume);
	}

	public static class Handler implements IMessageHandler<ClientPlaySoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ClientPlaySoundMessage message, MessageContext ctx)
		{
			return null;
		}
	}
}