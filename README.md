# DroidStar #

DroidStar is an Android testing tool that generates behavioral
specifications for Android classes that explain how and when their
callbacks occur.

The most important interface for using this tool is the
[`LearningPurpose` abstract class][1] which defines test harnesses for
classes you are interested in.  Examples can be found in the
[`droidstar-experiments`][2] repository.

DroidStar is an implementation of the callback typestate learning
technique described in the upcoming ICSE'18 paper [*DroidStar:
Callback Typestates for Android Classes*][3].


## Quick start ##

To run a DroidStar experiment on a mobile device, clone the
[`droidstar-experiments`][2] repository and follow the instructions in
its [README][4] file.


[1]: https://github.com/cuplv/droidstar/tree/master/src/main/java/edu/colorado/plv/droidstar/LearningPurpose.java
[2]: https://github.com/cuplv/droidstar-experiments
[3]: https://arxiv.org/abs/1701.07842
[4]: https://github.com/cuplv/droidstar-experiments/blob/master/README.md#quick-start
