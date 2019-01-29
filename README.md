# MediaPlayer

A simple media player app for Android. Refer to the
[manifest](https://github.com/rsookram/MediaPlayer/blob/master/app/src/main/AndroidManifest.xml#L22-L51)
for a list of supported formats. MediaPlayer doesn't have an intent filter for
launchers, so it won't appear in your launcher app. It gets launched by other
apps.


## Controls

The controls can be brought up by swiping up. Double-tapping in the middle of
the screen toggles the play / pause state. For media types which support
seeking, double-tapping on the left will rewind by 10 seconds, and
double-tapping on the right will fast forward by 10 seconds.


## Permisssions

### Internet

The internet permission is required to support media streams over http and
https.

### Foreground Service

The foreground service permission is used to keep the app at foreground priority
so that it is less likely to be killed by the system when media is being played
in the background.


## Dependencies

This app is written in Kotlin and uses
[ExoPlayer](https://github.com/google/ExoPlayer) to handle media playback.
AppCompat and ConstraintLayout are used for the UI. And there's also KTX for
some Kotlin niceness.


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

