# Fragments Doc 1.0

## Summary
Some fragments. Most common use is fragment open on navigation drawer selection.

## `FoodFragment.java`
Inflates layout with two horizontally scrolling RecyclerViews for displaying DiningHall
and Cafe cards. 

``` 
        // set up dining hall RecyclerView
        mDiningRecyclerView = (RecyclerView) layout.findViewById(R.id.dining_hall_recycler_view);
        mDiningRecyclerView.setHasFixedSize(true);
        mDiningRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mDiningHallList = new ArrayList<>();
        mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

        // set up cafe RecyclerView
        mCafeRecyclerView = (RecyclerView) layout.findViewById(R.id.cafe_recycler_view);
        mCafeRecyclerView.setHasFixedSize(true);
        mCafeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mCafeList = new ArrayList<>();
        mCafeRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mCafeList, FoodPlaceAdapter.FoodType.Cafe));

```

Clicking on RecylerView components will launch `OpenCafeActivity` or `OpenDiningHallActivity` accordingly.
For details, see the corresponding adapters.

Fetches information from the backend asynchronously by using `enqueue`, making UI prompts
 accordingly with refresh button and toasts.

```

    private void getDining() {

        mDiningHallsCall.enqueue(new retrofit2.Callback<DiningHallsResponse>() {
             @Override
             public void onResponse(Call<DiningHallsResponse> call, Response<DiningHallsResponse> response) {

                mDiningHallList = (List<FoodPlace>) response.body().getDiningHalls();

                mDiningHallLabel.setVisibility(View.VISIBLE);
                mDiningRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mDiningRecyclerView.setAdapter(new FoodPlaceAdapter(getContext(), mDiningHallList, FoodPlaceAdapter.FoodType.DiningHall));

             }

             @Override
             public void onFailure(Call<DiningHallsResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mRefreshWrapper.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to retrieve dining hall data, please try again",
                        Toast.LENGTH_SHORT).show();
             }
         });
    }

```