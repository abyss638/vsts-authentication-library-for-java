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
import java.util.UUID;

/**
 * A security token, usually acquired by some authentication and identity services.
 */
public class Token extends Secret {
    private static final UUID EMPTY_UUID = new UUID(0, 0);

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

    public Token(final String value, final TokenType type) {
        this(value, type, EMPTY_UUID);
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
        UUID targetIdentity = EMPTY_UUID;

        final NodeList propertyNodes = tokenNode.getChildNodes();
        for (int v = 0; v < propertyNodes.getLength(); v++) {
            final Node propertyNode = propertyNodes.item(v);
            final String propertyName = propertyNode.getNodeName();
            switch (propertyName) {
                case "Type":
                    tokenType = TokenType.valueOf(TokenType.class, XmlHelper.getText(propertyNode));
                    break;
                case "Value":
                    tokenValue = XmlHelper.getText(propertyNode);
                    break;
                case "targetIdentity":
                    targetIdentity = UUID.fromString(XmlHelper.getText(propertyNode));
                    break;
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

        if (!EMPTY_UUID.equals(this.getTargetIdentity())) {
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

    /**
     * Gets a hash code based on the contents of the token.
     *
     * @return 32-bit hash code.
     */
    @Override
    public int hashCode() {
        return type.ordinal() * value.hashCode();
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
}
