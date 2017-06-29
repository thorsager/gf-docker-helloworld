package dk.krakow.hello;


import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * The A simple demonstration of how to use ENV vars when
 * running GlassFish4 in Docker
 * @author Michael Thorsager, github.com/thorsager
 */
@Path("hello")
public class HelloResource {
    @Resource(lookup="java:app/env.hello")
    private String appName;

    @Resource(lookup="env.hello")
    private String glbName;

    @GET
    @Produces("text/plain")
    public String all() {
        String name = System.getProperty("HELLO");
        return "System.property - Hello "+(name==null ? "World" : name)+".\n"+
               "java:app/env.hello: - Hello "+(this.appName==null ? "world" : this.appName)+".\n"+
               "env.hello: - Hello "+(this.glbName==null ? "world" : this.glbName)+".";
    }
}
