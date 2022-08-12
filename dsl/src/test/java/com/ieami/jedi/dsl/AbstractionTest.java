package com.ieami.jedi.dsl;

import static com.ieami.jedi.dsl.Abstraction.abstraction;

public final class AbstractionTest {

    public void functionalityTest() {
        final var f = abstraction(IService.class)
                .implementedBy(Service.class)
                .asSingleton();
    }

    public interface IService{}
    public static class Service implements IService{}
    public static class ServiceMetricDecorator implements IService{}
}
