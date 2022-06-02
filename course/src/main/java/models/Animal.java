package models;

import java.util.Date;
/**
 * Модель питомца
 */
public class Animal {
    public int ID;
    /**
     * Идентификатор хозяина питомца
     */
    public int ClientID;
    /**
     * Имя питомца
     */
    public String Name;
    /**
     * Пол питомца
     */
    public String Gender;
    /**
     * Вид питомца (кот, собака и т.д.)
     */
    public String Type;
    /**
     * Порода питомца
     */
    public String Breed;
    /**
     * Дата рождения питомца
     */
    public Date BirthDate;
    /**
     * Относительный путь к фотографии питомца
     */
    public String Photo;
    /**
     * @param clientId Идентификатор хозяина питомца
     * @param name Имя питомца
     * @param gender Пол питомца
     * @param type Вид питомца (кот, собака и т.д.)
     * @param breed Порода питомца
     * @param birthDate Дата рождения питомца
     * @param photo Относительный путь к фотографии питомца
     */
    public Animal(int clientId, String name, String gender, String type, String breed, Date birthDate, String photo){
        ClientID = clientId;
        Name = name;
        Gender = gender;
        Type = type;
        Breed = breed;
        BirthDate = birthDate;
        Photo = photo;
    }
}
