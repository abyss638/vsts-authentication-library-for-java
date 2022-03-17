// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.secret;

import com.microsoft.a4o.credentialstorage.helpers.StringHelperTest;
import com.microsoft.a4o.credentialstorage.helpers.XmlHelper;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class TokenPairTest {

    @Test
    public void xmlSerialization_roundTrip() throws Exception {
        final TokenPair tokenPair =
            new TokenPair("9297fb18-46d0-4846-97ca-ab8dd3b55729", "d15281b1-03f1-4581-90d3-4527d9cf4147");
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = dbf.newDocumentBuilder();
        final Document serializationDoc = builder.newDocument();

        final Element element = tokenPair.toXml(serializationDoc);

        serializationDoc.appendChild(element);
        final String actualXmlString = XmlHelper.toString(serializationDoc);
        final String expectedXmlString =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                        "<value>\n" +
                        "    <accessToken>9297fb18-46d0-4846-97ca-ab8dd3b55729</accessToken>\n" +
                        "    <refreshToken>d15281b1-03f1-4581-90d3-4527d9cf4147</refreshToken>\n" +
                        "</value>";
        StringHelperTest.assertLinesEqual(expectedXmlString, actualXmlString);

        final ByteArrayInputStream bais = new ByteArrayInputStream(actualXmlString.getBytes());
        final Document deserializationDoc = builder.parse(bais);
        final Element rootNode = deserializationDoc.getDocumentElement();

        final TokenPair actualTokenPair = TokenPair.fromXml(rootNode);

        assertEquals(tokenPair.AccessToken.Value, actualTokenPair.AccessToken.Value);
        assertEquals(tokenPair.RefreshToken.Value, actualTokenPair.RefreshToken.Value);
    }
}
