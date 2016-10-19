This module is used to fill up with data fields for any java object.
You can map rules for data generation by using annotations in 
data_for_entity.annotations. Basically, all data is generated randomly,
in case you need to specify your own data generation rule, implement
EntityDataProvider interface and pass it to @DataProvider annotation to 
the appropriate field. You can also use existing FieldDataType with 
annotation @WithFieldDataType to control value that will be generated.
Supported annotations:
 1. DataIgnore. Use this option to ignore this field while generating 
 values;
 3. WithDataDependencies. In case field's value depends on other fields'
 values, use this option and specify fields' this field depends on.
 By default, values of dependant fields are joined. Specify you custom
 DependencyDataProvider to provide additional behavior.
 4.WithDataOptions. Specify here EntityDataProvider if needed, data 
 length and FieldDataType. Data types are listed in 
 data_for_entity.data_types
4. WithDataSize. Use this annotation to control exact length of the data.
5. WithCollectionSize. Use this annotation to control number of elements
in collection (can be used for Collection, Map and Arrays)
6. DataProvider. Use this annotation in case you need to generate 
specific data that is not in templates.

Consider the following example:

```
public class Person {
   
   private Address adrress;
   @WithDataDependencies(provider=SequenceProvider.class, fields = {"firstName", "lastName"})
   private String name;
   private String firstName;
   private String lastName;
   
   }

public class Address {
   
   private String firstAddress;
   private String lastAddress;
   @WithFieldDataType(FieldDataType.phone)
   private String phoneNumber;
   }
  
```
To create populated with values instance do the following:
```
RandomEntities randomEntities = new RandomEntities();
Person person = randomEntities.randomEntity(Person.class);

```

This will produce you a required object with all fields set, like 

```
{"address":{"firstAddress":"fthrkTest","lastAddress":"ggey9Ite", 
"phoneNumber":"059684029485"}, "name": "John Doe", "firstName": "John",
"lastName":"Doe"}
```

Another useful features:
1. You can control min and max length of data of collection using static
class FieldDataSizeOptions.
2. By default all instances are created using default constructor. If
you need to specify extended behavior, create your own instance of 
InstanceManager and passed it to your RandomEntities by 
randomEntities.setInstanceManager(InstanceManager manager)

#Not Supported:
 2. Nested lists, i.e. list inside list inside list etc.

#Recent features:
1. Added multithreading support.
2. Added map interface support.

#TODO
 1. Support custom FieldDataTypes
 