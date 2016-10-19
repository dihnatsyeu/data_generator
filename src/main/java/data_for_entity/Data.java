package data_for_entity;

/**
 * Any class that is supposed to produce data for a field
 * should be extended from this class.
 */
abstract class Data {
    abstract Object generateData();
}
