package samples;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BiConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;

public class SettersJava11 {

    @SuppressWarnings("unchecked")
    public static BiConsumer createSetter(final MethodHandles.Lookup lookup,
                                          final MethodHandle setter,
                                          final Class<?> valueType) throws Exception {
        try {
            if (valueType.isPrimitive()) {
                if (valueType == double.class) {
                    ObjDoubleConsumer consumer = (ObjDoubleConsumer) createSetterCallSite(
                            lookup, setter, ObjDoubleConsumer.class, double.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (double) b);
                } else if (valueType == int.class) {
                    ObjIntConsumer consumer = (ObjIntConsumer) createSetterCallSite(
                            lookup, setter, ObjIntConsumer.class, int.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (int) b);
                } else if (valueType == long.class) {
                    ObjLongConsumer consumer = (ObjLongConsumer) createSetterCallSite(
                            lookup, setter, ObjLongConsumer.class, long.class).getTarget().invokeExact();
                    return (a, b) -> consumer.accept(a, (long) b);
                } else {
                    // Real code needs to support short, char, boolean, byte, and float according to pattern above
                    throw new RuntimeException("Type is not supported yet: " + valueType.getName());
                }
            } else {
                return (BiConsumer) createSetterCallSite(lookup, setter, BiConsumer.class, Object.class)
                        .getTarget().invokeExact();
            }
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Error(e);
        }
    }

    private static CallSite createSetterCallSite(MethodHandles.Lookup lookup, MethodHandle setter, Class<?> interfaceType, Class<?> valueType) throws LambdaConversionException {
        return LambdaMetafactory.metafactory(lookup,
                "accept",
                MethodType.methodType(interfaceType),
                MethodType.methodType(void.class, Object.class, valueType), //signature of method SomeConsumer.accept after type erasure
                setter,
                setter.type());
    }
}
