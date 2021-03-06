package com.weirddev.testme.intellij.template.context

import com.weirddev.testme.intellij.configuration.TestMeConfig
import com.weirddev.testme.intellij.template.FileTemplateConfig
import com.weirddev.testme.intellij.template.context.impl.JavaTestBuilderImpl
import com.weirddev.testme.intellij.utils.ClassNameUtils
import spock.lang.Specification

/**
 * Date: 2/16/2017
 * @author Yaron Yamin
 */
class JavaTestBuilderImplTest extends Specification {
    static Type fearType = new Type("com.example.foes.Fear", "Fear", "com.example.foes", false, false, false, false, false, [])
    static Type stringType = new Type("java.lang.String", "String", "java.lang", false, false, false, false, false, [])
    static Type queueWithTypeParams = new Type("java.util.Queue<java.util.List<com.example.foes.Fear>>", "Queue<List<Fear>>", "java.util", false, false, false, false, false, [new Type("java.util.List<com.example.foes.Fear>", "List<Fear>", "java.util", false, false, false, false, false, [fearType])])
    static Map globalReplacementMap = ["java.util.Queue"       : "new java.util.LinkedList<TYPES>(java.util.Arrays.asList(<VAL>))",
                                       "java.util.Set"         : "new java.util.HashSet<TYPES>(java.util.Arrays.asList(<VAL>))",
                                       "java.util.Map"         : "new java.util.HashMap<TYPES>(){{put(<VAL>,<VAL>);}}",
                                       "java.util.NavigableMap": "new java.util.TreeMap<TYPES>(new java.util.HashMap<TYPES>(){{put(<VAL>,<VAL>);}})",
                                       "java.util.List"        : "java.util.Arrays.<TYPES>asList(<VAL>)"
    ]
    def testBuilder = new JavaTestBuilderImpl(null, TestBuilder.ParamRole.Input, new FileTemplateConfig(new TestMeConfig()), null, null)

    def "stripGenerics"() {

        expect:
        ClassNameUtils.stripGenerics(canonicalName) == result

        where:
        canonicalName               | result
        "java.util.Set"             | "java.util.Set"
        "java.util.Set<Fire>"       | "java.util.Set"
        "java.util.Set<List<Fire>>" | "java.util.Set"
    }

    def "extractGenerics"() {

        expect:
        ClassNameUtils.extractGenerics(canonicalName) == result

        where:
        canonicalName               | result
        "java.util.Set"             | ""
        "java.util.Set<Fire>"       | "<Fire>"
        "java.util.Set<List<Fire>>" | "<List<Fire>>"
    }

    def "resolveType"() {
        expect:
        result == testBuilder.resolveTypeName(new Type(canonicalName, "Set", "java.util", false, false, false, false, false, []), replacementMap as HashMap)

        where:
        result                          | canonicalName               | replacementMap
        "java.util.HashSet<Fire>"       | "java.util.Set<Fire>"       | ["java.util.Set": "java.util.HashSet<TYPES>"]
        "java.util.HashSet<List<Fire>>" | "java.util.Set<List<Fire>>" | ["java.util.Set": "java.util.HashSet<TYPES>"]
        "java.util.Set<List<Fire>>"     | "java.util.Set<List<Fire>>" | ["java.util.SetZ": "java.util.HashSet<TYPES>"]
        "java.util.Set<List<Fire>>"     | "java.util.Set<List<Fire>>" | []
        "java.util.HashSet"             | "java.util.Set<List<Fire>>" | ["java.util.Set": "java.util.HashSet"]
        "java.util.Arrays.asList"       | "java.util.Set<Fire>"       | ["java.util.Set": "java.util.Arrays.asList"]
        "HashSet<Fire>"                 | "Set<Fire>"                 | ["Set": "HashSet<TYPES>"]
    }

    def "renderJavaCallParam - generic collection"() {
        expect:
        testBuilder.renderJavaCallParam(type, "paramName", globalReplacementMap, [:]) == result

        where:
        result                                                                                                                                                    | type
        "new java.util.LinkedList<java.util.List<com.example.foes.Fear>>(java.util.Arrays.asList(java.util.Arrays.<com.example.foes.Fear>asList(null)))"          | queueWithTypeParams
        "new java.util.HashSet(java.util.Arrays.asList(\"String\"))"                                                                                              | new Type("java.util.Set", "Set", "java.util", false, false, false, false, false, [])
        "new java.util.HashMap<java.lang.String,com.example.foes.Fear>(){{put(\"String\",null);}}"                                                                | new Type("java.util.Map<java.lang.String,com.example.foes.Fear>", "Map", "java.util", false, false, false, false, false, [stringType, fearType])
        "new java.util.HashMap(){{put(\"String\",\"String\");}}"                                                                                                  | new Type("java.util.Map", "Map", "java.util", false, false, false, false, false, [])
        "new java.util.TreeMap<java.lang.String,com.example.foes.Fear>(new java.util.HashMap<java.lang.String,com.example.foes.Fear>(){{put(\"String\",null);}})" | new Type("java.util.NavigableMap<java.lang.String,com.example.foes.Fear>", "NavigableMap", "java.util", false, false, false, false, false, [stringType, fearType])
    }
}
