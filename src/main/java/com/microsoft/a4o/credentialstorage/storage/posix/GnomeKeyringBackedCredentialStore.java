// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.storage.posix;

import com.microsoft.a4o.credentialstorage.helpers.StringHelper;
import com.microsoft.a4o.credentialstorage.helpers.XmlHelper;
import com.microsoft.a4o.credentialstorage.secret.Credential;
import com.microsoft.a4o.credentialstorage.storage.posix.internal.GnomeKeyringBackedSecureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

public class GnomeKeyringBackedCredentialStore extends GnomeKeyringBackedSecureStore<Credential> {

    private static final Logger logger = LoggerFactory.getLogger(GnomeKeyringBackedCredentialStore.class);

    @Override
    protected Credential deserialize(final String secret) {
        Objects.requireNonNull(secret, "secret cannot be null");

        try {
            return fromXmlString(secret);
        } catch (final Exception e) {
            logger.error("Failed to deserialize credential.", e);
            return null;
        }
    }

    static Credential fromXmlString(final String xmlString) {
        final byte[] bytes = StringHelper.UTF8GetBytes(xmlString);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return fromXmlStream(inputStream);
    }

    static Credential fromXmlStream(final InputStream source) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document document = builder.parse(source);
            final Element rootElement = document.getDocumentElement();

            final Credential result = Credential.fromXml(rootElement);

            return result;
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }

    @Override
    protected String serialize(final Credential credential) {
        Objects.requireNonNull(credential, "Credential cannot be null");

        return toXmlString(credential);
    }

    static String toXmlString(final Credential credential) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document document = builder.newDocument();

            final Element element = credential.toXml(document);
            document.appendChild(element);

            final String result = XmlHelper.toString(document);

            return result;
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }

    @Override
    protected String getType() {
        return "Credential";
    }
}
