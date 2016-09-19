This module is used to fill up with data fields for any java object.
You can map rules for data generation by using annotations in 
data_for_entity.annotations. Basically, all data is generated randomly,
in case you need to specify your own data generation rule, implement
EntityDataProvider interface and pass it to @WithOptions annotation to 
the appropriate field.
Supported annotations:
 1. DataIgnore. Use this option to ignore this field while generating 
 values;
 2. DataStatic. In case your field always should have static value, use
 this option;
 3. WithDataDependencies. In case field's value depends on other fields'
 values, use this option and specify fields' this field depends on.
 By default, values of dependant fields are joined. Specify you custom
 DependencyDataProvider to provide additional behavior.
 4.WithDataOptions. Specify here EntityDataProvider if needed, data 
 length and FieldDataType. Data types are listed in 
 data_for_entity.data_types


To use it, create an instance of ObjectInitializer and use appropriate
method.

#Not Supported:
 1. Java map interface.
 2. Nested lists, i.e. list inside list inside list etc.


#TODO
 1. Add multiprocessing support
 2. Support java map interface
 