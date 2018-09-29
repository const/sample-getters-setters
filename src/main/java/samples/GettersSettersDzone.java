package samples;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BiConsumer;
import java.util.function.Function;

// The copy of the code from article https://dzone.com/articles/hacking-lambda-expressions-in-java
public class GettersSettersDzone {

    public static Function createGetter(final MethodHandles.Lookup lookup,
                                        final MethodHandle getter) throws Exception{
        final CallSite site = LambdaMetafactory.metafactory(lookup, "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class), //signature of method Function.apply after type erasure
                getter,
                getter.type()); //actual signature of getter
        try {
            return (Function) site.getTarget().invokeExact();
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Error(e);
        }
    }

    public static BiConsumer createSetter(final MethodHandles.Lookup lookup,
                                          final MethodHandle setter) throws Exception {
        final CallSite site = LambdaMetafactory.metafactory(lookup,
                "accept",
                MethodType.methodType(BiConsumer.class),
                MethodType.methodType(void.class, Object.class, Object.class), //signature of method BiConsumer.accept after type erasure
                setter,
                setter.type()); //actual signature of setter
        try {
            return (BiConsumer) site.getTarget().invokeExact();
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Error(e);
        }
    }
}
