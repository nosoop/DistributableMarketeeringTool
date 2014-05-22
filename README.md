Distributable Marketeering Tool
===============================

The landing page for an unofficial, portable, cross-platform Steam Client.  
Still needs some good code cleanup and rewriting bits of it to support new features.

We have a [Steam group](http://steamcommunity.com/groups/dmt-client).  It's tiny.

Currently in prerelease format as pulled out from my working files -- after compiling, you'll want a `users.json` file in the working directory with the following to get past the sign-in prompt:
```
{
    "clients": [
        {
            "username": "$username",
            "password": "$password",
            "machineauth": "$steamMachineAuthCookieValueFromWebLogin"
        }
    ]
}
```
... (yeah, plaintext, gross.) Plus a `sentry_($username).bin` sentry file, also in the working directory to skip past SteamGuard.  You'll need to, as the dialog for it hasn't been completely fixed yet.  (As if I ever wanted to test for SteamGuard functionality again...)

Uses the [SteamKit-Java library](https://github.com/Top-Cat/SteamKit-Java) for Steam connectivity and the [SteamTrade-Java library](https://github.com/nosoop/SteamTrade-Java) for trades.

Note that there are some issues plaguing the outdated SteamKit-Java library right now, so that'll be fun to update.  
I mean, mostly protobufs and stuff, some protocol work.  No idea what to do with those, so it'll be a learning experience, I suppose.


Notable Features
----------------
  * A reworked trading interface.  With a similar-yet-different UI to Steam Trading, similar items are grouped as uniquely as possible (well, they should be, anyways), making it easy to count out that they have a number of metal, a number of crates of a specific series, so on.  Adding items is as simple as clicking on a stack and right-clicking, too.  (As stackables aren't supported yet, functionality might be change for this to be even easier following the support.)
  * Multiple copies, multiple logins.
  * Chat logging.  Separated by however many Steam logins you have, chat history is archived by your conversee's Steam id, by date and time.  _Miiiiiiiight_ be a tad bit feature creep, but why not.


Screenshots
-----------

[In-progress screenshots are available as an imgur album here.](http://imgur.com/a/Nv9xH#0)


Slow Starts
===========

Requires the support of Java Cryptography Extension Unlimited Strength policy files.    
The direct download page for them is available [here](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html), and full instructions on how to install them can be viewed [here](http://suhothayan.blogspot.com/2012/05/how-to-install-java-cryptography.html).

In the future, an "easy install" method may be added to mostly? automate the installation of the policy files.


On the To-Do
============

  * Clean up all this spaghetti, namely:
    * Merge the UI with the client-handling classes.
    * Do some maintainance work on the connectivity (following the previous bullet point).  Flexibility is desired.
    * Update to reflect changes in SteamTrade-Java, which, of course, follows the previous point on flexibility.
  * Add direct support for the steamLogin token to bypass weblogin. (Well, that, and reimplement the old authentication method for those that actually have it working.  It sure as hell doesn't on mine.)
  * Import sentry files from an installed client to skip SteamGuard, using [the JSON conversion library](https://github.com/nosoop/vdf-json-java) to read where the sentryfile is, of course.
