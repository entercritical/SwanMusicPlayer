SwanMusicPlayer
===============

Simple Android Music Player

- Has at least two Activities, one that shows the list of songs with their name and author, and one shows the player with control buttons.
	
	MusicListActivity.java -> Music List
	MusicPlayActivity.java -> Music Player
	
- These two activities should be opened by one another via Android Intent

- Can play in background using Android Service when the application is not open 
	
	MusicPlayService.java
	
- Use Android¡¯s MusicStore Content Provider to retrieve the list of all songs in device storage
	
	MusicList.java
	
- Use an Android BroadcastReceiver to stop the music when it receives any broadcast intent which you defined.
	
	MusicPlayService.java 
		BroadcastReceiver : MusicPlayReceiver (Headset unplug)