package Pool;

import org.example.Person;

import java.util.LinkedList;

public class PoolTest
{
    public static void Init()
    {
        var test = new Pool<>(
                () -> new Person("John", 30, true),
                (Person person, Object[] args) ->
                {
                    person.Name = args[0].toString();
                    person.Age = (int)args[1];
                    person.IsMale = (boolean)args[2];
                    person.Address = args[3].toString();
                },
                100
        );

        for (int i = 0; i < 50; i++) test.add("Bob" + i, 30 + i, i % 2 == 0, (100 + i) + " Main Street");
        for (int i = 20; i < 30; i++) test.remove(20);

        var rupert = test.add("Rupert", 42, true, "39 Rupert Street");

        System.out.println(test.indexOf(rupert));

        for (var p : test)
        {
            System.out.println(p);
        }
    }
}
