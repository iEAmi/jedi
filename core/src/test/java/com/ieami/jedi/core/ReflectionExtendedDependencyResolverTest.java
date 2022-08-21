package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.DuplicateDependencyException;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.HashMap;

public final class ReflectionExtendedDependencyResolverTest {

    private interface TestRepository {
        final class Impl implements TestRepository {
            public Impl() {
            }
        }

        final class SqlImpl implements TestRepository {
            public SqlImpl() {
            }
        }

        final class RedisImpl implements TestRepository {
            public RedisImpl() {
            }
        }
    }

    private interface TestService {
        final class Impl implements TestService {
            public Impl(TestRepository repository) {
            }
        }
    }

    private static class TestCollectionInjection {
        public TestCollectionInjection(TestRepository[] repositories) {

        }
    }

    private static class AService {
        public AService() {

        }
    }

    private static class BService {
        public BService() {

        }
    }

    private static class CService {
        public CService(AService aService, BService bService) {

        }
    }

    private interface GenericService<T> {
        class _String implements GenericService<String> {
            public _String() {
            }
        }

        class _Integer implements GenericService<Integer> {
            public _Integer() {
            }
        }

        class _Long implements GenericService<Long> {
            public _Long() {
            }
        }
    }

    private abstract static class AbstractGenericService<T> {
        static class _Integer extends AbstractGenericService<Integer> {
            public _Integer() {
            }
        }
    }

    private static class IntegerStringHashMap extends HashMap<Integer, String> {
        public IntegerStringHashMap() {
        }
    }

    @Test
    public void resolve_returns_null_for_unregister_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolve(TestService.class, TestService.Impl.class);

            Assert.assertNull(instance);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveRequired_throws_IllegalStateException_unregister_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final ThrowingRunnable runnableToTest = () -> depResolver.resolveRequired(TestService.class, TestService.Impl.class);

            Assert.assertThrows(IllegalStateException.class, runnableToTest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveOptional_returns_empty_Optional_unregister_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolveOptional(TestService.class, TestService.Impl.class);

            Assert.assertTrue(instance.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolve_returns_instance_for_registered_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addTransientUnsafe(TestService.class, TestService.Impl.class);

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolve(TestService.class, TestService.Impl.class);

            Assert.assertNotNull(instance);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveRequired_returns_instance_for_registered_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addTransientUnsafe(TestService.class, TestService.Impl.class);

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolveRequired(TestService.class, TestService.Impl.class);

            Assert.assertNotNull(instance);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveOptional_returns_not_empty_optional_for_registered_classReference_abstraction() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addTransientUnsafe(TestService.class, TestService.Impl.class);

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolveOptional(TestService.class, TestService.Impl.class);

            Assert.assertFalse(instance.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolve_not_supports_implicit_service_resolving() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addTransientUnsafe(TestService.class, TestService.Impl.class);

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolve(TestService.Impl.class);

            Assert.assertNull(instance);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolve_returns_instance_for_registered_funcReference_abstraction() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addTransientUnsafe(TestService.class, TestService.Impl.class, dependencyResolver -> {
            final var repository = dependencyResolver.resolveRequiredUnsafe(TestRepository.class, TestRepository.Impl.class);
            return new TestService.Impl(repository);
        });

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolveOptional(TestService.class, TestService.Impl.class);

            Assert.assertFalse(instance.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolve_with_two_arguments_resolves_particular_implementation_of_abstraction() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.SqlImpl.class);
        final var resolver = depCollection.buildUnsafe();

        final var repository = resolver.resolveUnsafe(TestRepository.class, TestRepository.Impl.class);

        Assert.assertNotNull(repository);

        final var sqlRepository = resolver.resolveUnsafe(TestRepository.class, TestRepository.SqlImpl.class);

        Assert.assertNotNull(sqlRepository);
    }

    @Test
    public void resolveAll_returns_all_implementation() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.SqlImpl.class, dependencyResolver -> new TestRepository.SqlImpl());
        depCollection.addSingletonUnsafe(TestRepository.class, new TestRepository.RedisImpl());

        final var resolver = depCollection.buildUnsafe();
        final var repositories = resolver.resolveAllUnsafe(TestRepository.class);

        Assert.assertNotNull(repositories);
        Assert.assertEquals(3, repositories.length);
    }

