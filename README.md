Hello World
===========
This is a very simple J2EE 7 app, that was used as a proof of concept on using 
Environment variables to configure an application running inside GlassFish.

And it seams to be working, not without a bit of mugging about, but it could be
mad to work.

How it works
============
I won't waste time explaining how the actual hello-world app works, but I will 
explain how Environment variables are configured and used.

Env vs. System-Properties
-------------------------
The [thorsager/glassfish](https://hub.docker.com/r/thorsager/glassfish/) docker-
image that is used as a base for this application, transfers environment 
variables into System-properties before starting 
[GlassFish](https://github.com/thorsager/dockling/tree/master/glassfish).


Getting at the values
---------------------
First off you are able to access the values passed to GlassFish directly using 
`System.properties`, like this:  
```java
System.getProperty("HELLO");
```

Secondly you can add a `glassfish-resources.xml` to you application with 
custom-resources referring to the system-property like this:
```xml
<resources>
    <custom-resource factory-class="org.glassfish.resources.custom.factory.PrimitivesAndStringFactory"
                     res-type="java.lang.String"
                     jndi-name="env.hello">
        <property name="value" value="${HELLO}"/>
    </custom-resource>
</resources>
```
place the file en the `WEB-INF` or the `META-INF` (depending on you app) 
and GlassFish will create these resources on app-deployment. 
The `${HELLO}` will be _resolved_ using the matching system-property, and you 
will be able to access the value from you code like this:
```java
@Resource(lookup="java:app/env.hello")
private String appName;
```

The third and last way is to use `asadmin` on docker-image generation to add 
custom-resources in _server-scope_, this can be done like this:
```dockerfile
RUN asadmin --user=admin start-domain ${DOMAIN_NAME} && \
    echo "AS_ADMIN_PASSWORD=${PASSWORD}" > /tmp/glassfishpwd && \
    asadmin --user=admin --passwordfile=/tmp/glassfishpwd add-resources /tmp/gf-resources.xml && \
    asadmin --user=admin stop-domain ${DOMAIN_NAME} && \
    rm /tmp/gf-resources.xml && \
    rm /tmp/glassfishpwd
```
> The `PASSWORD` and `DOMAIN_NAME` variables are defined by the the [thorsager/glassfish](https://hub.docker.com/r/thorsager/glassfish/) image.

When custom-resources are located in _server-scope_ you are able to access the 
resource from you app like this:
```java
@Resource(lookup="env.hello")
private String glbName;
```

Build it or pull it
===================
To run this example you simple build the app (`mvn package`), build the 
docker image (`docker build -t hello-world .`) and run it.

```bash
docker run -d --name hello-world -p 8080:8080 -e GF_ENV_HELLO=Jimbo hello-world
```

At this point you should be able to navigate to http://localhost:8080/hello-world/resources/hello 
and get the following message:
```text
System.property - Hello Jimbo.
java:app/env.hello: - Hello Jimbo.
env.hello: - Hello Jimbo.
```

If you just want to run it.. pull it from https://hub.docker.com/r/thorsager/gf-docker-helloworld
