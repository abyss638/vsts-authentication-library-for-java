// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.storage.posix;

import com.microsoft.a4o.credentialstorage.helpers.LoggingHelper;
import com.microsoft.a4o.credentialstorage.secret.TokenPair;
import com.microsoft.a4o.credentialstorage.storage.posix.internal.GnomeKeyringBackedSecureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class GnomeKeyringBackedTokenPairStore extends GnomeKeyringBackedSecureStore<TokenPair> {

    private static final Logger logger = LoggerFactory.getLogger(GnomeKeyringBackedTokenPairStore.class);

    @Override
    protected String serialize(final TokenPair tokenPair) {
        Objects.requireNonNull(tokenPair, "TokenPair cannot be null");

        return TokenPair.toXmlString(tokenPair);
    }

    @Override
    protected TokenPair deserialize(final String secret) {
        Objects.requireNonNull(secret, "secret cannot be null");

        try {
            return TokenPair.fromXmlString(secret);
        } catch (final Exception e) {
            LoggingHelper.logError(logger, "Failed to deserialize the stored secret. Return null.", e);
            return null;
        }
    }

    @Override
    protected String getType() {
        return "OAuth2Token";
    }
}
