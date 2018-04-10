# ASUC-Android

Berkeley Mobile Android open source! Made with love.

## Build

Require Android Studio 3.0.0

## APIs

* Get your own Google API key through Google Console and paste it in [secret.xml](app/src/main/res/values/secret.xml)
    * Enable Google Maps and Google Places
* Configure Berkeley Mobile backend in [ServerUtils.java](app/src/main/java/com/asuc/asucmobile/utilities/ServerUtils.java)
    * Default configuration is our dummy, which just returns test data

## Documentation

For documentation, check out our [wiki](https://github.com/asuc-octo/asuc-android/wiki)

## Contributing Workflow

Here's how we suggest you go about proposing a change to this project
1. Fork this project to your account
2. Create a branch for the change you intend to make (see TODO below for some ideas)
3. Make your changes to your fork
4. Send a pull request from your fork's branch to our master branch

Using the web-based interface to make changes is fine too, and will help you by automatically forking 
the project and prompting to send a pull request too.

We will review your code and merge it in if we see that it's solid.

## What the team is working on
* Crowd sourcing water fountain, microwave, and nap pod locations
* Adding pools and tracks 
* Push notifications

## TODO public
Not too urgent, but would be nice to have:
* Moving all documentation to a Wiki
* Write more tests!!!
* Standardization of horizontally scrolling RecyclerViews with CardViews (e.g. in Food and Gyms screens)
* Porting old Bear Transit network controllers to Retrofit 2, see the [BMAPI.java](app/src/main/java/com/asuc/asucmobile/controllers/BMAPI.java)
* Some sort of indicator (toast maybe) after favorite/heart-ing an item
* Customizable splash screen featuring user settings and favorites
* Stop hard coding everything >:\(
* If you're a student org and want to do a partnership, feel free to submit a PR with some stuff you want to implement.

