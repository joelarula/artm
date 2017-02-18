package website.services;

import java.io.File;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.tapestry5.TapestryFilter;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import website.services.impl.Utf8Filter;

public class Server {
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public static void main(String[] args) throws Exception {


		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
		
		final ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setServer(server);
        errorHandler.setShowStacks(false);
        server.addBean(errorHandler);
 
               
        server.setStopAtShutdown(true);
        server.setStopTimeout(30*1000l);
		        
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setSecurityHandler(basicAuth());
        handler.setClassLoader(Thread.currentThread().getContextClassLoader());
        handler.setContextPath("/");
        handler.setInitParameter("tapestry.app-package", "website");
      
        HandlerCollection collection = new HandlerCollection();
        collection.addHandler(handler);

             
        FilterHolder filterHolder = new FilterHolder();
        filterHolder.setHeldClass(TapestryFilter.class);
        filterHolder.setName("website");
       
        handler.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        								
		server.setHandler(handler);	
		 
        server.start();        
        server.join();
	}
	
    private static final SecurityHandler basicAuth() {

    	HashLoginService l = new HashLoginService("website",getAuthPropertiesPath());
    	
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"admin"});
        constraint.setAuthenticate(true);
         
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/admin/*");
        
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("website");
        csh.addConstraintMapping(cm);
        csh.setLoginService(l);
        
        return csh;
    	
    }

	private static String getAuthPropertiesPath() {
		File f = new File(WebsiteModule.websiteFolder+File.separator+"login.properties");
		logger.info("auth {} {}",f.exists(), f.getAbsolutePath());
		return f.getAbsolutePath();
	}
}
