package samples;

import org.junit.Before;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class GettersSettersTest {
    private PropertyDescriptor nameProperty;
    private PropertyDescriptor valueProperty;

    @Before
    public void init() throws Exception {
        final BeanInfo beanInfo = Introspector.getBeanInfo(ResultDto.class);
        final Function<String, PropertyDescriptor> property = name -> Stream.of(beanInfo.getPropertyDescriptors())
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found: " + name));
        nameProperty = property.apply("name");
        valueProperty = property.apply("value");
    }


    @Test
    public void testGetter() throws Exception {
        final ResultDto dto = new ResultDto();
        dto.setName("Answer");
        dto.setValue(42);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final Function nameGetter = GettersSettersDzone.createGetter(lookup,
                lookup.unreflect(nameProperty.getReadMethod()));
        final Function valueGetter = GettersSettersDzone.createGetter(lookup,
                lookup.unreflect(valueProperty.getReadMethod()));
        assertEquals("Answer", nameGetter.apply(dto));
        assertEquals(42.0, (double) valueGetter.apply(dto), 0.1);
    }

    @Test
    public void testSetter() throws Exception {
        final ResultDto dto = new ResultDto();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final BiConsumer nameSetter = GettersSettersDzone.createSetter(lookup,
                lookup.unreflect(nameProperty.getWriteMethod()));
        final BiConsumer valueSetter = GettersSettersDzone.createSetter(lookup,
                lookup.unreflect(valueProperty.getWriteMethod()));
        nameSetter.accept(dto, "Answer");
        valueSetter.accept(dto, 42.0);
        assertEquals("Answer", dto.getName());
        assertEquals(42.0, dto.getValue(), 0.1);
    }

    @Test
    public void testSetterJava11() throws Exception {
        final ResultDto dto = new ResultDto();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final BiConsumer nameSetter = SettersJava11.createSetter(lookup,
                lookup.unreflect(nameProperty.getWriteMethod()), nameProperty.getPropertyType());
        final BiConsumer valueSetter = SettersJava11.createSetter(lookup,
                lookup.unreflect(valueProperty.getWriteMethod()), valueProperty.getPropertyType());
        nameSetter.accept(dto, "Answer");
        valueSetter.accept(dto, 42.0);
        assertEquals("Answer", dto.getName());
        assertEquals(42.0, dto.getValue(), 0.1);
    }

    public static class ResultDto {
        private double value;
        private String name;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
