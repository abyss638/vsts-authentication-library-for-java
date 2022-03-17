// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.a4o.credentialstorage.secret;

import com.microsoft.a4o.credentialstorage.helpers.StringHelperTest;
import com.microsoft.a4o.credentialstorage.helpers.XmlHelper;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.UUID;

public class TokenTest {

    @Test
    public void xmlSerialization_roundTrip() throws Exception {
        final Token token = new Token("1", TokenType.Access, UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"));
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = dbf.newDocumentBuilder();
        final Document serializationDoc = builder.newDocument();

        final Element element = token.toXml(serializationDoc);

        serializationDoc.appendChild(element);
        final String actualXmlString = XmlHelper.toString(serializationDoc);
        final String expectedXmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<value>\n" +
            "    <Type>Access</Type>\n" +
            "    <Value>1</Value>\n" +
            "    <targetIdentity>ffffffff-ffff-ffff-ffff-ffffffffffff</targetIdentity>\n" +
            "</value>";
        StringHelperTest.assertLinesEqual(expectedXmlString, actualXmlString);

        final ByteArrayInputStream bais = new ByteArrayInputStream(actualXmlString.getBytes());
        final Document deserializationDoc = builder.parse(bais);
        final Element rootNode = deserializationDoc.getDocumentElement();

        final Token actualToken = Token.fromXml(rootNode);

        Assert.assertEquals(token.getValue(), actualToken.getValue());
        Assert.assertEquals(token.getType(), actualToken.getType());
        Assert.assertEquals(token.getTargetIdentity(), actualToken.getTargetIdentity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_tooLong() {
        final int numberOfCharacters = 2048;
        final Token token = new Token("0".repeat(numberOfCharacters), TokenType.Test);

        Token.validate(token);
    }
}
