package net.lightoze.httpbasic;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Application extends WebApplication {
    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("transactions-optional");

    @Override
    public Class<? extends Page> getHomePage() {
        return GenerateKey.class;
    }

    @Override
    protected void init() {
        super.init();
        getResourceSettings().setResourcePollFrequency(null);
    }

    @Override
    protected ISessionStore newSessionStore() {
        return new HttpSessionStore(this);
    }
}
