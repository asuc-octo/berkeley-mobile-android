#ASUC-Android

##Testing
To run unit tests, run
```
Test.runTests();
```
in your first Activity. Results will be displayed on your logcat.

##Library
###How to Use
To start the Library activity and get a list of libraries, simply call
```
Intent intent = new Intent(this, LibraryActivity.class);
startActivity(intent);
```
in your Activity.

##JSON Parsing
###Available Controllers
  - DiningController
  - GymController
  - LibraryController

###How to Use
To retrieve data, call the refreshInBackground() method of a controller: 
```
Controller.getInstance(activity).refreshInBackground(callback);
```

To update UI elements with after retrieving data, implement the onDataRetrieved() method of your Callback.

So for example, if we're in an Activity, we would call:
```
DiningController.getInstance(this).refreshInBackground(new Callback() {
    
    @Override
    public void onDataRetrieved(Object data) {
      (ArrayList) diningHalls = (ArrayList) data;
      // Do something with diningHalls.
    }
    
    @Override
    public void onDataRetrievalFailed() {
      // Let the user know the app wasn't able to connect.
    }
    
});
```

IMPORTANT TO NOTE: Data Controllers here are singletons. So we get an instance of them with the static getInstance() method which takes in a parameter, Context (aka, your Activity).
