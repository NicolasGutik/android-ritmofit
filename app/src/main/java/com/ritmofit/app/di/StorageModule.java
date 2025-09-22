package com.ritmofit.app.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class StorageModule {
    
    private static final String PREFS_NAME = "ritmo_fit_secure_prefs";
    
    @Provides
    @Singleton
    public MasterKey provideMasterKey(@ApplicationContext Context context) {
        try {
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setKeySize(256)
                    .build();
            
            return new MasterKey.Builder(context)
                    .setKeyGenParameterSpec(keyGenParameterSpec)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating MasterKey", e);
        }
    }
    
    @Provides
    @Singleton
    public SharedPreferences provideEncryptedSharedPreferences(
            @ApplicationContext Context context,
            MasterKey masterKey) {
        try {
            return EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new RuntimeException("Error creating EncryptedSharedPreferences", e);
        }
    }
}


