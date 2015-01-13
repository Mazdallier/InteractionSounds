package com.zoonie.InteractionSounds.sound;

import java.io.File;

public class Sound
{
    private File soundLocation;
	private String soundName;
    private String category;
    private String remoteCategory;
    private SoundState state;

    public Sound(File soundLocation)
    {
        this.soundLocation = soundLocation;
        String path = soundLocation.getAbsolutePath();
        path = path.substring(0, path.lastIndexOf(File.separator));
        path = path.substring(path.lastIndexOf(File.separator) + 1);
        if (path.equals("sounds"))
            path = "";
        this.category = path;
        this.soundName = soundLocation.getName();
        this.state = SoundState.LOCAL_ONLY;
    }

    public Sound(String soundName, String category)
    {
        this.soundLocation = null;
        this.soundName = soundName;
        this.remoteCategory = category;
        this.state = SoundState.REMOTE_ONLY;
    }

    public String getCategory()
    {
        return category;
    }

    public String getRemoteCategory()
    {
        return remoteCategory;
    }

    public String getSoundName()
    {
        return soundName;
    }

    public File getSoundLocation()
    {
        return soundLocation;
    }

    public SoundState getState()
    {
        return state;
    }

    public void onSoundUploaded(String remoteCategory)
    {
        this.remoteCategory = remoteCategory;
        this.state = SoundState.SYNCED;
    }

    public void onSoundDownloaded(File soundFile)
    {
        this.soundLocation = soundFile;
        String path = soundLocation.getAbsolutePath();
        path = path.substring(0, path.lastIndexOf(File.separator));
        path = path.substring(path.lastIndexOf(File.separator) + 1);
        if (path.equals("sounds"))
            path = "";
        this.category = path;
        this.state = SoundState.SYNCED;
    }

    public boolean hasRemote()
    {
        return this.state == SoundState.SYNCED || this.state == SoundState.REMOTE_ONLY || this.state == SoundState.DOWNLOADING;
    }

    public boolean hasLocal()
    {
        return this.state == SoundState.SYNCED || this.state == SoundState.LOCAL_ONLY || this.state == SoundState.UPLOADING;
    }
    
    public boolean saved()
    {
    	return SoundHandler.getLocalSounds().contains(this);
    }

    public void setState(SoundState state)
    {
        this.state = state;
    }

    public static enum SoundState
    {
        LOCAL_ONLY,
        REMOTE_ONLY,
        SYNCED,
        DOWNLOADING,
        UPLOADING
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((soundName == null) ? 0 : soundName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sound other = (Sound) obj;
		if (category == null)
		{
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (soundName == null)
		{
			if (other.soundName != null)
				return false;
		} else if (!soundName.equals(other.soundName))
			return false;
		return true;
	}
}