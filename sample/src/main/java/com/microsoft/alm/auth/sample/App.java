// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.auth.sample;

import com.microsoft.alm.secret.Credential;
import com.microsoft.alm.storage.SecretStore;
import com.microsoft.alm.storage.StorageProvider;
import com.microsoft.alm.storage.StorageProvider.SecureOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    private static final String CREDENTIALS_KEY = "TestCredentials";
    private static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        // Get a secure store instance
        final SecretStore<Credential> credentialStorage = StorageProvider.getCredentialStorage(true, SecureOption.MUST);

        // Get token name from the user
        String credentialName = getCredentialName(CREDENTIALS_KEY);

        // Retrieve the existing credential from the store
        Credential storedCredential = credentialStorage.get(credentialName);
        printCredential(credentialName, storedCredential);

        // Create a new credential instance from user input
        System.out.println("Enter user name: ");
        String userName = INPUT.readLine();
        System.out.println("Enter password: ");
        String password = INPUT.readLine();
        Credential credential = new Credential(userName, password);

        // Save the credential to the store
        credentialStorage.add(credentialName, credential);

        System.out.println("Added/Updated credentials to Credential Manager under the key: " + credentialName);
        System.out.println();

        // Retrieve the credential from the store
        Credential newStoredCredential = credentialStorage.get(credentialName);

        System.out.println("Retrieved the updated token from Credential Manager using the key: " + credentialName);
        printCredential(credentialName, newStoredCredential);

        System.out.println("Remove the token from Credential Manager under the key " + credentialName + " [Y/n]?");
        if (!"n".equalsIgnoreCase(INPUT.readLine())) {
            credentialStorage.delete(credentialName);
        }
    }

    private static void printCredential(String credentialName, Credential storedCredential) {
        if (storedCredential != null) {
            System.out.println("Retrieved current credentials from Credential Manager using the key: " + credentialName);
            System.out.println("  Username: " + storedCredential.Username);
            System.out.println("  Password: " + storedCredential.Password);
        } else {
            System.out.println("No stored credentials under the key: " + credentialName);
        }
        System.out.println();
    }

    private static String getCredentialName(String defaultCredentialName) throws IOException {
        System.out.print("Enter token name [" + defaultCredentialName + "]: ");
        String tokenName = INPUT.readLine();
        if (tokenName == null || tokenName.isEmpty()) tokenName = defaultCredentialName;
        return tokenName;
    }
}
