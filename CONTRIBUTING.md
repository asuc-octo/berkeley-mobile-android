# How to contribute to Berkeley Mobile

Never made an open source contribution before? Wondering how contributions work in our project? Here's a quick rundown!

## Starting development

1. Choose an issue that you are interested in addressing or a feature that you would like to add. **See [TODOs](#todo) to increase the chance of getting your contribution accepted.**
2. Fork the repository. This means that you will have a copy of the repository under `github-username/repository-name`.
3. Clone the repository to your local machine using `git clone https://github.com/github-username/repository-name.git`.
4. Create a new branch for the change using `git checkout -b branch-name`.
5. Make changes to your fork until you are ready to submit. Make sure to commit regularly to save and describe your work. Here are some useful commands:
* Use `git add changed-file-paths` to include the file in your patch.
* Use `git commit -m "A short message describing your changes"` to save your work and describe the changes.

## Submitting changes

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

The following is a summary of open tasks and issues. For new contributors, we recommend focusing on Improvements. **Before starting to work on new features or large changes not listed below, especially ones that involve UI changes or new APIs, please [contact us](#contact)!**

### What the Berkeley Mobile team is working on

* Crowd sourcing water fountain, microwave, and nap pod locations

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

You can contact the Berkeley Mobile team at octo.berkeleymobile@asuc.org.
