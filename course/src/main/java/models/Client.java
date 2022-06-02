package models;

import java.util.Date;
/**
 * Модель клиента
 */
public class Client {
    public int ID;
    /**
     * Имя клиента
     */
    public String FullName;
    /**
     * Телефон клиента
     */
    public String Phone;
    /**
     * Дата рождения клиента
     */
    public Date BirthDate;
    /**
     * @param fullName Имя клиента
     * @param phone Телефон клиента
     * @param birthDate Дата рождения клиента
     */
    public  Client(String fullName, String phone, Date birthDate){
        FullName = fullName;
        Phone = phone;
        BirthDate = birthDate;
    }
}
