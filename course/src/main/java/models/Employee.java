package models;
/**
 * Модель работника
 */
public class Employee {
    public int ID;
    /**
     * Имя работника
     */
    public String Name;
    /**
     * Логин работника
     */
    public String Login;
    /**
     * Пароль работника
     */
    public String Password;
    /**
     * Телефон работника
     */
    public String Phone;
    /**
     * Должность работника
     */
    public String Post;
    /**
     * Специальность работника
     */
    public String Speciality;
    /**
     * Является ли работника администратором
     */
    public boolean Admin;
    /**
     * Может ли работник быть помощником
     */
    public boolean CanHelp;
    /**
     * @param name Имя работника
     * @param login Логин работника
     * @param password Пароль работника
     * @param phone Телефон работника
     * @param post Должность работника
     * @param speciality Специальность работника
     * @param admin Является ли работника администратором
     * @param canHelp Может ли работник быть помощником
     */
    public Employee(String name, String login, String password, String phone, String post, String speciality, boolean admin, boolean canHelp)
    {
        Name = name;
        Login = login;
        Password = password;
        Phone = phone;
        Post = post;
        Speciality = speciality;
        Admin = admin;
        CanHelp = canHelp;
    }
}
