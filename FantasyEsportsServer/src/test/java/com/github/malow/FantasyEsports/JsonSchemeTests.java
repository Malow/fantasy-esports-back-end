package com.github.malow.FantasyEsports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.github.malow.FantasyEsports.apidoc.JsonSchema;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaNode;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaObject;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaParent;
import com.github.malow.FantasyEsports.apidoc.JsonSchema.JsonSchemaProperty;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;

public class JsonSchemeTests
{
  public static class TestJsonClass
  {
    public String name;
    public int age;
    public String jobName;
    public List<Integer> scores = new ArrayList<>();
    public List<List<Integer>> doubleScores = new ArrayList<>();
    public TestJsonClass wife;
    public List<TestJsonClass> children = new ArrayList<>();
  }

  public static TestJsonClass createTestObject()
  {
    TestJsonClass grandChild1 = new TestJsonClass();
    grandChild1.name = "Savantha";
    grandChild1.jobName = "Kindergarten kid";
    grandChild1.age = 2;

    TestJsonClass child1 = new TestJsonClass();
    child1.name = "Sven";
    child1.jobName = "Miner";
    child1.age = 27;
    child1.children.add(grandChild1);

    TestJsonClass grandChild2 = new TestJsonClass();
    grandChild2.name = "Harold";
    grandChild2.wife = grandChild1;
    grandChild2.age = 3;

    TestJsonClass grandChild3 = new TestJsonClass();
    grandChild3.name = "Phillip";
    grandChild3.scores.add(99);
    grandChild3.age = 1;

    TestJsonClass child2 = new TestJsonClass();
    child2.name = "Hannah";
    child1.scores.add(3);
    child2.age = 25;
    child2.children.add(grandChild2);
    child2.children.add(grandChild3);

    TestJsonClass wife = new TestJsonClass();
    wife.name = "Linda";
    wife.jobName = "Rocket Scientist";
    wife.children.add(child1);
    wife.children.add(child2);
    wife.age = 65;

    TestJsonClass testObject = new TestJsonClass();
    testObject.name = "Carl";
    testObject.age = 72;
    testObject.wife = wife;
    testObject.children.add(child1);
    testObject.children.add(child2);
    testObject.scores.add(7);
    testObject.scores.add(19);
    List<Integer> s1 = new ArrayList<>();
    s1.add(37);
    s1.add(59);
    testObject.doubleScores.add(s1);
    List<Integer> s2 = new ArrayList<>();
    s2.add(67);
    testObject.doubleScores.add(s2);
    return testObject;
  }

  @Ignore
  @Test
  public void test()
  {
    String json = GsonSingleton.toJson(createTestObject());
    JsonSchema schema = JsonSchema.fromString(json);
    printRecursively(schema.root, "");
  }

  @Test
  public void testYaml()
  {
    String json = GsonSingleton.toJson(createTestObject());
    JsonSchema schema = JsonSchema.fromString(json);
    printYaml(schema.root);
  }


  public static void printYaml(JsonSchemaNode node)
  {
    print("type: " + node.type, "");
    printRecursivelyYaml(node, "");
  }

  public static void printRecursivelyYaml(JsonSchemaNode node, String indention)
  {
    if (node instanceof JsonSchemaObject)
    {
      JsonSchemaObject nodeObject = (JsonSchemaObject) node;
      List<JsonSchemaProperty> requriedProps = nodeObject.properties.stream().filter(p -> p.required).collect(Collectors.toList());
      if (!requriedProps.isEmpty())
      {
        print("required:", indention);
        for (JsonSchemaProperty prop : requriedProps)
        {
          print("- " + prop.name, indention + "  ");
        }
      }
      print("properties:", indention);
      for (JsonSchemaProperty prop : nodeObject.properties)
      {
        print(prop.name + ":", indention + "  ");
        print("type: " + prop.type, indention + "    ");
        if (prop.child != null)
        {
          printRecursivelyYaml(prop.child, indention + "    ");
        }
      }
    }
    else if (node instanceof JsonSchemaParent)
    {
      JsonSchemaParent nodeParent = (JsonSchemaParent) node;
      print("items: ", indention);
      print("type: " + nodeParent.child.type, indention + "  ");
      if (nodeParent.child instanceof JsonSchemaObject || nodeParent.child instanceof JsonSchemaParent)
      {
        printRecursivelyYaml(nodeParent.child, indention + "  ");
      }
    }
    else
    {
      MaloWLogger.error("Got wrong class: " + node.getClass());
    }
  }

  public static void printRecursively(JsonSchemaNode node, String indention)
  {
    if (node instanceof JsonSchemaProperty)
    {
      JsonSchemaProperty propertyNode = (JsonSchemaProperty) node;
      print(propertyNode.name + ":" + propertyNode.type + (propertyNode.required ? "*" : ""), indention);
      if (propertyNode.child != null)
      {
        printRecursively(propertyNode.child, indention + "  ");
      }
    }
    else if (node instanceof JsonSchemaObject)
    {
      for (JsonSchemaProperty prop : ((JsonSchemaObject) node).properties)
      {
        printRecursively(prop, indention);
      }
    }
    else if (node instanceof JsonSchemaParent)
    {
      JsonSchemaParent parentNode = (JsonSchemaParent) node;
      if (parentNode.child != null)
      {
        if (node.type.equals("array"))
        {
          print(parentNode.child.type, indention);
        }
        printRecursively(parentNode.child, indention + "  ");
      }
    }
  }

  public static void print(String s, String indention)
  {
    System.out.println(indention + s);
  }
}
