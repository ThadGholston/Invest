package com.investmobile.invest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by thad on 11/16/15.
 */
public class InvestRequest {

    private final String BASE_URL = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/";
    private final String NEWS_ENDPOINT = "news";
    private final String FAVORITE_ENDPOINT = "favorite";
    private final String ACCOUNT_ENDPOINT = "account";

    public static boolean  loginRequest(){
        return true;
    }

    public static boolean logoutRequest(Context context){

        return true;
    }

    public static ArrayList<Favorite> getFavorites(){
        return null;
    }

    public static boolean deleteFavorite(String symbol){
        return true;
    }

    public static ArrayList<News> getNews(){
        return null;
    }

    public static boolean changePassword(String oldPassword, String newPassword){
        return true;
    }

    public static boolean signUp(String firstName, String lastName, String password, String username){
        return true;
    }

    public abstract class PreRequest{


    }


}
