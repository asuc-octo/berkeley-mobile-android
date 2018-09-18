# Berkeley-Mobile-Android

This is the official repository for the Berkeley Mobile Android application.

## What is Berkeley Mobile?

Created in 2012, Berkeley Mobile has acculumated over 20,000 downloads on iOS and Android and currently has about 2,000 Monthly Active Users (MAUs). It has Bear Transit routes, library and gym information, dining hall menus, and campus resources, all in one place for the busy Berkeley student. The mission of Berkeley Mobile is to use cool technology to help students navigate Berkeley. You can download Berkeley Mobile on [the Google Play Store](https://play.google.com/store/apps/details?id=com.asuc.asucmobile&hl=en_US) or [the App Store](https://itunes.apple.com/us/app/berkeley-mobile/id912243518?mt=8).

<p align="center">
  <img src="/app_preview_images/screen1.png" width="200"/>
  <img src="/app_preview_images/screen2.png" width="200"/>
  <img src="/app_preview_images/screen3.png" width="200"/>
  <img src="/app_preview_images/screen4.png" width="200"/>
</p>

*Berkeley Mobile is a product of the Associated Students of University of California (ASUC) Office of the Chief Technology Officer (OCTO) and is not affiliated with the University of California, Berkeley or the Regents of the University of California.*

## Documentation

For documentation, check out our [wiki](https://github.com/asuc-octo/asuc-android/wiki).

The application consists of six modules:
* [Controllers](app/src/main/java/com/asuc/asucmobile/controllers): Retrofit configuration and some 
legacy controllers that fetch JSON for map data.
* [Fragments](app/src/main/java/com/asuc/asucmobile/fragments): fragments populating various UI elements
* [Activity](app/src/main/java/com/asuc/asucmobile/main): the main screens (e.g. `Open*Activity.java`) 
* [Models](app/src/main/java/com/asuc/asucmobile/models): data models including POJO for Retrofit
* [Responses](app/src/main/java/com/asuc/asucmobile/models/responses): POJO for Retrofit responses with deeper
structure
* [Utilities](app/src/main/java/com/asuc/asucmobile/utilities): some misc. utilities, most are deprecated

## Getting set up

It is recommended you use Android Studio 3.0.0.

### APIs

* Create a new project in the [Google Cloud Console](https://console.cloud.google.com)
  * Under the APIs and Services tab, enable the Google Places API for Android, Google Maps Directions API, Google Maps Android API, and Google Maps Geocoding API
  * Under the Credentials tab, create an API key
  * Paste your API key into [secret.xml](app/src/main/res/values/secret.xml) and [google-services.json](app/google-services.json)
* We've configured this repository so that the application pulls from our sandbox backend, which just returns test data. If you would like access to our production backend API, please [contact us](#contact). 

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for more information.

## <a name="todo"></a> TODO

The following is a summary of open tasks and issues. For new contributors, we recommend focusing on Improvements. **Before starting to work on new features or large changes not listed below, especially ones that involve UI changes or new APIs, please [contact us](#contact)!**

### What the Berkeley Mobile team is working on

* Crowd sourcing water fountain, microwave, and nap pod locations

### Improvements

* Write tests! We just started working with Bitrise to automate testing and deployment & notifications
* Stop hard coding everything
* Standardization of horizontally scrolling RecyclerViews with CardViews (e.g. in Food and Gyms screens)
* Porting old Bear Transit network controllers to Retrofit 2, see the [BMAPI.java](app/src/main/java/com/asuc/asucmobile/controllers/BMAPI.java)

### Feature Ideas

* Indicator after favorite/heart-ing an item
* Customizable splash screen featuring user settings and favorites
* We might be open to partnerships with student orgs

## <a name="contact"></a> Contact

You can contact the Berkeley Mobile team at octo.berkeleymobile@asuc.org with any questions.

## License

This project uses the Educational Community License (ECL) 2.0. See [LICENSE.md](LICENSE.md) for more information.
