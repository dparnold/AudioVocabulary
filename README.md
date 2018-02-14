# AudioVocabulary

## Motivation
It takes a lot of effort and time to learn a new language.
And in our busy lives we have so much to do that there is
only so little time to study vocabulary.
BUT there are commutes, the nightly hours in which we clean
our apartment or do our laundry.
During this time, we cannot focus on our phone.
BUT we can listen to audio files that teach us vocabulary.
The problem with common audio CDs for vocabulary learning is
that the user cannot exclude the words he or she has already memorized and has
to listen to them over and over again... even in the same order.

Audiovocabulary has one goal:

Enable the user to study vocabulary as effective as possible
during the many hours when it is not possible to fully focus.

## System
- The user loads a so-called package, which contains the
vocabulary and the audio files. These are put to a SQL database.
- In the main activity is a play button which starts the
playback of the n most relevant vocables.
- The user can set many options, such as the number of words
that are repeatedly played, the pauses between the word in the user's native
language and the new word, when a word is considered memorized, at what intervals
this should be checked ...
- Every now and then the user can update the vocabulary
files he or she is listening to by taking a short quiz. This eliminates
memorized vocabulary and adds fresh words to the active list.


