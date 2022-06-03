package main;

import javafx.scene.control.Alert;
import models.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Класс взаимодействия с базой данных
 * Перед использованием необходимо вызвать метод start (один раз на программу)
 * Настройки подключения можно задать в классе с помощью приватных полей
 */
public class DB {
    private static Connection connection = null;
    private static final String connectionUrl = "jdbc:mysql://localhost:3306/VetClinic";
    private static final String user = "root";
    private static final String password = "root_root";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    /**
     * Инициализирует соединение с базой данных
     */
    public static void start() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(connectionUrl, user, password);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    /**
     * Добавляет переданного питомца в базу данных
     * @param animal Питомец, которого нужно добавить
     */
    public static void addAnimal(Animal animal) {
        String sqlExpression = "Insert Into Animals (Name, Gender, Type, Breed, BirthDate, Photo, IDClient) values(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, animal.Name);
            statement.setString(2, animal.Gender);
            statement.setString(3, animal.Type);
            statement.setString(4, animal.Breed);
            statement.setString(5, String.valueOf(animal.BirthDate));
            statement.setString(6, animal.Photo);
            statement.setInt(7, animal.ClientID);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Добавляет переданного клиента в базу данных
     * @param client Клиент, которого нужно добавить
     */
    public static void addClient(Client client) {
        String sqlExpression = "Insert Into Clients (FullName, BirthDate, Phone) values(?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, client.FullName);
            statement.setString(2, String.valueOf(client.BirthDate));
            statement.setString(3, client.Phone);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Добавляет переданную процедуру в базу данных
     * @param procedure Питомец, которого нужно добавить
     */
    public static void addProcedure(Procedure procedure) {
        String sqlExpression = "Insert Into ProceduresList (Name, Cost) values(?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, procedure.Name);
            statement.setInt(2, procedure.Cost);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Check on existing login
    /**
     * Добавляет переданного работника в базу данных
     * Логин сотрудника должен быть уникальным
     * @param employee Работник, которого нужно добавить
     */
    public static void addEmployee(Employee employee) {
        String sqlExpression = "Insert Into Employees (Name, Login, Password, Phone, Post, Speciality, IsAdmin, CanHelp) values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, employee.Name);
            statement.setString(2, employee.Login);
            statement.setString(3, employee.Password);
            statement.setString(4, employee.Phone);
            statement.setString(5, employee.Post);
            statement.setString(6, employee.Speciality);
            statement.setBoolean(7, employee.Admin);
            statement.setBoolean(8, employee.CanHelp);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Добавляет переданный приём в базу данных
     * и записывает переданные процедуры как процедуры данного приёма
     * @param visit Питомец, которого нужно добавить
     * @param procedures Питомец, которого нужно добавить
     */
    public static void addVisit(Visit visit, List<Procedure> procedures) throws SQLException {
        String sqlExpression = "Insert Into Visits (IDAnimal, IDEmployee, IDClient, Date, Diagnosis, Assignment, IDHelperEmployee, TotalCost) values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        connection.setAutoCommit(false);
        int ID = 0;
        try {
            statement = connection.prepareStatement(sqlExpression, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, visit.IDAnimal);
            statement.setInt(2, visit.IDEmployee);
            statement.setInt(3, visit.IDClient);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String strDate = dateFormat.format(visit.Date);
            statement.setString(4, strDate);
            statement.setString(5, visit.Diagnosis);
            statement.setString(6, visit.Assignment);
            if (visit.IDHelperEmployee == 0){
                statement.setNull(7, Types.INTEGER);
            }
            else {
                statement.setInt(7, visit.IDHelperEmployee);
            }
            statement.setInt(8, visit.TotalCost);
            statement.execute();
            ResultSet res = statement.getGeneratedKeys();
            if (res.next()) {
                ID = res.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (procedures.size() == 0) {
            return;
        }

        sqlExpression = "Insert Into PerformedProcedures (IDVisit, IDProcedure) values(?, (Select ID from ProceduresList where Name = ?))";
        statement = null;
        try {
            for (Procedure procedure : procedures) {
                statement = connection.prepareStatement(sqlExpression);
                statement.setInt(1, ID);
                statement.setString(2, procedure.Name);
                statement.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }
    /**
     * Изменяет переданную процедуру
     * @param procedure Процедура, которую нужно изменить
     */
    public static void changeProcedure(Procedure procedure) {
        String sqlExpression = "UPDATE ProceduresList SET Name = ?, Cost = ? WHERE ID = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, procedure.Name);
            statement.setInt(2, procedure.Cost);
            statement.setInt(3, procedure.ID);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Изменяет переданного питомца
     * @param animal Питомец, которого нужно изменить
     */
    public static void changeAnimal(Animal animal) {
        String sqlExpression = "UPDATE Animals SET Name=?, Gender=?, Type= ?, Breed=?, BirthDate= ?, Photo=?, IDClient=? WHERE ID = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, animal.Name);
            statement.setString(2, animal.Gender);
            statement.setString(3, animal.Type);
            statement.setString(4, animal.Breed);
            statement.setString(5, String.valueOf(animal.BirthDate));
            statement.setString(6, animal.Photo);
            statement.setInt(7, animal.ClientID);
            statement.setInt(8, animal.ID);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Изменяет переданного работника
     * @param employee Работник, которого нужно добавить
     */
    public static void changeEmployee(Employee employee) {
        String sqlExpression = "UPDATE Employees SET Name = ?, Login=?, Password=?, Phone=?, Post=?, Speciality=?, IsAdmin=?, CanHelp=? WHERE ID = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, employee.Name);
            statement.setString(2, employee.Login);
            statement.setString(3, employee.Password);
            statement.setString(4, employee.Phone);
            statement.setString(5, employee.Post);
            statement.setString(6, employee.Speciality);
            statement.setBoolean(7, employee.Admin);
            statement.setBoolean(8, employee.CanHelp);
            statement.setInt(9, employee.ID);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Изменяет переданного клиента
     * @param client Клиент, которого нужно добавить
     */
    public static void changeClient(Client client) {
        String sqlExpression = "UPDATE Clients SET FullName = ?, BirthDate = ?, Phone = ? WHERE ID = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setString(1, client.FullName);
            statement.setString(2, String.valueOf(client.BirthDate));
            statement.setString(3, client.Phone);
            statement.setInt(4, client.ID);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Изменяет переданный приём
     * @param visit Приём, который нужно изменить
     * @param procedures Процедуры изменяемого приёма
     */
    public static void changeVisit(Visit visit, List<Procedure> procedures) throws SQLException {
        String sqlExpression = "Update Visits SET IDAnimal=?, IDEmployee=?, IDClient=?, Date=?, Diagnosis=?, Assignment=?, IDHelperEmployee=?, TotalCost=? Where ID = ?";
        PreparedStatement statement = null;
        connection.setAutoCommit(false);
        try {
            statement = connection.prepareStatement(sqlExpression);
            statement.setInt(1, visit.IDAnimal);
            statement.setInt(2, visit.IDEmployee);
            statement.setInt(3, visit.IDClient);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String strDate = dateFormat.format(visit.Date);
            statement.setString(4, strDate);
            statement.setString(5, visit.Diagnosis);
            statement.setString(6, visit.Assignment);
            if (visit.IDHelperEmployee == 0){
                statement.setNull(7, Types.INTEGER);
            }
            else {
                statement.setInt(7, visit.IDHelperEmployee);
            }
            statement.setInt(8, visit.TotalCost);
            statement.setInt(9, visit.ID);
            statement.execute();

            sqlExpression = "Delete FROM PerformedProcedures WHERE IDVisit = ?";
            statement = connection.prepareStatement(sqlExpression);
            statement.setInt(1, visit.ID);

            if (procedures.size() > 0) {
                sqlExpression = "Insert Into PerformedProcedures (IDVisit, IDProcedure) values(?, (Select ID from ProceduresList where Name = ?))";
                try {
                    for (Procedure procedure : procedures) {
                        statement = connection.prepareStatement(sqlExpression);
                        statement.setInt(1, visit.ID);
                        statement.setString(2, procedure.Name);
                        statement.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.commit();
        connection.setAutoCommit(true);
    }
    /**
     * Получает питомца по переданному идентификатору
     * @param ID Идентификатор питомца, которого нужно найти
     * @return Найденный питомец. Может быть null
     */
    public static Animal getAnimal(int ID) throws SQLException {
        String sqlExpression = "SELECT * FROM Animals Where ID = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, ID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Animal animal = new Animal(
                    result.getInt("IDClient"),
                    result.getString("Name"),
                    result.getString("Gender"),
                    result.getString("Type"),
                    result.getString("Breed"),
                    result.getDate("BirthDate"),
                    result.getString("Photo")
            );
            animal.ID = result.getInt("ID");
            return animal;
        }
        return null;
    }
    /**
     * Получает клиента по переданному идентификатору
     * @param ID Идентификатор клиента, которого нужно найти
     * @return Найденный клиент. Может быть null
     */
    public static Client getClient(int ID) throws SQLException {
        String sqlExpression = "SELECT * from Clients Where ID = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, ID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Client client = new Client(
                    result.getString("FullName"),
                    result.getString("Phone"),
                    result.getDate("BirthDate")
            );
            client.ID = result.getInt("ID");
            return client;
        }
        return null;
    }

    // TODO: search procedures also
    /**
     * Получает приём по переданному идентификатору. Не заполняет процедуры
     * @param ID Идентификатор приёма, который нужно найти
     * @return Найденный приём. Может быть null
     */
    public static Visit getVisit(int ID) throws SQLException {
        String sqlExpression = "Select * From Visits Where ID = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, ID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Visit visit = new Visit(
                    result.getInt("IDAnimal"),
                    result.getInt("IDEmployee"),
                    result.getInt("IDHelperEmployee"),
                    result.getInt("IDClient"),
                    result.getDate("Date"),
                    result.getString("Diagnosis"),
                    result.getString("Assignment"),
                    result.getInt("TotalCost")
            );
            visit.ID = result.getInt("ID");
            return visit;
        }
        return null;
    }
    /**
     * Получает работника по переданному идентификатору
     * @param ID Идентификатор работника, которого нужно найти
     * @return Найденный работник. Может быть null
     */
    public static Employee getEmployee(int ID) throws SQLException {
        String sqlExpression = "SELECT * From Employees Where ID = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, ID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Employee employee = new Employee(
                    result.getString("Name"),
                    result.getString("Login"),
                    result.getString("Password"),
                    result.getString("Phone"),
                    result.getString("Post"),
                    result.getString("Speciality"),
                    result.getBoolean("IsAdmin"),
                    result.getBoolean("CanHelp")
            );
            employee.ID = result.getInt("ID");
            return employee;
        }
        return null;
    }
    /**
     * Получает работника по переданному идентификатору
     * @param login Логин работника, которого нужно найти
     * @return Найденный работник. Может быть null
     */
    public static Employee getEmployee(String login) throws SQLException {
        String sqlExpression = "SELECT * From Employees Where Login = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setString(1, login);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Employee employee = new Employee(
                    result.getString("Name"),
                    result.getString("Login"),
                    result.getString("Password"),
                    result.getString("Phone"),
                    result.getString("Post"),
                    result.getString("Speciality"),
                    result.getBoolean("IsAdmin"),
                    result.getBoolean("CanHelp")
            );
            employee.ID = result.getInt("ID");
            return employee;
        }
        return null;

    }
    /**
     * Получает процедуру по переданному идентификатору
     * @param ID Идентификатор процедуры, которую нужно найти
     * @return Найденная процедура. Может быть null
     */
    public static Procedure getProcedure(int ID) throws SQLException {
        String sqlExpression = "SELECT * From ProceduresList Where ID = ?";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, ID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Procedure procedure = new Procedure(
                    result.getString("Name"),
                    result.getInt("Cost")
            );
            procedure.ID = result.getInt("ID");
            return procedure;
        }
        return null;
    }
    /**
     * Получает список клиентов
     * @return Найденные клиенты. Может быть пустым
     */
    public static List<Client> getClients() throws SQLException, ParseException {
        String sqlExpression = "SELECT * from Clients";
        List<Client> clients = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlExpression);
        while (result.next()) {
            Client client = new Client(
                    result.getString("FullName"),
                    result.getString("Phone"),
                    new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("BirthDate")));
            client.ID = result.getInt("ID");
            clients.add(client);
        }
        return clients;
    }
    /**
     * Получает список клиентов
     * @return Найденные клиенты. Может быть пустым
     */
    public static List<Employee> getEmployees() throws SQLException {
        String sqlExpression = "SELECT * from Employees";
        List<Employee> employees = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlExpression);
        while (result.next()) {
            Employee employee = new Employee(
                    result.getString("Name"),
                    result.getString("Login"),
                    result.getString("Password"),
                    result.getString("Phone"),
                    result.getString("Post"),
                    result.getString("Speciality"),
                    result.getBoolean("IsAdmin"),
                    result.getBoolean("CanHelp")
            );
            employee.ID = result.getInt("ID");
            employees.add(employee);
        }
        return employees;
    }
    /**
     * Получает список работников
     * @return Найденные работники. Может быть пустым
     */
    public static List<Animal> getAnimals() throws SQLException {
        String sqlExpression = "SELECT * from Animals";
        List<Animal> animals = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlExpression);
        while (result.next()) {
            Animal animal = new Animal(
                    result.getInt("IDClient"),
                    result.getString("Name"),
                    result.getString("Gender"),
                    result.getString("Type"),
                    result.getString("Breed"),
                    result.getDate("BirthDate", new GregorianCalendar()),
                    result.getString("Photo")
            );
            animal.ID = result.getInt("ID");
            animals.add(animal);
        }
        return animals;
    }
    /**
     * Получает список процедур
     * @return Найденные процедуры. Может быть пустым
     */
    public static List<Procedure> getProcedures() throws SQLException {
        String sqlExpression = "SELECT * from ProceduresList";
        List<Procedure> procedures = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sqlExpression);
        while (result.next()) {
            Procedure procedure = new Procedure(
                    result.getString("Name"),
                    result.getInt("Cost")
            );
            procedure.ID = result.getInt("ID");
            procedures.add(procedure);
        }
        return procedures;
    }
    /**
     * Получает список процедур
     * @param visitId Идентификатор приёма, к которому должны принадлежать процедуры
     * @return Найденные питомец. Может быть пустым
     */
    public static List<Procedure> getProcedures(int visitId) throws SQLException {
        String sqlExpression = "SELECT * from ProceduresList Where ID IN (Select PerformedProcedures.IDProcedure From PerformedProcedures Where PerformedProcedures.IDVisit = ?)";
        ;
        List<Procedure> procedures = new ArrayList<>();
        PreparedStatement statement = null;
        statement = connection.prepareStatement(sqlExpression);
        statement.setInt(1, visitId);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            Procedure procedure = new Procedure(
                    result.getString("Name"),
                    result.getInt("Cost")
            );
            procedure.ID = result.getInt("ID");
            procedures.add(procedure);
        }
        return procedures;
    }
    /**
     * Получает таблицу из базы данных
     * @param name название таблицы в базе данных
     * @return Таблица из базы данных. Может быть null
     */
    public static ResultSet getTable(String name) {
        switch (name) {
            case "Animals" -> {
                return getAnimalsRep();
            }
            case "Employees" -> {
                return getEmployeesRep();
            }
            case "Visits" -> {
                return getVisitsRep();
            }
            case "Clients" -> {
                return getClientsRep();
            }
            case "ProceduresList" -> {
                return getProceduresRep();
            }
        }
        return null;
    }
    private static ResultSet getAnimalsRep() {
        String sqlExpression = "SELECT Animals.ID ,Animals.Name as Имя, Animals.Gender as Пол,Animals.Type as Тип," +
                "Animals.Breed as Порода, Animals.BirthDate as 'Дата рождения', Clients.FullName as Хозяин FROM Animals JOIN Clients on Animals.IDClient = Clients.ID";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static ResultSet getEmployeesRep() {
        String sqlExpression = "SELECT ID ,Name as Имя, Login as Логин, " +
                "Phone as Телефон, Post as Должность, Speciality as Специализация, IsAdmin as Администратор, " +
                "CanHelp as Помощник FROM Employees";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private static ResultSet getVisitsRep() {
        String sqlExpression = "SELECT Visits.ID, Animals.Name as Питомец, Emp.Name as Врач, Visits.Date as Дата, Clients.FullName as Клиент, " +
                "Visits.Diagnosis as Диагноз, Visits.Assignment as Назначение, Helper.Name as Помощник, Visits.TotalCost as Стоимость " +
                "FROM Visits " +
                "JOIN Animals on Visits.IDAnimal = Animals.ID " +
                "JOIN Employees as Emp on Visits.IDEmployee = Emp.ID " +
                "JOIN Clients on Visits.IDClient = Clients.ID " +
                "LEFT JOIN Employees as Helper on Visits.IDHelperEmployee = Helper.ID";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static ResultSet getProceduresRep() {
        String sqlExpression = "SELECT ID, Name as Название, Cost as Стоимость FROM ProceduresList";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static ResultSet getClientsRep() {
        String sqlExpression = "Select ID ,FullName as Имя, BirthDate as 'День рождения', Phone as Телефон FROM Clients";
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Получает приёмы работника с фильтром по заданным параметрам
     * @param idEmployee идентификатор работника, отчёт по которому нужно получить
     * @param main должен ли работник быть главным в приёмах
     * @param helper должен ли работник быть помощников в приёмах
     * @param minDate начальная дата выборки
     * @param maxDate конечная дата выборки
     * @return набор записей из базы данных. Может быть null
     */
    public static ResultSet getEmployeeRep(int idEmployee, boolean main, boolean helper, java.util.Date minDate, java.util.Date maxDate) {
        String sqlExpression = "SELECT Visits.ID, Animals.Name as Питомец, Emp.Name as Врач, Visits.Date as Дата," +
                " Clients.FullName as Клиент, Visits.Diagnosis as Диагноз, Visits.Assignment as Назначение, Helper.Name as Помощник, Visits.TotalCost as Стоимость " +
                "FROM Visits " +
                "JOIN Animals on Visits.IDAnimal = Animals.ID " +
                "JOIN Employees as Emp on Visits.IDEmployee = Emp.ID " +
                "JOIN Clients on Visits.IDClient = Clients.ID " +
                "LEFT JOIN Employees as Helper on Visits.IDHelperEmployee = Helper.ID " +
                "Where Visits.Date > '" + minDate.toString() + "' " +
                "AND Visits.Date < '" + maxDate.toString() + "' ";
        if (main && helper) {
            sqlExpression += " AND Visits.IDEmployee = '" + idEmployee + "' OR Visits.IDHelperEmployee = '" + idEmployee + "'";
        } else if (main) {
            sqlExpression += " AND Visits.IDEmployee = '" + idEmployee + "'";
        } else if (helper) {
            sqlExpression += " AND Visits.IDHelperEmployee = '" + idEmployee + "'";
        }
        try {
            PreparedStatement statement = connection.prepareStatement(sqlExpression);
            return statement.executeQuery(sqlExpression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
