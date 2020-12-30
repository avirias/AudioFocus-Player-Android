
# AudioFocus-Player-Android
[ ![Download](https://api.bintray.com/packages/avirias/audiofocus/com.avirias.audiofocus/images/download.svg)](https://bintray.com/avirias/audiofocus/com.avirias.audiofocus/_latestVersion)


A Wrapper over the Android Media Player Library that Plays a Media with Audio focus

### Overview of Audio Focus Player library
* Audio Focus player can be used to play any Media with [Audio Focus](https://developer.android.com/guide/topics/media-apps/audio-focus)
* This library can be used to play a media with a Uri
* Supports android versions above Android Oreo 8.0 ( API level 26 )
* This library has a simple interface just Like [Media Player](https://developer.android.com/reference/android/media/MediaPlayer) API in android 

## Using Audio Focus Player Library in your Android application

Add this in your build.gradle
```groovy
implementation 'com.avirias.audiofocus:audiofocus:1.0.0'
```
Then initialize it in onCreate() Method of Activity or Fragment :
```java
AudioFocusPlayer audioFocusPlayer = new AudioFocusPlayer(context);
```
Initializing it with some customization
```java
// Set a Datasource 
audioFocusPlayer.setDataSource(yourMediaUri);
```

Play or Pause it 
```java
// Play the Media 
audioFocusPlayer.play();

// Pause the Media 
audioFocusPlayer.pause();



