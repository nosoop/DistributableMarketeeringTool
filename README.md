Distributable Marketeering Tool
===============================

The landing page for an unofficial, portable, cross-platform Steam Client.  
Still needs some good code cleanup and rewriting bits of it to support new features.

Currently in prerelease format -- if you somehow manage to get it up and running, you'll want a `users.json` file in the working directory with the following to get past the sign-in prompt, plus a `sentry_($username).bin` sentry file if you want to skip past SteamGuard:
```
{
    "clients": [
        {
            "username": "$username",
            "password": "$password",
            "machineauth": "$steamMachineAuthCookieValueFromWebLogin",
        }
    ]
}
```

Uses the [SteamKit-Java library](https://github.com/Top-Cat/SteamKit-Java) for Steam connectivity and the [SteamTrade-Java library](https://github.com/nosoop/SteamTrade-Java) for trades.

Requires the support of Java Cryptography Extension Unlimited Strength policy files.  And Bouncy Castle for encryption.  
The direct download page for them is available [here](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html), and full instructions on how to install them can be viewed [here](http://suhothayan.blogspot.com/2012/05/how-to-install-java-cryptography.html).

In the future, an "easy install" method may be added to mostly? automate the installation of the policy files.
