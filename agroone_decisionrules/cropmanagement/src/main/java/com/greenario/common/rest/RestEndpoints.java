package com.greenario.common.rest;

public class RestEndpoints {

    // API version
    public static final String API_V1 = "/api/v1/";
// Crop Condition Endpoints
public static final String URL_CROP_CONDITIONS = API_V1 + "cropconditions";
public static final String URL_CROP_CONDITION = API_V1 + "cropconditions/{cropconditionid}";
public static final String URL_CROP_CONDITIONS_CROP_MONITORINGS = API_V1 + "cropconditions/cropconditionmonitorings";
public static final String URL_CROP_CONDITIONS_CROP_MONITORINGS_PROJECT_ID = API_V1 + "cropconditions/cropconditionmonitorings/{projectid}";
}