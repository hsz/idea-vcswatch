VCS watch [![Build Status](https://travis-ci.org/hsz/idea-vcswatch.svg)](https://travis-ci.org/hsz/idea-gitignore) [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SJAU4XWQ584QL) <a href="http://blockchain.info/address/1BUbqKrUBmGGSnMybzGCsJyAWJbh4CcwE1"><img src="https://www.gnu.org/software/octave/images/donate-bitcoin.png" width="100" height="21"/></a>
==================


Introduction
------------

**VCS watch** is a plugin that periodically checks for any changes in the current project's repositories.

*Compiled with Java 1.6*


Features
--------

- Support GIT repositories
- Support SVN repositories
- Support Mercurial repositories

Feature requests
----------------

- Exclude project repositories from watching
- Specify watch interval


Installation
------------

- Using IDE built-in plugin system:
  - <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "VCS watch"</kbd> > <kbd>Install Plugin</kbd>
- Manually:
  - Download the [latest release][latest-release] and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>
  
Restart IDE.


Changelog
---------

## [v0.1](https://github.com/hsz/idea-vcswatch/tree/v0.1) (2015-05-19)

- Support GIT repositories
- Support SVN repositories
- Support Mercurial repositories

[Full Changelog History](./CHANGELOG.md)


Contribution
------------

Check [`CONTRIBUTING.md`](./CONTRIBUTING.md) file.

### Compiling the source code

- Clone `idea-vcswatch` project from https://github.com/hsz/idea-vcswatch.git
- [Configure IntelliJ IDEA Plugin SDK][idea-sdk-configuration]
- Enable required plugins:
  - Plugin DevKit *(bundled)*
- Create *New Project* as a *IntelliJ Platform Plugin* and set *Project location* to the **idea-vcswatch** sources
  - In <kbd>Project settings</kbd> > <kbd>Modules</kbd> section mark:
    - `resources` as *Resources*
    - `src` as *Sources*
    - `tests` as *Test Sources*
    - `.idea` as *Excluded*
    - `out` as *Excluded*
- Add new *Run/Debug configuration*
  - <kbd>+</kbd> <kbd>Add new configuration</kbd> > <kbd>Plugin</kbd>
  - Add `-Didea.is.internal=true` to *VM Options*
  - Remove `-XX:MaxPermSize=250m` from *VM Options*
- Set *Java Compiler* to **1.6**
  - Go to <kbd>Settings<kbd> > <kbd>Compiler</kbd> > <kbd>Java Compiler</kbd> and set *Project bytecode version* to **1.6**

Developed By
------------

[**@hsz** Jakub Chrzanowski][hsz]


**Contributors**

- none


License
-------

Copyright (c) 2015 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

    
[idea-sdk-configuration]: http://confluence.jetbrains.com/display/IntelliJIDEA/Prerequisites
[build-xml]:              ./build.xml
[hsz]:                    http://hsz.mobi
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest