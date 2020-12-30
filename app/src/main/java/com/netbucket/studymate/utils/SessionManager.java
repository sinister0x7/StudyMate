package com.netbucket.studymate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    public static final String SESSION_USER_SESSION = "sessionUserLogin";
    public static final String KEY_UID = "uid";
    public static final String KEY_USER_PATH = "path";
    public static final String KEY_USER_STATUS = "userStatus";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ROLE = "role";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_INSTITUTE = "institute";
    public static final String KEY_COURSE = "course";
    public static final String KEY_ID = "id";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_ABOUT = "about";
    public static final String KEY_SEM_OR_YEAR = "semOrYear";
    public static final String KEY_PROFILE_IMAGE_URI = "profileImageUri";
    private static final String IS_LOGGED_IN = "isLoggedIn";
//    private static final int KEY_SIZE = 256;
//    private static final String KEY_CONTAINER = "Vault007";

    Context mContext;
    SharedPreferences mUserSession;
    //    KeyGenParameterSpec mKeyGenParameterSpec;
//    MasterKey mMasterKey;
    SharedPreferences.Editor mSharedPrefEditor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context, String sessionName) {
        mContext = context;

//        mKeyGenParameterSpec = new KeyGenParameterSpec.Builder(
//                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
//                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
//                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//                .setKeySize(KEY_SIZE).build();
//
//        mMasterKey = new MasterKey.Builder(context)
//                .setKeyGenParameterSpec(mKeyGenParameterSpec).build();
//
//        mUserSession = EncryptedSharedPreferences.create(context, KEY_CONTAINER, mMasterKey,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        mUserSession = mContext.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        mSharedPrefEditor = mUserSession.edit();
    }

//    MasterKey exMasterKey = new MasterKey.Builder(mContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

    public void createUserSession(String uid, String userPath, String userStatus, String fullName, String email, String role, String gender, String password, String institute, String course, String id, String birthday, String phoneNumber, String username, String about, String semOrYear, String profileImageUri) {
        mSharedPrefEditor.putBoolean(IS_LOGGED_IN, true);

        mSharedPrefEditor.putString(KEY_UID, uid);
        mSharedPrefEditor.putString(KEY_USER_PATH, userPath);

        mSharedPrefEditor.putString(KEY_USER_STATUS, userStatus);
        mSharedPrefEditor.putString(KEY_FULL_NAME, fullName);
        mSharedPrefEditor.putString(KEY_EMAIL, email);
        mSharedPrefEditor.putString(KEY_ROLE, role);
        mSharedPrefEditor.putString(KEY_GENDER, gender);
        mSharedPrefEditor.putString(KEY_PASSWORD, password);
        mSharedPrefEditor.putString(KEY_INSTITUTE, institute);
        mSharedPrefEditor.putString(KEY_COURSE, course);
        mSharedPrefEditor.putString(KEY_ID, id);
        mSharedPrefEditor.putString(KEY_BIRTHDAY, birthday);
        mSharedPrefEditor.putString(KEY_PHONE_NUMBER, phoneNumber);
        mSharedPrefEditor.putString(KEY_USERNAME, username);
        mSharedPrefEditor.putString(KEY_ABOUT, about);
        mSharedPrefEditor.putString(KEY_SEM_OR_YEAR, semOrYear);
        mSharedPrefEditor.putString(KEY_PROFILE_IMAGE_URI, profileImageUri);

        mSharedPrefEditor.apply();
    }

    public void createUserSession(String userStatus) {
        mSharedPrefEditor.putString(KEY_USER_STATUS, userStatus);
        mSharedPrefEditor.apply();
    }

    public HashMap<String, String> getUserDataFromSession() {
        HashMap<String, String> storedUserData = new HashMap<>();
        storedUserData.put(KEY_UID, mUserSession.getString(KEY_UID, null));
        storedUserData.put(KEY_USER_PATH, mUserSession.getString(KEY_USER_PATH, null));
        storedUserData.put(KEY_USER_STATUS, mUserSession.getString(KEY_USER_STATUS, null));
        storedUserData.put(KEY_FULL_NAME, mUserSession.getString(KEY_FULL_NAME, null));
        storedUserData.put(KEY_EMAIL, mUserSession.getString(KEY_EMAIL, null));
        storedUserData.put(KEY_ROLE, mUserSession.getString(KEY_ROLE, null));
        storedUserData.put(KEY_GENDER, mUserSession.getString(KEY_GENDER, null));
        storedUserData.put(KEY_PASSWORD, mUserSession.getString(KEY_PASSWORD, null));
        storedUserData.put(KEY_INSTITUTE, mUserSession.getString(KEY_INSTITUTE, null));
        storedUserData.put(KEY_COURSE, mUserSession.getString(KEY_COURSE, null));
        storedUserData.put(KEY_ID, mUserSession.getString(KEY_ID, null));
        storedUserData.put(KEY_BIRTHDAY, mUserSession.getString(KEY_BIRTHDAY, null));
        storedUserData.put(KEY_PHONE_NUMBER, mUserSession.getString(KEY_PHONE_NUMBER, null));
        storedUserData.put(KEY_USERNAME, mUserSession.getString(KEY_USERNAME, null));
        storedUserData.put(KEY_ABOUT, mUserSession.getString(KEY_ABOUT, null));
        storedUserData.put(KEY_SEM_OR_YEAR, mUserSession.getString(KEY_SEM_OR_YEAR, null));
        storedUserData.put(KEY_PROFILE_IMAGE_URI, mUserSession.getString(KEY_PROFILE_IMAGE_URI, null));
        return storedUserData;
    }

    public boolean checkIfLoggedIn() {
        return mUserSession.getBoolean(IS_LOGGED_IN, true);
    }

    public void invalidateSession() {
        mSharedPrefEditor.clear();
        mSharedPrefEditor.apply();
    }
}