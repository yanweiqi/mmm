
package com.mex.bidder.api.vertx.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class that creates the injector and performs the injection.
 */
public class GuiceVerticleHelper {
    private static final Logger logger = LoggerFactory.getLogger(GuiceVerticleHelper.class);


    /**
     * Use the static inject method if your Verticle cannot inherit from GuiceVerticle.
     *
     * @param verticle Your verticle
     * @param vertx    A Vertx instance
     */
    public static void inject(Verticle verticle, Vertx vertx) {
        GuiceVertxBinding modAnnotation = verticle.getClass().getAnnotation(GuiceVertxBinding.class);

        if (null == modAnnotation) {
            String msg = "GuiceVerticle " + verticle.getClass().getName() + " did not declare a Module with @VertxModule()";
        }

        if (null == modAnnotation.modules()) {
            String msg = "@VertxModule() for " + verticle.getClass().getName() + "  did not declare a Module(s).";
        }

        try {
            List<Module> mods = new ArrayList<Module>();
            mods.add(new GuiceVertxModule(vertx));
            for (Class<?> modClass : modAnnotation.modules()) {
                VertxModule m = (VertxModule) modClass.newInstance();
                m.setVertx(vertx);
                mods.add(m);
            }

            Injector injector = Guice.createInjector(mods);
            injector.injectMembers(verticle);

        } catch (Exception ex) {
            logger.error("inject error.", ex);
        }
    }
}
