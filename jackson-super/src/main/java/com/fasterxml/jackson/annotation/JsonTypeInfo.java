package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for configuring details of if and how type information is
 * used with JSON serialization and deserialization, to preserve information
 * about actual class of Object instances. This is necessarily for polymorphic
 * types, and may also be needed to link abstract declared types and matching
 * concrete implementation.
 *<p>
 * Some examples of typical annotations:
 *<pre>
 *  // Include Java class name ("com.myempl.ImplClass") as JSON property "class"
 *  &#064;JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
 *
 *  // Include logical type name (defined in impl classes) as wrapper; 2 annotations
 *  &#064;JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
 *  &#064;JsonSubTypes({com.myemp.Impl1.class, com.myempl.Impl2.class})
 *</pre>
 * Alternatively you can also define fully customized type handling by using
 * <code>&#064;JsonTypeResolver</code> annotation (from databind package).
 *<p>
 * This annotation can be used both for types (classes) and properties.
 * If both exist, annotation on property has precedence, as it is
 * considered more specific.
 *<p>
 * When used for properties (fields, methods), this annotation applies
 * to <b>values</b>: so when applied to structure types
 * (like {@link java.util.Collection}, {@link java.util.Map}, arrays),
 * will apply to contained values, not the container;
 * for non-structured types there is no difference.
 * This is identical to how JAXB handles type information
 * annotations; and is chosen since it is the dominant use case.
 * There is no per-property way to force type information to be included
 * for type of container (structured type); for container types one has
 * to use annotation for type declaration.
 *<p>
 * Note on visibility of type identifier: by default, deserialization
 * (use during reading of JSON) of type identifier
 * is completely handled by Jackson, and is <b>not passed to</b>
 * deserializers. However, if so desired,
 * it is possible to define property <code>visible = true</code>
 * in which case property will be passed as-is to deserializers
 * (and set via setter or field) on deserialization.
 *<p>
 * On serialization side, Jackson will generate type id by itself,
 * except if there is a property with name that matches
 * {@link #property()}, in which case value of that property is
 * used instead.
 *
 * @author vegegoku
 * @version $Id: $Id
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE,
    ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonTypeInfo
{    
    /*
    /**********************************************************
    /* Value enumerations used for properties
    /**********************************************************
     */
    
    /**
     * Definition of different type identifiers that can be included in JSON
     * during serialization, and used for deserialization.
     */
    public enum Id {
        /**
         * This means that no explicit type metadata is included, and typing is
         * purely done using contextual information possibly augmented with other
         * annotations.
         */
        NONE(null),

        /**
         * Means that fully-qualified Java class name is used as the type identifier.
         */
        CLASS("@class"),

        /**
         * Means that Java class name with minimal path is used as the type identifier.
         * Minimal means that only the class name, and that part of preceding Java
         * package name is included that is needed to construct fully-qualified name
         * given fully-qualified name of the declared supertype; additionally a single
         * leading dot ('.') must be used to indicate that partial class name is used.
         * For example, for supertype "com.foobar.Base", and concrete type
         * "com.foo.Impl", only ".Impl" would be included; and for "com.foo.impl.Impl2"
         * only ".impl.Impl2" would be included.
         *<br>
         * <b>NOTE</b>: leading dot ('.') MUST be used to denote partial (minimal) name;
         * if it is missing, value is assumed to be fully-qualified name. Fully-qualified
         * name is used in cases where subtypes are not in same package (or sub-package
         * thereof) as base class.
         *<p>
         * If all related classes are in the same Java package, this option can reduce
         * amount of type information overhead, especially for small types.
         * However, please note that using this alternative is inherently risky since it
         * assumes that the
         * supertype can be reliably detected. Given that it is based on declared type
         * (since ultimate supertype, <code>java.lang.Object</code> would not be very
         * useful reference point), this may not always work as expected.
         */
        MINIMAL_CLASS("@c"),

        /**
         * Means that logical type name is used as type information; name will then need
         * to be separately resolved to actual concrete type (Class).
         */
        NAME("@type"),

        /**
         * Means that typing mechanism uses customized handling, with possibly
         * custom configuration. This means that semantics of other properties is
         * not defined by Jackson package, but by the custom implementation.
         */
        CUSTOM(null)
        ;

        private final String _defaultPropertyName;

        private Id(String defProp) {
            _defaultPropertyName = defProp;
        }

        public String getDefaultPropertyName() { return _defaultPropertyName; }
    }

    /**
     * Definition of standard type inclusion mechanisms for type metadata.
     * Used for standard metadata types, except for {@link Id#NONE}.
     * May or may not be used for custom types ({@link Id#CUSTOM}).
     */
    public enum As {
        /**
         * Inclusion mechanism that uses a single configurable property, included
         * along with actual data (POJO properties) as a separate meta-property.
         * <p>
         * Default choice for inclusion.
         */
        PROPERTY,

        /**
         * Inclusion mechanism that wraps typed JSON value (POJO
         * serialized as JSON) in
         * a JSON Object that has a single entry,
         * where field name is serialized type identifier,
         * and value is the actual JSON value.
         *<p>
         * Note: can only be used if type information can be serialized as
         * String. This is true for standard type metadata types, but not
         * necessarily for custom types.
         */
        WRAPPER_OBJECT,

        /**
         * Inclusion mechanism that wraps typed JSON value (POJO
         * serialized as JSON) in
         * a 2-element JSON array: first element is the serialized
         * type identifier, and second element the serialized POJO
         * as JSON Object.
         */
        WRAPPER_ARRAY,

        /**
         * Inclusion mechanism similar to <code>PROPERTY</code>, except that
         * property is included one-level higher in hierarchy, i.e. as sibling
         * property at same level as JSON Object to type.
         * Note that this choice <b>can only be used for properties</b>, not
         * for types (classes). Trying to use it for classes will result in
         * inclusion strategy of basic <code>PROPERTY</code> instead.
         */
        EXTERNAL_PROPERTY,

        /**
         * Inclusion mechanism similar to <code>PROPERTY</code> with respect
         * to deserialization; but one that is produced by a "regular" accessible
         * property during serialization. This means that <code>TypeSerializer</code>
         * will do nothing, and expects a property with defined name to be output
         * using some other mechanism (like default POJO property serialization, or
         * custom serializer).
         *<p>
         * Note that this behavior is quite similar to that of using JsonTypeId
         * annotation;
         * except that here <code>TypeSerializer</code> is basically suppressed;
         * whereas with JsonTypeId, output of regular property is suppressed.
         * This mostly matters with respect to output order; this choice is the only
         * way to ensure specific placement of type id during serialization.
         * 
         * @since 2.3.0 but databind <b>only since 2.5.0</b>.
         */
        EXISTING_PROPERTY
        ;
    }
    
    /*
    /**********************************************************
    /* Annotation properties
    /**********************************************************
     */
    
    /**
     * Specifies kind of type metadata to use when serializing
     * type information for instances of annotated type
     * and its subtypes; as well as what is expected during
     * deserialization.
     *
     * @return a {@link com.fasterxml.jackson.annotation.JsonTypeInfo.Id} object.
     */
    public Id use();    
    
    /**
     * Specifies mechanism to use for including type metadata (if any; for
     * {@link com.fasterxml.jackson.annotation.JsonTypeInfo.Id#NONE} nothing is included); used when serializing,
     * and expected when deserializing.
     *<p>
     * Note that for type metadata type of {@link com.fasterxml.jackson.annotation.JsonTypeInfo.Id#CUSTOM},
     * this setting may or may not have any effect.
     *
     * @return a {@link com.fasterxml.jackson.annotation.JsonTypeInfo.As} object.
     */
    public As include() default As.PROPERTY;

    /**
     * Property names used when type inclusion method ({@link com.fasterxml.jackson.annotation.JsonTypeInfo.As#PROPERTY}) is used
     * (or possibly when using type metadata of type {@link com.fasterxml.jackson.annotation.JsonTypeInfo.Id#CUSTOM}).
     * If POJO itself has a property with same name, value of property
     * will be set with type id metadata: if no such property exists, type id
     * is only used for determining actual type.
     *<p>
     * Default property name used if this property is not explicitly defined
     * (or is set to empty <code>String</code>) is based on
     * type metadata type ({@link #use}) used.
     *
     * @return a {@link java.lang.String} object.
     */
    public String property() default "";

    /**
     * Optional property that can be used to specify default implementation
     * class to use for deserialization if type identifier is either not present,
     * or can not be mapped to a registered type (which can occur for ids,
     * but not when specifying explicit class to use).
     * Property has no effect on choice of type id used for serialization;
     * it is only used in deciding what to do for otherwise unmappable cases.
     *<p>
     * Note that while this property allows specification of the default
     * implementation to use, it does not help with structural issues that
     * may arise if type information is missing. This means that most often
     * this is used with type-name -based resolution, to cover cases
     * where new sub-types are added, but base type is not changed to
     * reference new sub-types.
     *<p>
     * There are certain special values that indicate alternate behavior:
     *<ul>
     * <li>{@link java.lang.Void} means that objects with unmappable (or missing)
     *    type are to be mapped to null references.
     *    For backwards compatibility (2.5 and below), value of
     *    <code>com.fasterxml.jackson.databind.annotation.NoClass</code> is also allowed
     *    for such usage.
     *  </li>
     * <li>Placeholder value of {@link com.fasterxml.jackson.annotation.JsonTypeInfo} (that is, this annotation type
     *    itself} means "there is no default implementation" (in which
     *   case an error results from unmappable type).
     *   For backwards compatibility with earlier versions (2.5 and below),
     *   value of {@link com.fasterxml.jackson.annotation.JsonTypeInfo.None} may also be used.
     *  </li>
     * </ul>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> defaultImpl() default JsonTypeInfo.class;

    /**
     * Property that defines whether type identifier value will be passed
     * as part of JSON stream to deserializer (true), or handled and
     * removed by <code>TypeDeserializer</code> (false).
     * Property has no effect on serialization.
     *<p>
     * Default value is false, meaning that Jackson handles and removes
     * the type identifier from JSON content that is passed to
     * <code>JsonDeserializer</code>.
     *
     * @since 2.0
     * @return a boolean.
     */
    public boolean visible() default false;

    // 19-Dec-2014, tatu: Was hoping to implement for 2.5, but didn't quite make it.
    //   Hope for better luck with 2.8 or later
    /**
     * Property that defines whether type serializer is allowed to omit writing
     * of type id, in case that value written has type same as {@link #defaultImpl()}.
     * If true, omission is allowed (although writer may or may not be able to do that);
     * if false, type id should always be written still.
     *
     * @since 2.5
    public boolean skipWritingDefault() default false;
    /*
    
    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    /**
     * This marker class that is only to be used with <code>defaultImpl</code>
     * annotation property, to indicate that there is no default implementation
     * specified.
     * 
     * @deprecated Since 2.5, use any Annotation type (such as {@link JsonTypeInfo},
     *    if such behavior is needed; this is rarely necessary.
     */
    @Deprecated
    public abstract static class None { }
}
