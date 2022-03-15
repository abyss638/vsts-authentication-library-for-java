// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.storage.windows;

import com.microsoft.a4o.credentialstorage.secret.Credential;
import com.microsoft.a4o.credentialstorage.storage.windows.internal.CredManagerBackedSecureStore;

public class CredManagerBackedCredentialStore extends CredManagerBackedSecureStore<Credential> {

    @Override
    protected Credential create(final String username, final String secret) {
        return new Credential(username, secret) ;
    }

    @Override
    protected String getUsername(final Credential cred) {
        return cred.Username;
    }

    @Override
    protected String getCredentialBlob(final Credential cred) {
        return cred.Password;
    }
}