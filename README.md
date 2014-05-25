Distributable Marketeering Tool
===============================
The landing page for an unofficial, portable, cross-platform Steam Client.  
Still needs some rewriting bits of it to support new features.

We have a [Steam group](http://steamcommunity.com/groups/dmt-client).  It's tiny.

Uses the [SteamKit-Java library](https://github.com/Top-Cat/SteamKit-Java) for Steam connectivity and the [SteamTrade-Java library](https://github.com/nosoop/SteamTrade-Java) for trades.

Note that there are some issues plaguing the outdated SteamKit-Java library right now, so that'll be fun to update.  
I mean, mostly protobufs and stuff, some protocol work.  No idea what to do with those, so it'll be a learning experience, I suppose.

Notable Features
----------------
  * A reworked trading interface.  With a similar-yet-different UI to Steam Trading, similar items are grouped as uniquely as possible (well, they should be, anyways), making it easy to count out that they have a number of metal, a number of crates of a specific series, so on.  Adding items is as simple as selecting something and then repeatedly right-clicking.  (As stackables aren't supported yet, functionality might be changed for this to be even easier following the support.)
  * Multiple copies, multiple logins.
  * Chat logging.  Separated by however many Steam logins you have, chat history is archived by your conversee's Steam id, by date and time.  _Miiiiiiiight_ be a tad bit feature creep, but why not.

Where It Falls Flat
-------------------
  * Group chatting.  If you're looking to get yourself scammed with the "friend as middleman" bait and switch, well, it's not going to work here.
  * Everything else Steam does!  No voice chatting or running games here (maybe some game coordinator stuff to craft items).

Screenshots
-----------
[In-progress screenshots are available as an imgur album here.](http://imgur.com/a/Nv9xH#0)

Builds!
-------
It compiles nicely and can be run standalone, outside of an IDE now!  The preferred IDE is NetBeans, by the way, unless your IDE of choice also supports hand-picked code folding and form files for UI editing.

Outside of the Maven dependencies that'll be grabbed for you, you will also need to download and/or compile copies of [SteamTrade-Java](https://github.com/nosoop/SteamTrade-Java) and [SteamKit-Java, in which you'll have to pick one of the number available](https://github.com/Top-Cat/SteamKit-Java/network).

Just compile and build at this point.  Compiled binaries from that will be available where you'd expect them to be, along with a `lib/` subfolder for the other libraries.

At the moment, you'll want a `users.json` file in the working directory with the following to get past the sign-in prompt:
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
... (yeah, plaintext, gross.) Plus a `sentry_($username).bin` sentry file, also in the working directory to skip past SteamGuard the first time, if you'd like that.  The `machineauth` key in the JSON file is for skipping through the weblogin.  Other methods of getting past signing in will be available soon, hopefully.  The initial client SteamGuard prompt works now, but the weblogin to fully authenticate the client after signing in still does not stick.

Slow Starts
-----------
Requires the support of Java Cryptography Extension Unlimited Strength policy files, assuming you're on the standard JRE.  
The direct download page for them is available [here](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html), and full instructions on how to install them can be viewed [here](http://suhothayan.blogspot.com/2012/05/how-to-install-java-cryptography.html).

In the future, an "easy install" method may be added to mostly? automate the installation of the policy files.

On the To-Do
------------
  * Re-add features including friends list modifications and chat logging.
  * Update to reflect changes in SteamTrade-Java.
  * Add direct support for the steamLogin token to bypass weblogin. (Well, that, and reimplement the old authentication method for those that actually have it working.  It sure as hell doesn't on mine.)
  * Import sentry files from an installed client to skip SteamGuard, using [the JSON conversion library](https://github.com/nosoop/vdf-json-java) to read where the sentryfile is, of course.
