package com.mex.bidder.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

/**
 * Utilities for loading classes and Guice bindings.
 */
public final class GuiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(GuiceUtils.class);

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    private GuiceUtils() {
    }

    /**
     * @return {@link ImmutableList} of instances from the specified {@link Injector} of the
     * specified superclass from a comma-delimited list of classes.
     */
    public static <T> ImmutableList<? extends T> loadInstances(
            Injector injector,
            Class<T> baseClass,
            String classNames) {

        return loadInstances(injector, baseClass, parseClassNames(classNames));
    }

    public static <T> ImmutableList<? extends T> loadInstances(
            Injector injector,
            Class<T> baseClass,
            Iterable<String> classNames) {

        return getInstances(injector, loadClasses(baseClass, classNames));
    }

    /**
     * @return {@link ImmutableList} of class names from a comma-delimited list.
     */
    public static ImmutableList<String> parseClassNames(String classNames) {
        return ImmutableList.copyOf(SPLITTER.split(classNames));
    }

    /**
     * @return {@link ImmutableList} of {@link Class} objects from the passed in names for
     * classes that could be loaded
     */
    public static <T> ImmutableList<Class<? extends T>> loadClasses(
            Class<T> baseClass, String... classNames) {
        return loadClasses(baseClass, asList(classNames));
    }

    public static <T> ImmutableList<Class<? extends T>> loadClasses(
            Class<T> baseClass,
            Iterable<String> classNames) {

        checkNotNull(baseClass);
        ImmutableList.Builder<Class<? extends T>> builder = ImmutableList.builder();
        for (String className : classNames) {
            try {
                @SuppressWarnings("unchecked")
                Class<T> klass = (Class<T>) Class.forName(className);
                if (!baseClass.isAssignableFrom(klass)) {
                    throw new IllegalArgumentException(String.format("Class %s is not a subclass of %s",
                            className, baseClass.getName()));
                } else {
                    builder.add(klass);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Class not found: {}", className);
            }
        }
        return builder.build();
    }

    public static <T> ImmutableList<? extends T> getInstances(
            Injector injector,
            Iterable<Class<? extends T>> classes) {

        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (Class<? extends T> klass : classes) {
            builder.add(injector.getInstance(klass));
        }
        return builder.build();
    }
}
