package models;
/**
 * Модель процедуры
 */
public class Procedure {
    public int ID;
    /**
     * Название процедуры
     */
    public String Name;
    /**
     * Стоимость процедуры
     */
    public int Cost;
    /**
     * @param name Название процедуры
     * @param cost Стоимость процедуры
     */
    public Procedure(String name, int cost)
    {
        Name = name;
        Cost = cost;
    }
}
