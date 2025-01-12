package org.example;

import java.util.Collection;

public class Person
{
    public String Name;
    public int Age;
    public boolean IsMale;
    public String Address;
    public int[] PhoneNumbers;
    public Collection<Person> Contacts;

    public Person(String name, int age, boolean isMale)
    {
        Name = name;
        Age = age;
        IsMale = isMale;
    }

    @Override
    public String toString()
    {
        return Name + ", " + Age + ", " + IsMale + ": " + Address;
    }
}
