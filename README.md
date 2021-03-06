# Unbound Jobs
Unbound Jobs is a job plugin for Spigot servers. It was originally started as a plugin specifically built for the Tyrengard server to replace Jobs Reborn by Zrips and McMMO by Nossr50, but we decided to release it to the public as open-source.

## Features
--- under construction ---

## Supported versions
Currently, Unbound Jobs only supports Minecraft 1.18 and above. If enough requests for supporting lower versions are given, we may retrofit the code to support that version. You can also submit pull requests if you manage to retrofit the code yourself!

## Installation and configuration
You can get the latest version of the plugin via the latest release link on the right, and place it in your `/plugins` folder. After starting your server, you can add job config files inside the plugin's data folder. Then just reload the job configs by running `/unbound-jobs-admin jobs reload` as an op, and you're ready to start testing your new jobs!

## Wiki
Click [here](https://github.com/Team-Tyrengard/Unbound-Jobs/wiki) to visit the wiki.

## Translations
Translations will be supported soon!

## API and integration
Unbound Jobs also has an API with allows you to:
* Add your own [actions](https://github.com/Team-Tyrengard/Unbound-Jobs/wiki/Actions) for job and job quest tasks,
* Add your own [job quest rewards](https://github.com/Team-Tyrengard/Unbound-Jobs/wiki) specific to your plugin, and
* Listen for [events](https://github.com/Team-Tyrengard/Unbound-Jobs/wiki) such as quests being done or levelling up

Simply add the plugin as a dependency via Maven:

```xml
<dependencies>
    ...
    <dependency>
      <groupId>com.tyrengard.unbound</groupId>
      <artifactId>unbound-jobs</artifactId>
      <version>1.0</version>
      <scope>provided</scope>
    </dependency>
    ...
</dependencies>
```
To use SNAPSHOT versions, add the Sonatype Snapshots repository:
```xml
<repositories>
    ...
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    ...
</repositories>
```
If you add your own actions or job quest rewards, don't forget to [let us know](https://github.com/Team-Tyrengard/Unbound-Jobs/issues/new?labels=integration), so we can add integration for it!

## Demo
--- under construction ---

## Community
You can join the [Team Tyrengard Discord server](https://discord.gg/4Zct7WmYUD) to ask us questions, get help, report bugs, or request features! You can also click [here](https://github.com/Team-Tyrengard/Unbound-Jobs/issues/new/choose) to submit a new issue.