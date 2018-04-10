# Berkeley-Mobile-Android

This is the official repository for the Berkeley Mobile Android application.

## What is Berkeley Mobile?

Created in 2012, Berkeley Mobile has acculumated over 20,000 downloads on iOS and Android and currently has about 2,000 Monthly Active Users (MAUs). It has Bear Transit routes, library and gym information, dining hall menus, and campus resources, all in one place for the busy Berkeley student. The mission of Berkeley Mobile is to use cool technology to help students navigate Berkeley.

*Berkeley Mobile is a product of the Associated Students of University of California (ASUC) Office of the Chief Technology Officer (OCTO) and is not affiliated with the University of California, Berkeley or the Regents of the University of California.*

## Documentation

For documentation, check out our [wiki](https://github.com/asuc-octo/asuc-android/wiki).

The application consists of six modules:
* [Controllers](app/src/main/java/com/asuc/asucmobile/controllers): Retrofit configuration and some 
legacy controllers that fetch JSON for map data.
* [Fragments](app/src/main/java/com/asuc/asucmobile/fragments)
* [Open*Activity](app/src/main/java/com/asuc/asucmobile/main): the main screens 
* [Models](app/src/main/java/com/asuc/asucmobile/models): data models including POJO for Retrofit
* [Responses](app/src/main/java/com/asuc/asucmobile/models/responses): POJO for Retrofit responses with deeper
structure
* [Utilities](app/src/main/java/com/asuc/asucmobile/utilities): some misc. utilities, most are deprecated

## Getting set up

It is recommended you use Android Studio 3.0.0.

### APIs

* Get a Google API key through Google Console, enable the Google Maps and Google Places APIs, and paste it in [secret.xml](app/src/main/res/values/secret.xml)
* We've configured this repository so that the application pulls from our sandbox backend, which just returns test data. If you would like access to our production backend API, please [contact us](#contact). 

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for more information.

## <a name="contact"></a> Contact

You can contact the Berkeley Mobile team at octo.berkeleymobile@asuc.org with any questions.

## License

This project uses the Educational Community License (ECL) 2.0. See [LICENSE.md] for more information.
