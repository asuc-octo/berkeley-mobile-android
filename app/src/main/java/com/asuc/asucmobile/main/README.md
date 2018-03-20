# Main Activities Doc 1.0

## Summary

Contains some main screens and activities that the user sees throughout the application lifecycle.

## `Open*Activity.java`

These activities represent the different screens (from the Navigation Drawer) that the user can choose
to display on screen. The `Open*Activity` sets up the layouts and views, and is dynamically populated by
a fragment `*Fragment`, which is similarly named. e.g. `OpenDiningHallActivity` and `OpenCafeActivity` are
jointly controlled by the `FoodFragment`. `OpenLibraryActivity` is controlled by the `LibraryFragment`.

Before usage of `Open*Activity`, we must first set the object which it will display using a `set*` method.
Upon activity start and view inflation from XML, we check if data has loaded using the method `exitIfNoData`.


```
// from LibraryFragment.java
  mLibraryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenLibraryActivity.setLibrary(mAdapter.getItem(i));
                Intent intent = new Intent(getContext(), OpenLibraryActivity.class);
                startActivity(intent);
            }
        });
...

// from OpenLibraryActivity.java
    public static void setLibrary(Library l) {
        library = l;
        Log.d(TAG, library.toString());
    }

    private void exitIfNoData() {
        if (library == null) {
            finish();
        }
    }
```

## `SplashActivity.java`

The splash screen the user sees upon app start. This activity starts and critical services in the background
such as initiating the `BMRetrofitController` instance for all network calls to the back end service.

## `MainActivity.java`

This is horribly named. This activity is started after 'SplashActivity' sleeps. Its main purpose is to
set up the `utilities/NavigationGenerator` that populates the navigation drawer on the left side of the
screen.