import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class DbHandler {

    // Константа, в которой хранится адрес подключения
    private static final String DB_NAME = "jdbc:sqlite:A:/Projects/Games/untitled/src/test.db";

    private static String sqlQuery;
    private Connection connection;
    private static Statement statmt;

    // Используем шаблон одиночка, чтобы не плодить множество
    // экземпляров класса DbHandler
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    public DbHandler() throws SQLException, ClassNotFoundException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(DB_NAME);
    }

    /* Создание Базы Данных */
    public void CreateDB() throws SQLException {
        statmt = this.connection.createStatement();

        System.out.println("> Создание базы данных и ее структуры...");
        // Создание структуры базы данных
        statmt.execute("CREATE TABLE if not exists 'Auditories' ('Code_Auditory' INTEGER PRIMARY KEY, 'Number_Auditory' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Dayweeks' ('Code_Dayweek' INTEGER PRIMARY KEY, 'Name_Dayweek' TEXT NOT NULL UNIQUE, 'Num_Dayweek' INTEGER NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Disciplines' ('Code_Discipline' INTEGER PRIMARY KEY, 'Name_Discipline' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Faculties' ('Code_Faculty' INTEGER PRIMARY KEY, 'Name_Faculty' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Groups' ('Code_Group' INTEGER PRIMARY KEY, 'Name_Group' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Lessons' ('Number_Lesson' INTEGER PRIMARY KEY, 'Time_Start' TEXT NOT NULL UNIQUE, 'Time_End' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Schedules' ('_id' INTEGER PRIMARY KEY, 'Code_Dayweek' INTEGER NOT NULL, 'Number_Lesson' INTEGER NOT NULL, 'Code_Typeweek' INTEGER NOT NULL, 'Code_Typelesson' INTEGER NOT NULL, 'Code_Discipline' INTEGER NOT NULL, 'Code_Teacher' INTEGER NOT NULL, 'Code_Auditory' INTEGER NOT NULL, 'Code_Subgroup' INTEGER NOT NULL, 'Code_Group' INTEGER NOT NULL, 'Code_Faculty' INTEGER NOT NULL); ");
        statmt.execute("CREATE TABLE if not exists 'Subgroups' ('Code_Subgroup' INTEGER PRIMARY KEY, 'Name_Subgroup' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Teachers' ('Code_Teacher' INTEGER PRIMARY KEY, 'Name_Teacher' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Typelessons' ('Code_Typelesson' INTEGER PRIMARY KEY, 'Name_Typelesson' TEXT NOT NULL UNIQUE); ");
        statmt.execute("CREATE TABLE if not exists 'Typeweeks' ('Code_Typeweek' INTEGER PRIMARY KEY, 'Name_Typeweek' TEXT NOT NULL UNIQUE); ");
        System.out.println("> База данных успешно создана!");

        /* TODO:
        *   Сделать проверку на существующие значения */
        // Заполнение созданной базы данных нативными данными
        System.out.println("> Создание таблиц и заполнение их нативными данными...");
        /* Таблица Dayweeks*/
        statmt.execute("INSERT OR IGNORE INTO 'Dayweeks' ('Name_Dayweek', 'Num_Dayweek') VALUES"
                + " ('Понедельник', 0), "
                + " ('Вторник', 1), "
                + " ('Среда', 2), "
                + " ('Четверг', 3), "
                + " ('Пятница', 4), "
                + " ('Суббота', 5); ");
        System.out.println("> Таблица Dayweeks заполнена!");

        /* Таблица Lessons*/
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (1, '08:20', '09:50'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (2, '10:00', '11:30'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (3, '11:40', '13:10'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (4, '13:45', '15:15'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (5, '15:25', '16:55'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Lessons' ('Number_Lesson', 'Time_Start', 'Time_End') VALUES (6, '17:05', '18:35'); ");
        System.out.println("> Таблица Lessons заполнена!");

        /* Таблица Faculties*/
        statmt.execute("INSERT OR IGNORE INTO 'Faculties' ('Code_Faculty', 'Name_Faculty') VALUES (1, 'ИСУ'); ");
        System.out.println("> Таблица Faculties заполнена!");

        /* Таблица Groups*/
        statmt.execute("INSERT OR IGNORE INTO 'Groups' ('Code_Group', 'Name_Group') VALUES (1, 'ПИб-16И1'); ");
        System.out.println("> Таблица Groups заполнена!");

        /* Таблица Subgroups*/
        statmt.execute("INSERT OR IGNORE INTO 'Subgroups' ('Code_Subgroup', 'Name_Subgroup') VALUES (1, 'первая подгруппа'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Subgroups' ('Code_Subgroup', 'Name_Subgroup') VALUES (2, 'вторая подгруппа'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Subgroups' ('Code_Subgroup', 'Name_Subgroup') VALUES (3, 'первая подгруппа'); ");
        System.out.println("> Таблица Subgroups заполнена!");

        /* Таблица Typelessons*/
        statmt.execute("INSERT OR IGNORE INTO 'Typelessons' ('Code_Typelesson', 'Name_Typelesson') VALUES (1, 'лабораторная'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Typelessons' ('Code_Typelesson', 'Name_Typelesson') VALUES (2, 'лекция'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Typelessons' ('Code_Typelesson', 'Name_Typelesson') VALUES (3, 'практика'); ");
        System.out.println("> Таблица Typelessons заполнена!");

        /* Таблица Typeweeks*/
        statmt.execute("INSERT OR IGNORE INTO 'Typeweeks' ('Code_Typeweek', 'Name_Typeweek') VALUES (1, 'нечетная неделя'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Typeweeks' ('Code_Typeweek', 'Name_Typeweek') VALUES (2, 'четная неделя'); ");
        statmt.execute("INSERT OR IGNORE INTO 'Typeweeks' ('Code_Typeweek', 'Name_Typeweek') VALUES (3, 'обе недели'); ");
        System.out.println("> Таблица Typeweeks заполнена!");

        System.out.println("База данных создана или уже существует!");
    }


    // , String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9, String p10, String p11
    public void WriteDB(String name, String ... args) throws SQLException {
        // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
        switch (name) {
            case("Auditories"):
                sqlQuery = "INSERT OR IGNORE INTO 'Auditories' (Number_Auditory) " +
                        "VALUES (?); ";
                break;
            case("Disciplines"):
                sqlQuery = "INSERT OR IGNORE INTO 'Disciplines' (Name_Discipline) " +
                        "VALUES (?); ";
                break;
            case("Teachers"):
                sqlQuery = "INSERT OR IGNORE INTO 'Teachers' (Name_Teacher) " +
                        "VALUES (?); ";
                break;
            case("Schedules"):
                sqlQuery = "INSERT OR IGNORE INTO 'Schedules' ('Code_Dayweek', 'Number_Lesson', 'Code_Typeweek', 'Code_Typelesson', 'Code_Discipline', 'Code_Teacher', 'Code_Auditory', 'Code_Subgroup', 'Code_Group', 'Code_Faculty') " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
                break;
            default:
                sqlQuery = null;
                System.out.println("Error add!");
                return;
        }
        try (PreparedStatement statement = this.connection.prepareStatement(sqlQuery)) {
            for(int i = 0; i < args.length; i++) {
                String ff = args[i];
                //System.out.println();
                statement.setString(i+1, args[i]);
            }
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*public List<Schedules> getAllProducts() {

        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать наши продукты, полученные из БД
            List<Schedules> schedules = new ArrayList<Schedules>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()

            sqlQuery = "SELECT _id, Name_Dayweek, Time_Start, Time_End, Name_Typeweek, Name_Typelesson, Name_Discipline, Name_Teacher, Number_Auditory "
                    + "FROM Schedules "
                    + "INNER JOIN Dayweeks ON Schedules.Code_Dayweek=Dayweeks.Code_Dayweek "
                    + "INNER JOIN Lessons ON Schedules.Number_Lesson=Lessons.Number_Lesson "
                    + "INNER JOIN Typeweeks ON Schedules.Code_Typeweek=Typeweeks.Code_Typeweek "
                    + "INNER JOIN Typelessons ON Schedules.Code_Typelesson=Typelessons.Code_Typelesson "
                    + "INNER JOIN Disciplines ON Schedules.Code_Discipline=Disciplines.Code_Discipline "
                    + "INNER JOIN Teachers ON Schedules.Code_Teacher=Teachers.Code_Teacher "
                    + "INNER JOIN Auditories ON Schedules.Code_Auditory=Auditories.Code_Auditory";

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            // Проходимся по нашему resultSet и заносим данные в products
            while (resultSet.next()) {
                String f = resultSet.getString("Name_Dayweek");
                f = resultSet.getString("Time_Start");
                f =resultSet.getString("Time_End");
                f =  resultSet.getString("Name_Typeweek");
                f = resultSet.getString("Name_Typelesson");
                f =   resultSet.getString("Name_Discipline");
                f =    resultSet.getString("Name_Teacher");
                f =    resultSet.getString("Number_Auditory");
                System.out.println();
                schedules.add(new Schedules(resultSet.getInt("_id"),
                        resultSet.getString("Name_Dayweek"),
                        resultSet.getString("Time_Start"),
                        resultSet.getString("Time_End"),
                        resultSet.getString("Name_Typeweek"),
                        resultSet.getString("Name_Typelesson"),
                        resultSet.getString("Name_Discipline"),
                        resultSet.getString("Name_Teacher"),
                        resultSet.getString("Number_Auditory")));
            }
            return schedules;
        } catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }
    }*/

    public List<String[]> getTable(String tName) throws SQLException {
        statmt = this.connection.createStatement();

        sqlQuery = "SELECT * FROM '" + tName + "'";
        ResultSet resultSet = statmt.executeQuery(sqlQuery);

        List<String[]> disciplines = new ArrayList<String[]>();

        while (resultSet.next()) {
            //String f = resultSet.getString(resultSet.get);
            disciplines.add(new String[] { resultSet.getString(1), resultSet.getString(2) } );
            //System.out.println();
        }

        return disciplines;
    }

    public void CloseDB() throws SQLException {
        connection.close();
        statmt.close();

        System.out.println("Соединения закрыты");
    }

/*    // Удаление продукта по id
    public void deleteProduct(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Products WHERE id = ?")) {
            statement.setObject(1, id);
            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}