    @Test
    public void resolve_with_single_argument_resolves_abstraction_with_one_implementation() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.Impl.class);

        final var resolver = depCollection.buildUnsafe();
        final var repository = resolver.resolveUnsafe(TestRepository.class);

        Assert.assertNotNull(repository);
    }

    @Test
    public void resolve_with_single_argument_could_not_resolve_abstraction_with_more_than_one_implementation() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.SqlImpl.class);

        final var resolver = depCollection.buildUnsafe();
        final ThrowingRunnable repository = () -> resolver.resolve(TestRepository.class);

        Assert.assertThrows(MoreThanOneDependencyException.class, repository);
    }

    @Test
    public void addDependency_detects_duplicate_dependencies() {
        final var depCollection = DependencyCollection.newDefault();
        final ThrowingRunnable functionToTest = () -> {
            depCollection.addSingleton(TestRepository.class, TestRepository.Impl.class);
            depCollection.addSingleton(TestRepository.class, TestRepository.Impl.class);
        };

        Assert.assertThrows(DuplicateDependencyException.class, functionToTest);
    }

    @Test
    public void addDependency_not_detects_duplicate_dependencies_when_implementation_is_different() {
        final var depCollection = DependencyCollection.newDefault();
        try {
            depCollection.addSingleton(TestRepository.class, TestRepository.Impl.class);
            depCollection.addSingleton(TestRepository.class, TestRepository.SqlImpl.class);
        } catch (DuplicateDependencyException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolve_returns_valid_instance_for_dependency_with_constructor_contains_array() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.Impl.class);
        depCollection.addSingletonUnsafe(TestRepository.class, TestRepository.SqlImpl.class);
        depCollection.addSingletonUnsafe(TestCollectionInjection.class);
        final var depResolver = depCollection.buildUnsafe();

        final var instance = depResolver.resolveUnsafe(TestCollectionInjection.class);

        Assert.assertNotNull(instance);
    }

    @Test
    public void resolve_returns_valid_instance_for_dependency_with_more_than_one_argument() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(AService.class);
        depCollection.addSingletonUnsafe(BService.class);
        depCollection.addSingletonUnsafe(CService.class);
        final var depResolver = depCollection.buildUnsafe();

        final var instance = depResolver.resolveUnsafe(CService.class);

        Assert.assertNotNull(instance);
    }

    @Test
    public void resolveAll_returns_all_instances_for_generic_dependencies() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(GenericService.class, GenericService._String.class);
        depCollection.addSingletonUnsafe(GenericService.class, GenericService._Integer.class, dependencyResolver -> new GenericService._Integer());
        depCollection.addSingletonUnsafe(GenericService.class, new GenericService._Long());
        depCollection.addSingletonUnsafe(AbstractGenericService.class, AbstractGenericService._Integer.class);
        depCollection.addSingletonUnsafe(HashMap.class, IntegerStringHashMap.class);
        final var depResolver = depCollection.buildUnsafe();

        final var genericServiceInstances = depResolver.resolveAllUnsafe(GenericService.class);
        final var abstractGenericServiceInstances = depResolver.resolveAllUnsafe(AbstractGenericService.class);
        final var hashmapInstances = depResolver.resolveAllUnsafe(HashMap.class);

        Assert.assertEquals(3, genericServiceInstances.length);
        Assert.assertEquals(1, abstractGenericServiceInstances.length);
        Assert.assertEquals(1, hashmapInstances.length);
    }

    @Test
    public void resolve_returns_particular_instance_with_generic_params() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingletonUnsafe(GenericService.class, GenericService._String.class);
        depCollection.addSingletonUnsafe(GenericService.class, GenericService._Integer.class, dependencyResolver -> new GenericService._Integer());
        depCollection.addSingletonUnsafe(GenericService.class, new GenericService._Long());
        depCollection.addSingletonUnsafe(AbstractGenericService.class, AbstractGenericService._Integer.class);
        depCollection.addSingletonUnsafe(HashMap.class, IntegerStringHashMap.class);
        final var depResolver = depCollection.buildUnsafe();

        final var stringInstance = depResolver.resolveUnsafe(GenericService.class, GenericService._String.class);
        final var integerInstance = depResolver.resolveUnsafe(GenericService.class, GenericService._Long.class);
        final var longInstance = depResolver.resolveUnsafe(GenericService.class, GenericService._Integer.class);

        Assert.assertNotNull(stringInstance);
        Assert.assertNotNull(integerInstance);
        Assert.assertNotNull(longInstance);
    }
}
