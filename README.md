ASUC-Android JSON Parsing
=========================

What this includes:
  - Data Controllers
  - Models
  - Parsing Utilities

How to Use
----------
To retrieve data, call: 
```
JSONUtilities.readJSONFromUrl(URL, NAME_OF_JSON_ARRAY, DATA_CONTROLLER);
```
To update UI elements with after retrieving data, fill out the updateUI() method in your corresponding data controller.

So for example, if we're in an Activity, we would call:
```
JSONUtilities.readJSONFromUrl("http://asuc-mobile.herokuapp.com/api/gyms", "gyms", GymController.getInstance(this));
```

IMPORTANT TO NOTE: Data Controllers here are singletons. So we get an instance of them with the static getInstance() method which takes in a parameter, Context (aka, your Activity).
