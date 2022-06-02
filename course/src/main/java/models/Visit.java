package models;

import java.util.Date;
/**
 * Модель приёма
 */
public class Visit {
    public int ID;
    /**
     * Идентификатор питомца
     */
    public int IDAnimal;
    /**
     * Идентификатор работника
     */
    public int IDEmployee;
    /**
     * Идентификатор помощника
     */
    public int IDHelperEmployee;
    /**
     * Идентификатор клиента
     */
    public int IDClient;
    /**
     * Дата приёма
     */
    public Date Date;
    /**
     * Диагноз
     */
    public String Diagnosis;
    /**
     * Назначение
     */
    public String Assignment;
    /**
     * Общая стоимость
     */
    public int TotalCost;
    public Visit(int iDAnimal, int iDEmployee, int iDHelperEmployee, int iDClient, Date date, String diagnosis, String assignment, int cost)
    {
        IDAnimal = iDAnimal;
        IDEmployee = iDEmployee;
        IDHelperEmployee = iDHelperEmployee;
        IDClient = iDClient;
        Date = date;
        Diagnosis = diagnosis;
        Assignment = assignment;
        TotalCost = cost;
    }
}
