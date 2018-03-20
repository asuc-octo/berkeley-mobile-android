# Adapters Doc 1.0

## Summary
Contains adapters for all our scrollable views, for dynamic population of data models. These classes 
also contain view holders for their associated UI elements, but will probably separate this out in the 
future for more clarity.

Here are some standard adapters and how they are implemented.

## `FoodAdapter.java`

Used in `MenuFragment` as the adapter of `FoodItem`s for each Cafe or Dining Hall's (`FoodPlace`) 
menu items. Number of `FoodAdapter`s is equal to the number of menus a `FoodPlace`. 

Switches on if Cafe or Dining Hall, based on constructor arguments. Then sets proper UI elements:
* Dining Hall food items have food types 
* Cafe food items have prices

Also includes favorite button, which resorts list of food items on click.

```java

imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfFavorites != null && listOfFavorites.contains(foodItem.getName())) {
                    listOfFavorites.remove(foodItem.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.pre_favorite);

                } else if (listOfFavorites != null) {
                    listOfFavorites.add(foodItem.getName());
                    SerializableUtilities.saveObject(context, listOfFavorites);
                    imageView.setImageResource(R.drawable.post_favorite);

                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(FoodAdapter.this.context);
                    Bundle bundle = new Bundle();
                    bundle.putString("food_item", foodItem.getName());
                    mFirebaseAnalytics.logEvent("favorited_food_item", bundle);
                }

                if (foodType == FoodPlaceAdapter.FoodType.Cafe) {
                    Collections.sort(foodItems, CustomComparators.FacilityComparators.getFoodSortByFavorite(OpenCafeActivity.selfReference));
                } else if (foodType == FoodPlaceAdapter.FoodType.DiningHall) {
                    Collections.sort(foodItems, CustomComparators.FacilityComparators.getFoodSortByFavorite(OpenDiningHallActivity.selfReference));
                }

                MenuFragment.refreshLists();
            }
        });

```



## `FoodPlaceAdapter.java`

Defines `FoodType` enum that serves as switch for later UI stuff. Defines view holder for `FoodPlace`s 
for Dining Halls and Cafes cards to display on the `FoodFragment` screen. Clicking on cards launches the respective
`Open*Activity.java` for Dining Hall of Cafe.

```java
 public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            openStatusTextView = (TextView) itemView.findViewById(R.id.open_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;

                    // set the current cafe/dining hall and start the right intent
                    if (foodType == FoodType.Cafe) {
                        Cafe cafe = (Cafe) mFoodPlaceList.get(getAdapterPosition());
                        OpenCafeActivity.setCafe(cafe);
                        intent = new Intent(mContext, OpenCafeActivity.class);
                        mContext.startActivity(intent);
                    } else if (foodType == FoodType.DiningHall) {
                        DiningHall diningHall = (DiningHall) mFoodPlaceList.get(getAdapterPosition());
                        OpenDiningHallActivity.setDiningHall(diningHall);
                        intent = new Intent(mContext, OpenDiningHallActivity.class);
                        mContext.startActivity(intent);
                    }

                }
            });
        }

```

Similar to `GymAdapter.java` in the card view design. Possibly modularize this in the future.

Above references view and sets onClickListener for the entire card. On click, switches on food tpe
and creates the appropriate intent for launching `Open*Activity.java`

## `LibraryAdapter.java`

Important feature is the filter, that returns a list of `Library`s given a search string. Searches all 
`Library` objects fetched, depending on name of library. Possibly to search for other fields as well, 
including location or description, but might be confusing to users. 

Also features favorite button, as with some other list adapters, and auto sorts based on favorites and AZ. 
This style of adapter is pretty standard, and a variant is used for `ResourceAdapter.java` as well. Possible 
to standardize this and make it more modular.

## `MainMenuAdapter.java`

Defines adapter for `Category`s in the main menu (navigation drawer really). The name is misleading imo, and 
probably need a better main menu splash screen instead of having the user see an open nav drawer as the first 
screen on launch.
