import data_for_entity.RandomEntities;

public class Test {


    public static void main(String[] args) {
        RandomEntities entities = new RandomEntities();
        A a = entities.randomEntity(A.class);
        System.out.println(a.toString());
    }
}
