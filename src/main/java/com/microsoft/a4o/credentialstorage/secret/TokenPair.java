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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class TokenPair extends Secret {
    /**
     * Access token, used to grant access to resources.
     */
    private final Token accessToken;

    /**
     * Refresh token, used to grant new access tokens.
     */
    private final Token refreshToken;

    /**
     * Additional token parameters.
     */
    private final Map<String, String> parameters;

    /**
     * Creates a new {@link TokenPair} from raw access and refresh token data.
     *
     * @param accessToken  The base64 encoded value of the access token's raw data
     * @param refreshToken The base64 encoded value of the refresh token's raw data
     */
    public TokenPair(final String accessToken, final String refreshToken) {
        if (StringHelper.isNullOrWhiteSpace(accessToken)) {
            throw new IllegalArgumentException("The accessToken parameter is null or invalid.");
        }
        if (StringHelper.isNullOrWhiteSpace(refreshToken)) {
            throw new IllegalArgumentException("The refreshToken parameter is null or invalid.");
        }

        this.accessToken = new Token(accessToken, TokenType.Access);
        this.refreshToken = new Token(refreshToken, TokenType.Refresh);
        this.parameters = Collections.emptyMap();
    }

    public Token getAccessToken() {
        return accessToken;
    }

    public Token getRefreshToken() {
        return refreshToken;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public static TokenPair fromXml(final Node tokenPairNode) {
        TokenPair value;

        String accessToken = null;
        String refreshToken = null;

        final NodeList propertyNodes = tokenPairNode.getChildNodes();
        for (int v = 0; v < propertyNodes.getLength(); v++) {
            final Node propertyNode = propertyNodes.item(v);
            final String propertyName = propertyNode.getNodeName();
            if ("accessToken".equals(propertyName)) {
                accessToken = XmlHelper.getText(propertyNode);
            } else if ("refreshToken".equals(propertyName)) {
                refreshToken = XmlHelper.getText(propertyNode);
            }
        }

        value = new TokenPair(accessToken, refreshToken);
        return value;
    }

    public Element toXml(final Document document) {
        final Element valueNode = document.createElement("value");

        final Element accessTokenNode = document.createElement("accessToken");
        final Text accessTokenValue = document.createTextNode(accessToken.getValue());
        accessTokenNode.appendChild(accessTokenValue);
        valueNode.appendChild(accessTokenNode);

        final Element refreshTokenNode = document.createElement("refreshToken");
        final Text refreshTokenValue = document.createTextNode(refreshToken.getValue());
        refreshTokenNode.appendChild(refreshTokenValue);
        valueNode.appendChild(refreshTokenNode);

        return valueNode;
    }

    public static String toXmlString(final TokenPair tokenPair) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document document = builder.newDocument();

            final Element element = tokenPair.toXml(document);
            document.appendChild(element);

            return XmlHelper.toString(document);
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }

    public static TokenPair fromXmlString(final String xmlString) {
        final byte[] bytes = StringHelper.UTF8GetBytes(xmlString);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return fromXmlStream(inputStream);
    }

    static TokenPair fromXmlStream(final InputStream source) {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            final Document document = builder.parse(source);
            final Element rootElement = document.getDocumentElement();

            return TokenPair.fromXml(rootElement);
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Compares an object to this.
     *
     * @param object The object to compare.
     * @return True if equal; false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        return operatorEquals(this, object instanceof TokenPair ? ((TokenPair) object) : null);
    }

    /**
     * Gets a hash code based on the contents of the {@link TokenPair}.
     *
     * @return 32-bit hash code.
     */
    @Override
    public int hashCode() {
        return accessToken.hashCode() * refreshToken.hashCode();
    }

    /**
     * Compares two {@link TokenPair} for equality.
     *
     * @param pair1 {@link TokenPair} to compare.
     * @param pair2 {@link TokenPair} to compare.
     * @return True if equal; false otherwise.
     */
    public static boolean operatorEquals(final TokenPair pair1, final TokenPair pair2) {
        if (pair1 == pair2)
            return true;
        if ((pair1 == null) || (null == pair2))
            return false;

        return Token.operatorEquals(pair1.accessToken, pair2.accessToken)
                && Token.operatorEquals(pair1.refreshToken, pair2.refreshToken);
    }
}
