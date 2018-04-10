# Berkeley-Mobile-Android

This is the official repository for the Berkeley Mobile Android application.

## What is Berkeley Mobile?

Created in 2012, Berkeley Mobile has acculumated over 20,000 downloads on iOS and Android and currently has about 2,000 Monthly Active Users (MAUs).
It has Bear Transit routes, library and gym information, dining hall menus, and campus resources, all in one place for the busy Berkeley student.

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

## How to contribute

Never made an open source contribution before? Wondering how contributions work in our project? Here's a quick rundown!

### Starting development

1. Choose an issue that you are interested in addressing or a feature that you would like to add. **See [TODOs](#todo) to increase the chance of getting your contribution accepted.**
2. Fork the repository. This means that you will have a copy of the repository under `github-username/repository-name`.
3. Clone the repository to your local machine using `git clone https://github.com/github-username/repository-name.git`.
4. Create a new branch for the change using `git checkout -b branch-name`.
5. Make changes to your fork until you are ready to submit. Make sure to commit regularly to save and describe your work. Here are some useful commands:
* Use `git add changed-file-paths` to include the file in your patch.
* Use `git commit -m "A short message describing your changes"` to save your work and describe the changes.

Before the size of your change gets too big, make a pull request to have your changes reflected in the app!

### Submitting changes

1. Push your changes to the remote repository using `git push origin branch-name`.
2. Send a pull request from your fork's branch to our master branch
3. Title the pull request with a short description of the changes made and the issue or bug number associated with your change.
* For example, one issue might be called "Added more log outputting to resolve #4352".
4. In the description of the pull request, explain the changes that you made, any issues you think exist with the pull request you made, and any questions you have for the maintainer.
5. Wait for the pull request to be reviewed by us.
6. Make changes to the pull request if the we recommend them.

Congrats! You’ve helped to make Berkeley Mobile better!

*Keep in mind Berkeley Mobile reserves the right to reject pull requests if they don't meet our standards or are not consistent with Berkeley Mobile's mission, values, and/or long term plans.*

## <a name="todo"></a> TODO

The following is a summary of open tasks and issues. For new contributors, we recommend focusing on Improvements. **Before starting to work on new features or large changes, especially ones that involve UI changes, please [contact us](#contact)!**

### What we're working on

* Crowd sourcing water fountain, microwave, and nap pod locations
* Adding pools and tracks 
* Push notifications

### Improvements

* Write tests!
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

This project uses the Educational Community License (ECL) 2.0.
