# Writing your first application
Create your first Spring Boot project to manage invoices for our customers.

![Hexagonal Architecture Diagram](./diagram1.png)
## Step 1. Skeleton app

Let's create a skeleton app from the Spring Boot Starter [page](https://start.spring.io/).
- Explore various dependencies available
- Select `Maven project`
- Choose dependencies: **Spring Web**, **H2 Database**, **Spring Data JPA**, **Spring Security**, **Spring Boot DevTools**
- Select **Java 17**
- Choose **Spring Boot 3.0.0**
- GroupId: `com.mih.training`
- Artifact: `invoice-api`
- Package name: `com.mih.training.invoice`

## Import project
- Unzip the zip you generated in step 1.
- Right click `pom.xml` and `Import as maven project`
- Run `mvn install`

## Invoice tracker

Our application is compatible with the [UBL 2.3](http://docs.oasis-open.org/ubl/os-UBL-2.3/) format.
You can find:
- A zip with all resources from [here.](http://docs.oasis-open.org/ubl/os-UBL-2.3/UBL-2.3.zip)
- A UBL [presentation](http://docs.oasis-open.org/ubl/os-UBL-2.3/UBL-2.3.pdf) describing various flows.
- Unzip the `UBL-2.3.zip` file to `src/main/resources/`
- Generate Java classes based on the XSD files
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- Plugin required to build java classes from XSD using XJC -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxb2-maven-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>xjc</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <sources>
                        <source>src/main/resources/UBL-2.3/xsd/maindoc/UBL-Invoice-2.3.xsd</source>
                    </sources>
                    <!--                    <packageName>io.github.invoice</packageName>-->
                    <clearOutputDir>true</clearOutputDir>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
- Add dependencies

```xml
        <dependency>
            <groupId>jakarta.xml.bind</groupId>
            <artifactId>jakarta.xml.bind-api</artifactId>
            <version>4.0.0</version>
        </dependency>
        <dependency>
            <groupId>jakarta.activation</groupId>
            <artifactId>jakarta.activation-api</artifactId>
            <version>2.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jaxb</groupId>
            <artifactId>jaxb-runtime</artifactId>
            <version>4.0.0</version>
            <scope>runtime</scope>
        </dependency>
```
- Run `mvn install -DskipTests`. You can see an executable jar was created in your maven repository
```text
.m2/repository/com/mih/training/invoice-api/0.0.1-SNAPSHOT/invoice-api-0.0.1-SNAPSHOT.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  30.104 s
[INFO] Finished at: 2022-10-27T16:21:40+03:00
[INFO] ------------------------------------------------------------------------

```
- Check UBL classes are generated. You may need to reload the project (`Reload All Maven Projects`)

![Ubl classes](./ubl-classes.png)

- Incoming files need to be deserialized into `InvoiceXml` instances
```java
package com.mih.training.invoice.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

@XmlRootElement(name = "Invoice", namespace = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")
public class InvoiceXml extends InvoiceType {

}
```

## Exercises

There are multiple tests failing in the project. You need to fix them:
![Failing tests](./failing-tests.png)

1. Run InvoiceApiApplicationTests. Check the logs to understand what happens.
```text
2022-10-28T09:28:53.567+03:00  INFO 75151 --- [           main] c.m.t.i.InvoiceApiApplicationTests       : Started InvoiceApiApplicationTests in 1.004 seconds (process running for 4.903)
```
2. Fix InvoiceXmlConverterTest
3. Fix InvoiceCommandLineRunnerTest. Find a way to process incoming files for a given location
4. Fix InvoiceRepositoryTest. _Hint_:
Check how spring data works - start the application, search in the logs the automatically generated password and go to **http://localhost:8080/h2-console**
```text
2022-10-28T09:09:09.010+03:00  WARN 73083 --- [  restartedMain] .s.s.UserDetailsServiceAutoConfiguration : 

Using generated security password: 85818375-1f1f-41e0-a130-69e7de64d1db

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```
In the logs you also find the connection details for the database
```text
2022-10-28T09:09:07.667+03:00  INFO 73083 --- [  restartedMain] o.s.b.a.h2.H2ConsoleAutoConfiguration    : H2 console available at '/h2-console'. Database available at 'jdbc:h2:mem:a3fc2bc6-a34b-4bb3-b14f-d72692eb5d32'
```
5. Fix InvoiceXmlToInvoiceConverterTest. Build our `AntiCorruption Layer` between external UBL language and `Invoice` internal domain representation. Suggestion to use `mapstruct`
```xml

<properties>
    <org.mapstruct.version>1.5.3.Final</org.mapstruct.version>
    <!-- ... -->
</properties>
<dependencies>
<!-- ... -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${org.mapstruct.version}</version>
        </dependency>
</dependencies>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.6.0</version>
        <configuration>
            <source>${java.version}</source> <!-- or higher, depending on your project -->
            <target>${java.version}</target> <!-- or higher, depending on your project -->
            <annotationProcessorPaths>
                <path>
                    <groupId>org.mapstruct</groupId>
                    <artifactId>mapstruct-processor</artifactId>
                    <version>${org.mapstruct.version}</version>
                </path>
                <!-- other annotation processors -->
            </annotationProcessorPaths>
        </configuration>
    </plugin>

</plugins>
```

6. Fix InvoiceResourceTest. 
- Return all invoices from repository `testGetAll`
- Add pagination `testWithFilters`
- Get invoice by Id `testById`
