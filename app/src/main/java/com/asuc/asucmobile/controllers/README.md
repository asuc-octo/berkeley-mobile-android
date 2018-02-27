# Controllers Doc 1.0

## Summary

Contains some legacy data controllers that GET and manually parse JSON from our backend service. Phased
out in favor of Retrofit for:
* maintenance
* scalability
* caching and data flow
* built in POJO support

## `BMRetrofitController`

A singleton that initiates and executes all calls to RESTful API at the specified URL. Defines all
`okhttp`, `okhttp` caching, `gson`, and `retrofit2` settings. Some specifications:
* 10 MiB cache
* 60 second max age for data when connection is available
* 24 hour max for stale cached data
* defines request interceptors to add request headers

The `BMRetrofitController` is instantiated in the `SplashActivity` to lengthen the amount of time the
service has to start up all network dependencies. We also aren't caching much, so we can store everything
in the same cache rooted at the first activity the user loads.

## `BMAPI`

The Berkeley Mobile (back end) API. Defines all the endpoints we can call using Retrofit-defined
annotation syntax. Responses are received in POJO format after automated parsing. They should be populated
first into `models/responses` objects, and then into `models` objects, since our back end provides everything
in a JSON array.

e.g. a response from `/dining_halls` is received as an array `{"dining_halls": [...]}`, and as such, it
is first populated into a `models/responses/DiningHallResponse` object that contains an array of
`DiningHall` objects:

```
public class DiningHallsResponse {

    @SerializedName("dining_halls")
    private ArrayList<DiningHall> diningHalls;

    public ArrayList<? extends FoodPlace> getDiningHalls() {
        return diningHalls;
    }

}
```

The `DiningHall` objects should also be read as POJO:

```

public class DiningHall extends FoodPlace{


    @SerializedName("breakfast_menu")
    private ArrayList<FoodItem> breakfastMenu;

    @SerializedName("lunch_menu")
    private ArrayList<FoodItem> lunchMenu;

    @SerializedName("dinner_menu")
    private ArrayList<FoodItem> dinnerMenu;

    @SerializedName("limited_lunch_menu")
    private ArrayList<FoodItem> limitedLunchMenu;

    @SerializedName("limited_dinner_menu")
    private ArrayList<FoodItem> limitedDinnerMenu;

    @SerializedName("breakfast_open")
    private Date breakfastOpen;

    ...

```

