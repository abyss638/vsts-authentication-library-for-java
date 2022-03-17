// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.secret;

import com.microsoft.a4o.credentialstorage.helpers.Guid;
import com.microsoft.a4o.credentialstorage.helpers.StringHelper;
import com.microsoft.a4o.credentialstorage.helpers.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A security token, usually acquired by some authentication and identity services.
 */
public class Token extends Secret {
    /**
     * The type of the security token.
     */
    private final TokenType type;

    /**
     * The raw contents of the token.
     */
    private final String value;

    /**
     * The target identity for the security token.
     */
    private final UUID targetIdentity;

    public static boolean getFriendlyNameFromType(final TokenType type, final AtomicReference<String> name) {
        // PORT NOTE: Java doesn't have the concept of out-of-range enums

        name.set(null);

        name.set(type.getDescription() == null
                ? type.toString()
                : type.getDescription());

        return name.get() != null;
    }

    public static boolean getTypeFromFriendlyName(final String name, final AtomicReference<TokenType> type) {
        if (StringHelper.isNullOrWhiteSpace(name)) {
            throw new IllegalArgumentException("The name parameter is null or invalid");
        }

        type.set(TokenType.Unknown);

        for (final TokenType value : EnumSet.allOf(TokenType.class)) {
            type.set(value);

            AtomicReference<String> typename = new AtomicReference<String>();
            if (getFriendlyNameFromType(type.get(), typename)) {
                if (name.equalsIgnoreCase(typename.get()))
                    return true;
            }
        }

        return false;
    }

    public Token(final String value, final TokenType type) {
        this(value, type, Guid.Empty);
    }

    public Token(final String value, final String typeName) {
        if (StringHelper.isNullOrWhiteSpace(value)) {
            throw new IllegalArgumentException("The value parameter is null or invalid");
        }
        if (StringHelper.isNullOrWhiteSpace(value)) {
            throw new IllegalArgumentException("The value parameter is null or invalid");
        }
        if (StringHelper.isNullOrWhiteSpace(value)) {
            throw new IllegalArgumentException("The typeName parameter is null or invalid");
        }

        AtomicReference<TokenType> type = new AtomicReference<TokenType>();
        if (!getTypeFromFriendlyName(typeName, type)) {
            throw new IllegalArgumentException("Unexpected token type '" + typeName + "' encountered");
        }
        this.type = type.get();
        this.value = value;
        this.targetIdentity = Guid.Empty;
    }

    public Token(final String value, final TokenType type, final UUID targetIdentity) {
        if (StringHelper.isNullOrWhiteSpace(value)) {
            throw new IllegalArgumentException("The value parameter is null or invalid");
        }
        Objects.requireNonNull(type, "The type parameter is null");
        Objects.requireNonNull(targetIdentity, "The targetIdentity parameter is null");

        this.type = type;
        this.value = value;
        this.targetIdentity = targetIdentity;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public UUID getTargetIdentity() {
        return targetIdentity;
    }

    public static Token fromXml(final Node tokenNode) {
        Token value;

        String tokenValue = null;
        TokenType tokenType = null;
        UUID targetIdentity = Guid.Empty;

        final NodeList propertyNodes = tokenNode.getChildNodes();
        for (int v = 0; v < propertyNodes.getLength(); v++) {
            final Node propertyNode = propertyNodes.item(v);
            final String propertyName = propertyNode.getNodeName();
            if ("Type".equals(propertyName)) {
                tokenType = TokenType.valueOf(TokenType.class, XmlHelper.getText(propertyNode));
            } else if ("Value".equals(propertyName)) {
                tokenValue = XmlHelper.getText(propertyNode);
            } else if ("targetIdentity".equals(propertyName)) {
                targetIdentity = UUID.fromString(XmlHelper.getText(propertyNode));
            }
        }
        value = new Token(tokenValue, tokenType, targetIdentity);
        return value;
    }

    public Element toXml(final Document document) {
        final Element valueNode = document.createElement("value");

        final Element typeNode = document.createElement("Type");
        final Text typeValue = document.createTextNode(this.type.toString());
        typeNode.appendChild(typeValue);
        valueNode.appendChild(typeNode);

        final Element tokenValueNode = document.createElement("Value");
        final Text valueValue = document.createTextNode(this.value);
        tokenValueNode.appendChild(valueValue);
        valueNode.appendChild(tokenValueNode);

        if (!Guid.Empty.equals(this.getTargetIdentity())) {
            final Element targetIdentityNode = document.createElement("targetIdentity");
            final Text targetIdentityValue = document.createTextNode(this.getTargetIdentity().toString());
            targetIdentityNode.appendChild(targetIdentityValue);
            valueNode.appendChild(targetIdentityNode);
        }
        return valueNode;
    }

    /**
     * Compares an object to this {@link Token} for equality.
     *
     * @param obj The object to compare.
     * @return True is equal; false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return operatorEquals(this, obj instanceof Token ? ((Token) obj) : null);
    }
    // PORT NOTE: Java doesn't support a specific overload (as per IEquatable<T>)

    /**
     * Gets a hash code based on the contents of the token.
     *
     * @return 32-bit hash code.
     */
    @Override
    public int hashCode() {
        // PORT NOTE: Java doesn't have unchecked blocks; the default behaviour is apparently equivalent.
        {
            return type.ordinal() * value.hashCode();
        }
    }

    /**
     * Converts the token to a human friendly string.
     *
     * @return Humanish name of the token.
     */
    @Override
    public String toString() {
        final AtomicReference<String> value = new AtomicReference<String>();
        if (getFriendlyNameFromType(type, value))
            return value.get();
        else
            return super.toString();
    }

    public static void validate(final Token token) {
        if (token == null)
            throw new IllegalArgumentException("The `token` parameter is null or invalid.");
        if (StringHelper.isNullOrWhiteSpace(token.value))
            throw new IllegalArgumentException("The value of the `token` cannot be null or empty.");
        if (token.value.length() > Credential.PASSWORD_MAX_LENGTH)
            throw new IllegalArgumentException(String.format("The value of the `token` cannot be longer than %1$d " +
                    "characters.", Credential.PASSWORD_MAX_LENGTH));
    }

    /**
     * Explicitly casts a personal access token token into a set of credentials
     *
     * @param token The {@link Token} to convert.
     * @return A corresponding {@link Credential} instance.
     * @throws IllegalArgumentException if the {@link Token#type} is not {@link TokenType#Personal}.
     */
    public static Credential toCredential(final Token token) {
        if (token == null)
            return null;

        if (token.type != TokenType.Personal)
            throw new IllegalArgumentException("Cannot convert " + token + " to credentials");

        return new Credential(token.toString(), token.value);
    }

    /**
     * Compares two tokens for equality.
     *
     * @param token1 Token to compare.
     * @param token2 Token to compare.
     * @return True if equal; false otherwise.
     */
    public static boolean operatorEquals(final Token token1, final Token token2) {
        if (token1 == token2)
            return true;
        if ((token1 == null) || (null == token2))
            return false;

        return token1.type == token2.type
                && token1.value.equalsIgnoreCase(token2.value);
    }

    /**
     * Compares two tokens for inequality.
     *
     * @param token1 Token to compare.
     * @param token2 Token to compare.
     * @return False if equal; true otherwise.
     */
    public static boolean operatorNotEquals(final Token token1, final Token token2) {
        return !operatorEquals(token1, token2);
    }
}
