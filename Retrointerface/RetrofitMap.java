package com.example.dell.mapsusingretrofit.Retrointerface;

import com.example.dell.mapsusingretrofit.MyPojo.MyNearBy;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Dell on 13-02-2018.
 */

public interface RetrofitMap {


    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
   @GET("/maps/api/place/nearbysearch/json") Call<MyNearBy> getNearByPlaces(@Query("location") String  location, @Query("radius") String radius);

   // Call<MyNearBy> getNearByPlaces(String s, int proximity_radius);


    //Call<MyNearBy>getNearByPlaces(@Query("type") String type,@Query("location") String location,@Query("radius") int radius);
}
