package dev.timduerr.openapigeneratorexample.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Sort;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * SortHelper.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public class SortHelper {

    private SortHelper() {
    }

    /**
     * Determines if a given property of a DTO (Data Transfer Object) class is exposed,
     * meaning it is both readable or writable and not explicitly ignored or hidden.
     *
     * <p>This method checks for property visibility, annotations indicating ignorance
     * (e.g., {@code JsonIgnore}, {@code Schema.hidden()}), and the presence of valid
     * accessor methods (getter and setter).
     *
     * @param dtoClass the class of the DTO to analyze.
     * @param property the name of the property to check.
     * @return {@code true} if the property is exposed, {@code false} otherwise.
     */
    public static boolean isDtoExposedProperty(Class<?> dtoClass, String property) {
        if (property == null || property.isBlank()) return false;

        BeanWrapper beanWrapper = new BeanWrapperImpl(dtoClass);
        if (!(beanWrapper.isReadableProperty(property) || beanWrapper.isWritableProperty(property))) return false;

        if (isIgnoredOnField(dtoClass, property)) return false;
        PropertyDescriptor pd = getPropertyDescriptor(dtoClass, property);
        return pd == null || (!isIgnored(pd.getReadMethod()) && !isIgnored(pd.getWriteMethod()));
    }

    /**
     * Determines if a specified property of a given class is writable,
     * indicating the property can be modified.
     *
     * <p>This method uses a bean introspection mechanism to check
     * the writability of the property.
     *
     * @param clazz the class to inspect.
     * @param property the name of the property to check.
     * @return {@code true} if the property is writable; {@code false} otherwise.
     */
    public static boolean isSortableProperty(Class<?> clazz, String property) {
        if (property == null || property.isBlank()) return false;
        BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
        return beanWrapper.isWritableProperty(property);
    }

    /**
     * Resolves the sort order based on the provided class, DTO class, and sort parameter.
     *
     * <p>This method determines the effective property for sorting and the sort direction
     * (ascending or descending) based on `sortParam`. If the property is valid and
     * exposed in both `clazz` and `dtoClass`, it will be used; otherwise, a default property
     * ("title") is used.
     *
     * @param clazz the class containing the properties to be checked for sortability.
     * @param dtoClass the DTO class containing the properties to be checked for exposure.
     * @param sortParam a string representing the sort property and direction
     *                  (e.g., "-name" for descending order on "name").
     * @return a {@link Sort.Order} object representing the sort property and direction.
     */
    public static Sort.Order resolveSortOrder(Class<?> clazz, Class<?> dtoClass, String sortParam) {
        String effectiveProperty = "title";
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParam != null && !sortParam.isBlank()) {
            String sortString = sortParam.trim();

            if (sortString.startsWith("-")) {
                direction = Sort.Direction.DESC;
                sortString = sortString.substring(1);
            } else if (sortString.startsWith("+")) {
                sortString = sortString.substring(1);
            }

            if (isSortableProperty(clazz, sortString) && isDtoExposedProperty(dtoClass, sortString)) {
                effectiveProperty = sortString;
            }
        }

        return new Sort.Order(direction, effectiveProperty);
    }

    // Private helper methods
    /**
     * Determines if a specified field in a given class is marked to be ignored or hidden.
     *
     * <p>This method checks if the field has the {@code JsonIgnore} annotation or if the
     * {@code Schema} annotation is present and marked as hidden.
     *
     * @param dtoClass the class containing the field to check.
     * @param name the name of the field to evaluate.
     * @return {@code true} if the field is ignored or hidden; {@code false} otherwise.
     */
    private static boolean isIgnoredOnField(Class<?> dtoClass, String name) {
        try {
            Field f = dtoClass.getDeclaredField(name);
            if (f.isAnnotationPresent(JsonIgnore.class)) return true;
            Schema s = f.getAnnotation(Schema.class);
            return s != null && s.hidden();
        } catch (NoSuchFieldException ignored) { }
        return false;
    }

    /**
     * Retrieves the {@link PropertyDescriptor} for a specified property name in a given class.
     *
     * <p>This method uses Java Bean introspection to locate the descriptor of the property
     * by name, which contains metadata such as getter and setter methods.
     *
     * @param clazz the class to inspect for property descriptors.
     * @param name the name of the property to retrieve.
     * @return the {@link PropertyDescriptor} for the specified property, or {@code null} if not found.
     */
    private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String name) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (pd.getName().equals(name)) return pd;
            }
        } catch (Exception ignored) { }
        return null;
    }

    /**
     * Determines if the specified method is marked as ignored or hidden.
     *
     * <p>This method checks if the method is annotated with {@code JsonIgnore}
     * or if the {@code Schema} annotation is present and marked as hidden.
     *
     * @param m the method to evaluate. If {@code null}, the method is not considered ignored.
     * @return {@code true} if the method is marked as ignored or hidden, {@code false} otherwise.
     */
    private static boolean isIgnored(Method m) {
        if (m == null) return false;
        if (m.isAnnotationPresent(JsonIgnore.class)) return true;
        Schema s = m.getAnnotation(Schema.class);
        return s != null && s.hidden();
    }
}
