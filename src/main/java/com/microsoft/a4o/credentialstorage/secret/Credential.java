// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.secret;

import com.microsoft.a4o.credentialstorage.helpers.StringHelper;
import com.microsoft.a4o.credentialstorage.helpers.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.Objects;

/**
 * Credential for user authentication.
 */
public final class Credential extends Secret {
    public static final int USERNAME_MAX_LENGTH = 511;
    public static final int PASSWORD_MAX_LENGTH = 2047;

    private final String username;
    private final String password;

    /**
     * Creates a credential object with a username and password pair.
     *
     * @param username The username value of the {@link Credential}.
     * @param password The password value of the {@link Credential}.
     */
    public Credential(final String username, final String password) {
        this.username = Objects.requireNonNullElse(username, StringHelper.Empty);
        this.password = Objects.requireNonNullElse(password, StringHelper.Empty);
    }

    /**
     * Creates a credential object with only a username.
     *
     * @param username The username value of the {@link Credential}.
     */
    public Credential(final String username) {
        this(username, StringHelper.Empty);
    }

    /**
     * Unique identifier of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Secret related to the username.
     */
    public String getPassword() {
        return password;
    }

    public static Credential fromXml(final Node credentialNode) {
        Credential value;
        String password = null;
        String username = null;

        final NodeList propertyNodes = credentialNode.getChildNodes();
        for (int v = 0; v < propertyNodes.getLength(); v++) {
            final Node propertyNode = propertyNodes.item(v);
            if (propertyNode.getNodeType() != Node.ELEMENT_NODE) continue;

            final String propertyName = propertyNode.getNodeName();
            if ("Password".equals(propertyName)) {
                password = XmlHelper.getText(propertyNode);
            } else if ("Username".equals(propertyName)) {
                username = XmlHelper.getText(propertyNode);
            }
        }
        value = new Credential(username, password);
        return value;
    }

    public Element toXml(final Document document) {
        final Element valueNode = document.createElement("value");

        final Element passwordNode = document.createElement("Password");
        final Text passwordValue = document.createTextNode(this.password);
        passwordNode.appendChild(passwordValue);
        valueNode.appendChild(passwordNode);

        final Element usernameNode = document.createElement("Username");
        final Text usernameValue = document.createTextNode(this.username);
        usernameNode.appendChild(usernameValue);
        valueNode.appendChild(usernameNode);

        return valueNode;
    }

    /**
     * Compares an object to this {@link Credential} for equality.
     *
     * @param obj The object to compare.
     * @return True if equal; false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return operatorEquals(this, obj instanceof Credential ? ((Credential) obj) : null);
    }
    // PORT NOTE: Java doesn't support a specific overload (as per IEquatable<T>)

    /**
     * Gets a hash code based on the contents of the {@link Credential}.
     *
     * @return 32-bit hash code.
     */
    @Override
    public int hashCode() {
        // PORT NOTE: Java doesn't have unchecked blocks; the default behaviour is apparently equivalent.
        {
            return username.hashCode() + 7 * password.hashCode();
        }
    }

    public static void validate(final Credential credentials) {
        if (credentials == null)
            throw new IllegalArgumentException("The credentials parameter cannot be null");
        if (credentials.password.length() > PASSWORD_MAX_LENGTH)
            throw new IllegalArgumentException(String.format("The Password field of the credentials parameter cannot " +
                    "be longer than %1$d characters.", PASSWORD_MAX_LENGTH));
        if (credentials.username.length() > USERNAME_MAX_LENGTH)
            throw new IllegalArgumentException(String.format("The Username field of the credentials parameter cannot " +
                    "be longer than %1$d characters.", USERNAME_MAX_LENGTH));
    }

    /**
     * Compares two credentials for equality.
     *
     * @param credential1 Credential to compare.
     * @param credential2 Credential to compare.
     * @return True if equal; false otherwise.
     */
    public static boolean operatorEquals(final Credential credential1, final Credential credential2) {
        if (credential1 == credential2)
            return true;
        if ((credential1 == null) || (null == credential2))
            return false;

        return credential1.username.equals(credential2.username)
                && credential1.password.equals(credential2.password);
    }

    /**
     * Compares two credentials for inequality.
     *
     * @param credential1 Credential to compare.
     * @param credential2 Credential to compare.
     * @return False if equal; true otherwise.
     */
    public static boolean operatorNotEquals(final Credential credential1, final Credential credential2) {
        return !operatorEquals(credential1, credential2);
    }
}
