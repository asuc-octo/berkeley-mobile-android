# Models Doc 1.0

## Summary

Contains Java object models for important data classes. Those that are pulled and populated from the
back end must use proper Retrofit annotation syntax, specifying serialized names:

```
    @SerializedName("breakfast_menu")
    private ArrayList<FoodItem> breakfastMenu;

    @SerializedName("breakfast_open")
    private Date breakfastOpen;

    @SerializedName("breakfast_close")
    private Date breakfastClose;

```

## `FoodPlace.java`

Not too creative (Rustie), so both `DiningHall`s and `Cafe`s extend `FoodPlace`. The syntax for using
the related `adapters/FoodPlaceAdapter` uses enums:

```
    public enum FoodType {
        DiningHall,
        Cafe
    }
    ...
    public FoodPlaceAdapter(Context context, List<FoodPlace> foodPlaces, FoodType type) {
        mFoodPlaceList = foodPlaces;
        mContext = context;
        foodType = type;
    }

```

## `/responses`

Contain POJO responses from the backend, used in conjunction with Retrofit.