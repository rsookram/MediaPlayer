# MediaPlayer

A simple media player app for Android. Refer to the
[manifest](https://github.com/rsookram/MediaPlayer/blob/master/app/src/main/AndroidManifest.xml#L20-L37)
for a list of supported formats. MediaPlayer doesn't have an intent filter for
launchers, so it won't appear in your launcher app. It gets launched by other
apps.


## Controls

The controls can be brought up by swiping up. Long pressing in the middle of
the screen toggles the play / pause state. For media types which support
seeking, long pressing on the left will rewind by 10 seconds, and long pressing
on the right will fast forward by 10 seconds.


## Permissions

### Internet

The internet permission is required to support media streams over http and
https.


## Build

Run the following command from the root of the repository to make a debug
build:

```shell
./gradlew assembleDebug
```

Making a release build is similar, but requires environment variables to be set
to indicate how to sign the APK:

```shell
MEDIA_STORE_FILE='...' MEDIA_STORE_PASSWORD='...' MEDIA_KEY_ALIAS='...' MEDIA_KEY_PASSWORD='...' ./gradlew assembleRelease
```

## Dependencies

This app is written in Kotlin and uses
[ExoPlayer](https://github.com/google/ExoPlayer) to handle media playback.


License
-------

    Copyright 2019 Rashad Sookram

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

