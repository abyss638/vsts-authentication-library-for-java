# Credential Secure Storage for Java 
Unified interface to store Java application secrets on different platforms.

# What this library provides
This library provides a set of secure `storage` providers that store retrieved secrets, as well as In memory and File system backed insecure storages.   

### Available Secure Storage Providers:
| Secret Type                                    | Windows (Credential Manager) | Linux (GNOME Keyring v2.22+)  | Mac OSX (Keychain)|
|------------------------------------------------|------------------------------|-------------------------------|-------------------|
| Username / Password Credentials (`Credential`) | Yes | Yes | Yes |
| OAuth2 Access/Refresh Token (`TokenPair`)      | Yes (On Windows 7, 8/8.1 and 10) | Yes | Yes | 
| Personal Access Token (`Token`)                | Yes | Yes | Yes |

# How to use this library
Maven is the preferred way to referencing this library.  

```xml
  <dependency>
    <groupId>com.microsoft.a4o</groupId>
    <artifactId>credential-storage</artifactId>
    <version>1.0.0</version>
  </dependency>
```

Here is sample code for [credentials](sample/src/main/java/com/microsoft/a4o/credentialstorage/sample/AppCredential.java) 
and [tokens](sample/src/main/java/com/microsoft/a4o/credentialstorage/sample/AppToken.java) that shows how to use this library.


# How to build
1. JDK 11
2. Maven 3.8+
3. `mvn clean verify`

# License
The MIT license can be found in [LICENSE.txt](LICENSE.txt)

# Contributing
This project welcomes contributions and suggestions. Most contributions require you to
agree to a Contributor License Agreement (CLA) declaring that you have the right to,
and actually do, grant us the rights to use your contribution. For details, visit
https://cla.microsoft.com.

When you submit a pull request, a CLA-bot will automatically determine whether you need
to provide a CLA and decorate the PR appropriately (e.g., label, comment). Simply follow the
instructions provided by the bot. You will only need to do this once across all repositories using our CLA.

# Code of Conduct
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/)
or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
