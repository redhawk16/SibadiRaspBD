import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

class MySQL {

    // Используем шаблон одиночка, чтобы не плодить множество
    // экземпляров класса DbHandler
    private static MySQL instance = null;

    private static final String DB_NAME = "rasp";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    private static String sqlQuery = null;

    static synchronized MySQL getInstance() throws SQLException {
        if (instance == null)
            instance = new MySQL();
        return instance;
    }

    private MySQL() throws SQLException {
            Properties properties=new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASS);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");
            properties.setProperty("serverTimezone", "UTC");

            con = DriverManager.getConnection(URL, properties);
    }

    void createDefaultTable() {

        try{
            //connect();
            if(stmt == null) {
            stmt = con.createStatement(); }
            DatabaseMetaData dbMeta = con.getMetaData();

            List<String[]> Tables = new ArrayList<String[]>();

            // {Название таблицы, SQL запрос}
            Tables.add(new String[] { "Schedules", "CREATE TABLE if not exists Schedules (id INTEGER PRIMARY KEY AUTO_INCREMENT, Code_Dayweek INTEGER NOT NULL, Number_Lesson INTEGER NOT NULL, Code_Typeweek INTEGER NOT NULL, Code_Typelesson INTEGER NOT NULL, Code_Discipline INTEGER NOT NULL, Code_Teacher INTEGER NOT NULL, Code_Auditory INTEGER NOT NULL, Code_Subgroup INTEGER NOT NULL, Code_Group INTEGER NOT NULL, Code_Faculty INTEGER NOT NULL);" });
            Tables.add(new String[] { "Disciplines", "CREATE TABLE if not exists Disciplines (Code_Discipline INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Discipline VARCHAR(255) NOT NULL UNIQUE); " } );
            Tables.add(new String[] { "Auditories", "CREATE TABLE if not exists Auditories (Code_Auditory INTEGER PRIMARY KEY AUTO_INCREMENT, Number_Auditory VARCHAR(64) NOT NULL UNIQUE); "} );
            Tables.add(new String[] { "Teachers", "CREATE TABLE if not exists Teachers (Code_Teacher INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Teacher VARCHAR(128) NOT NULL UNIQUE); " } );
            Tables.add(new String[] { "Dayweeks",
                    "CREATE TABLE if not exists Dayweeks (Code_Dayweek INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Dayweek VARCHAR(14) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Dayweeks (Name_Dayweek) VALUES"
                            + " ('Понедельник'), "
                            + " ('Вторник'), "
                            + " ('Среда'), "
                            + " ('Четверг'), "
                            + " ('Пятница'), "
                            + " ('Суббота');"
            } );
            Tables.add(new String[] { "Faculties",
                    "CREATE TABLE if not exists Faculties (Code_Faculty INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Faculty VARCHAR(16) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Faculties (Code_Faculty, Name_Faculty) VALUES (1, 'ИСУ');"
            } );
            Tables.add(new String[] { "Groups",
                    "CREATE TABLE if not exists Groups (Code_Group INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Group VARCHAR(16) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Groups (Code_Group, Name_Group) VALUES (1, 'ПИб-16И1');"
            } );
            Tables.add(new String[] { "Lessons",
                    "CREATE TABLE if not exists Lessons (Number_Lesson INTEGER PRIMARY KEY AUTO_INCREMENT, Time_Start VARCHAR(6) NOT NULL UNIQUE, Time_End VARCHAR(16) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Lessons (Time_Start, Time_End) VALUES " +
                            "('08:20', '09:50'), " +
                            "('10:00', '11:30'), " +
                            "('11:40', '13:10'), " +
                            "('13:45', '15:15'), " +
                            "('15:25', '16:55'), " +
                            "('17:05', '18:35');"
            } );
            Tables.add(new String[] { "Subgroups",
                    "CREATE TABLE if not exists Subgroups (Code_Subgroup INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Subgroup VARCHAR(18) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Subgroups (Code_Subgroup, Name_Subgroup) VALUES " +
                            "(1, 'первая подгруппа'), " +
                            "(2, 'вторая подгруппа');"
            } );
            Tables.add(new String[] { "Typelessons",
                    "CREATE TABLE if not exists Typelessons (Code_Typelesson INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Typelesson VARCHAR(14) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Typelessons (Code_Typelesson, Name_Typelesson) VALUES " +
                            "(1, 'лабораторная')," +
                            "(2, 'лекция')," +
                            "(3, 'практика');"
            } );
            Tables.add(new String[] { "Typeweeks",
                    "CREATE TABLE if not exists Typeweeks (Code_Typeweek INTEGER PRIMARY KEY AUTO_INCREMENT, Name_Typeweek VARCHAR(16) NOT NULL UNIQUE); ",
                    "INSERT IGNORE INTO Typeweeks (Code_Typeweek, Name_Typeweek) VALUES " +
                            "(1, 'нечетная неделя')," +
                            "(2, 'четная неделя')," +
                            "(3, 'обе недели');"
            } );

            System.out.println("> Создание таблиц и их структуры...");

            for(int i = 0; i < Tables.size(); i++) {
                rs = dbMeta.getTables(null, null, Tables.get(i)[0], null);

                if(!rs.next()) { // Проверяем существует ли данная таблица
                    stmt.executeUpdate(Tables.get(i)[1]);
                    if(Tables.get(i).length == 3) {
                        stmt.executeUpdate(Tables.get(i)[2]);
                    }
                    System.out.println("> Таблица '"+ Tables.get(i)[0] +"' создана");
                } else {
                    rs.close();
                    System.out.println("Таблицы созданы ранее. Вы хотите удалить их? \nYes(Y)");
                    Scanner scanner = new Scanner(System.in);
                    String ans = scanner.next();
                    switch (ans) {
                        case "y": case "yes": delTables(); createDefaultTable();
                        default: return;
                    }
                }
            }

            rs.close();
            System.out.println("> Таблицы созданы или уже были созданы ранее!");
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }

    void WriteDB(String name, String... args) throws SQLException {
        // Создадим подготовленное выражение, чтобы избежать SQL-инъекций
        switch (name) {
            case("Auditories"):
                sqlQuery = "INSERT IGNORE INTO Auditories (Number_Auditory) " +
                        "VALUES (?); ";
                break;
            case("Disciplines"):
                sqlQuery = "INSERT IGNORE INTO Disciplines (Name_Discipline) " +
                        "VALUES (?); ";
                break;
            case("Teachers"):
                sqlQuery = "INSERT IGNORE INTO Teachers (Name_Teacher) " +
                        "VALUES (?); ";
                break;
            case("Schedules"):
                sqlQuery = "INSERT IGNORE INTO Schedules (Code_Dayweek, Number_Lesson, Code_Typeweek, Code_Typelesson, Code_Discipline, Code_Teacher, Code_Auditory, Code_Subgroup, Code_Group, Code_Faculty) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
                break;
            default:
                sqlQuery = null;
                System.out.println("Error add!");
                return;
        }
        try (PreparedStatement statement = con.prepareStatement(sqlQuery)) {
            for(int i = 0; i < args.length; i++) {
                //String ff = args[i];
                //System.out.println();
                statement.setString(i+1, args[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    List<String[]> getTable(String tName) throws SQLException {
        if(stmt == null) {
            stmt = con.createStatement(); }

        sqlQuery = "SELECT * FROM " + tName;
        rs = stmt.executeQuery(sqlQuery);

        List<String[]> table = new ArrayList<String[]>();

        while (rs.next()) {
            //String f = resultSet.getString(resultSet.get);
            table.add(new String[] { rs.getString(1), rs.getString(2) } );
            //System.out.println();
        }

        return table;
    }


    void print() throws SQLException {
        if(stmt == null) {
            stmt = con.createStatement(); }

        sqlQuery = "SELECT id, Name_Dayweek, Time_Start, Time_End, Name_Typeweek, Name_Typelesson, Name_Discipline, Name_Teacher, Number_Auditory "
                + "FROM Schedules "
                + "INNER JOIN Dayweeks ON Schedules.Code_Dayweek=Dayweeks.Code_Dayweek "
                + "INNER JOIN Lessons ON Schedules.Number_Lesson=Lessons.Number_Lesson "
                + "INNER JOIN Typeweeks ON Schedules.Code_Typeweek=Typeweeks.Code_Typeweek "
                + "INNER JOIN Typelessons ON Schedules.Code_Typelesson=Typelessons.Code_Typelesson "
                + "INNER JOIN Disciplines ON Schedules.Code_Discipline=Disciplines.Code_Discipline "
                + "INNER JOIN Teachers ON Schedules.Code_Teacher=Teachers.Code_Teacher "
                + "INNER JOIN Auditories ON Schedules.Code_Auditory=Auditories.Code_Auditory "
                + "GROUP BY id; ";

        rs = stmt.executeQuery(sqlQuery);
        while (rs.next()){
            System.out.println(rs.getString("Name_Dayweek") + " "
                    + rs.getString("Time_Start") + ":" + rs.getString("Time_End") + " "
                    + rs.getString("Name_Typeweek") + " "
                    + rs.getString("Name_Typelesson") + " "
                    + rs.getString("Name_Discipline") + " "
                    + rs.getString("Name_Teacher") + " "
                    + rs.getString("Number_Auditory"));
        }
        rs.close();
    }


/*TODO: Test this code*/
    private void delTables() {
        try {
            if(stmt == null) {
                stmt = con.createStatement(); }
            stmt.executeUpdate("DROP DATABASE " + DB_NAME);
            stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
            stmt.executeQuery("USE " + DB_NAME);

            System.out.println("> Таблицы удалены!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void closeDB() {
        try {
            con.close();
            stmt.close();
            rs.close();
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }

}
