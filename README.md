Distributable Marketeering Tool
===============================

The landing page for an unofficial, portable, cross-platform Steam Client.  
Still needs some good code cleanup and rewriting bits of it to support new features.

Currently in prerelease format as pulled out from my working files -- if you somehow manage to get it compiled and running, you'll want a `users.json` file in the working directory with the following to get past the sign-in prompt:
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
... (yeah, plaintext, gross.) Plus a `sentry_($username).bin` sentry file, also in the working directory to skip past SteamGuard.  You'll need to, as the dialog for it hasn't been completely fixed yet.

Uses the [SteamKit-Java library](https://github.com/Top-Cat/SteamKit-Java) for Steam connectivity and the [SteamTrade-Java library](https://github.com/nosoop/SteamTrade-Java) for trades.


Slow Starts
===========

Requires the support of Java Cryptography Extension Unlimited Strength policy files.  And Bouncy Castle for encryption.  
The direct download page for them is available [here](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html), and full instructions on how to install them can be viewed [here](http://suhothayan.blogspot.com/2012/05/how-to-install-java-cryptography.html).

In the future, an "easy install" method may be added to mostly? automate the installation of the policy files.


On the To-Do
============

  * Clean up all this spaghetti.
  * Add direct support for the steamLogin token to bypass weblogin. (Well, that, and reimplement the old authentication method for those that actually have it working.)
  * Import sentry files from an installed client to skip SteamGuard, with [the JSON conversion library](https://github.com/nosoop/vdf-json-java), of course.
