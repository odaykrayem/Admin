package com.example.admin.utils;

public class NetworkUtils {

    //    public static final String BASE_URL =  "http://192.168.43.78:8000";
//    public static final String BASE_URL =  "http://192.168.1.107:8000";
//    public static final String BASE_URL =  "http://192.168.43.130:8000";
//      public static final String BASE_URL =  "http://192.168.137.1:8000";
//    public static final String BASE_URL =  "http://192.168.1.110:8000";
    public static final String BASE_URL =  "https://rasidi.jableh.net";

    public static final String BASE_API_FOLDER = "api";
    public static final String BASE_IDENTIFIER = "admin";
    public static final String OPERATIONS_URL = "transfer_operations";

    // URL FOR GETTING USER'S OPERATIONS IN OPERATION FRAGMENT
    public static final String URL_OPERATIONS =
            NetworkUtils.BASE_URL + "/" +
                    NetworkUtils.BASE_API_FOLDER + "/" +
                    NetworkUtils.BASE_IDENTIFIER+ "/" +
                    NetworkUtils.OPERATIONS_URL;
}
