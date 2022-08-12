package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Abstraction;

import static com.ieami.jedi.dsl.Abstraction.abstraction;

public final class BeanCollectionTest {

    public void testDsl() {
        final var beanCollection = createBeanCollection();
        final var f = abstraction(IService.class)
                .implementedBy(Service.class)
                .asSingleton();

        beanCollection.addSingleton(IService.class, Service.class);

//        abstraction(IService.class).implementedBy(Service.class).asSingleton();
//        abstraction(IService.class)
//                .implementedBy(Service.class)
//                .decorateWith(ServiceMetricDecorator.class)
//                .asSingleton();

    }

    public interface IService { }

    public static class Service implements IService { }

    public static class ServiceMetricDecorator implements IService {

    }


    private DependencyCollection createBeanCollection() {
        return null;
    }
}
