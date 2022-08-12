package com.ieami.jedi.core;

public final class BeanCollectionTest {

    public void testDsl() {
        final var beanCollection = createBeanCollection();
        beanCollection
                .addSingleton(IService.class, Service.class)
                .addSingleton(IService.class, Service.class);

        abstraction(IService.class).implementedBy(Service.class).asSingleton();
        abstraction(IService.class)
                .implementedBy(Service.class)
                .decorateWith(ServiceMetricDecorator.class)
                .asSingleton();

    }

    public interface IService { }

    public static class Service implements IService { }

    public static class ServiceMetricDecorator implements IService {

    }


    private BeanCollection createBeanCollection() {
        return null;
    }
}
