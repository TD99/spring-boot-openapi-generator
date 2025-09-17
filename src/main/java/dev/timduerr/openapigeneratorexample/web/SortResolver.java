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
 * SortResolver.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public class SortResolver {

    private SortResolver() {
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
     * Checks if a specified property in a given class is sortable.
     *
     * <p>This method determines whether the specified property is readable
     * and non-blank in the provided class.
     *
     * @param clazz the class to inspect for the property.
     * @param property the name of the property to check.
     * @return {@code true} if the property is sortable, {@code false} otherwise.
     */
    public static boolean isSortableProperty(Class<?> clazz, String property) {
        if (property == null || property.isBlank()) return false;
        BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
        return beanWrapper.isReadableProperty(property);
    }

    /**
     * Represents the result of a sorting resolution process.
     *
     * <p>This record encapsulates the resolved {@link Sort} configuration, the key used for sorting,
     * and the direction in which the sort is applied.
     *
     * @param sort the resolved {@link Sort} object, containing the sort configuration.
     * @param appliedKey the key or property name used for sorting.
     * @param appliedDirection the direction of the sorting, either ascending or descending.
     */
    public record SortResolution(Sort sort, String appliedKey, Sort.Direction appliedDirection) {}

    /**
     * Represents a default sorting configuration with a property key and sort direction.
     *
     * <p>This record is used to define the default sorting behavior for a given entity or DTO.
     *
     * @param key       the name of the property to sort by.
     * @param direction the direction of the sort, either ascending or descending.
     */
    public record DefaultSort(String key, Sort.Direction direction) {}

    /**
     * Resolves sorting parameters into a {@link SortResolution} object based on the provided DTO class,
     * entity class, sort parameter, and default sort configuration.
     *
     * <p>It ensures that the resolved sort property is valid and exposed in both the DTO and entity classes,
     * falling back to default sort settings if no valid sort parameter is provided.
     *
     * @param dtoClass the class of the Data Transfer Object used for validation.
     * @param entityClass the class of the entity used for validation.
     * @param sortParam the sorting parameter provided as input, which may include a direction prefix.
     * @param defaultSort the default sorting configuration to apply when no valid sort parameter is given.
     * @return a {@link SortResolution} object containing the resolved sort configuration, property, and direction.
     */
    public static SortResolution resolve(Class<?> dtoClass, Class<?> entityClass, String sortParam, DefaultSort defaultSort) {
        String effectiveProperty = defaultSort.key();
        Sort.Direction direction = defaultSort.direction();

        if (sortParam != null && !sortParam.isBlank()) {
            String sortString = sortParam.trim();

            // Determine sort direction
            if (sortString.startsWith("-")) {
                direction = Sort.Direction.DESC;
                sortString = sortString.substring(1);
            } else if (sortString.startsWith("+")) {
                sortString = sortString.substring(1);
            }

            // Check for an exact match or case-insensitive match for exposed property
            if (isSortableProperty(entityClass, sortString) && isDtoExposedProperty(dtoClass, sortString)) {
                effectiveProperty = sortString;
            } else {
                String sortStringLowercase = sortString.toLowerCase();
                if (isSortableProperty(entityClass, sortStringLowercase) && isDtoExposedProperty(dtoClass, sortStringLowercase)) {
                    effectiveProperty = sortStringLowercase;
                }
            }
        }

        Sort sort = Sort.by(new Sort.Order(direction, effectiveProperty));
        return new SortResolution(sort, effectiveProperty, direction);
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
