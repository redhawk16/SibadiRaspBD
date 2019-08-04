/* TODO:
*   1. Создать БД
*   2. Записать данные в БД
*   3. Сделать структуру с зависимостями в БД
*   4. Подключить БД к проекту Android
*   5. Сделать оптимизацию кода
*   6. Определиться с программой, в Android или отдельно на ПК
*   ...
*   Логика:
*   Добавляем в БД данные при этом ищем их в БД на предмет совпадения, если найдено то считываем их ID */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        // Создаем экземпляр по работе с БД
        DbHandler dbHandler = DbHandler.getInstance();
        dbHandler.CreateDB();

        //Document doc = Jsoup.connect("http://u999451g.beget.tech/rasp/rasp.html").get(); // Адрес сайта для парсинга
        Document doc = Jsoup.connect("http://umu.sibadi.org/Rasp/Rasp.aspx?group=10599&sem=2").get(); // Адрес сайта для парсинга

        String[] DayOfWeeks = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"}; // Ключевые слова для поиска на странице сайта
        String[] Time = {"8-20 9-50", "10-00 11-30", "11-40 13-10", "13-45 15-15", "15-25 16-55", "17-05 18-35"};
        String[] Typelessons = {"Лаб", "лек", "пр."};

        List<String[]> Schedules = new ArrayList<String[]>();

        AtomicInteger trPos = new AtomicInteger(0);
        AtomicInteger rowspan = new AtomicInteger();
        AtomicReference<String> dayweek = new AtomicReference<String>(null);
        AtomicReference<String> discipline = new AtomicReference<String>(null);
        AtomicReference<String> disciplineType = new AtomicReference<String>(null);

        Element table = doc.getElementById("tblGr");
        table.getElementsByTag("tbody").first().child(0).remove(); // Удаляем заголовок таблицы (День, Часы, Нед...)
        Elements trs = table.getElementsByTag("tr");

        trs.forEach(tr -> {
            /* Переход по td внутри tr */
            for(int tds = 0; tds < tr.children().size(); tds++){
                if(rowspan.get() <= trPos.get()) {
                    for(String el : DayOfWeeks) {
                        if(el.equals(tr.child(tds).text())){
                            //Elements y = td.parent().children();
                            System.out.println("\t\t" + tr.child(tds).text()); // Выводим день недели
                            dayweek.set(tr.child(tds).text());
                            rowspan.set(Integer.valueOf(tr.child(tds).attr("rowspan"))); // Записываем кол-во пропускаемых строк(оптимизация)
                            tr.child(tds).remove(); // Удаление дня недели
                            break;
                        }
                    }
                }

                if ((tds == 0 || tds == 1) && tr.child(tds).hasAttr("rowspan")) {
                    if(tr.child(0).hasAttr("rowspan")) {
                        int rowspanF = Integer.valueOf(tr.child(0).attr("rowspan"));
                        for(int i = 1; i < rowspanF; i++){
                            if(tr.child(0).hasAttr("rowspan")) tr.child(0).removeAttr("rowspan"); // Удаляем атрибуты у td
                            tr.parent().child(trPos.get() + i).child(0).before(tr.child(0).clone()); // Копируем необходимые строчки на следующий tr->td
                        }
                    }
                    if(tr.child(1).hasAttr("rowspan")) {
                        int rowspanF = Integer.valueOf(tr.child(1).attr("rowspan"));
                        for(int i = 1; i < rowspanF; i++){
                            if(tr.child(1).hasAttr("rowspan")) tr.child(1).removeAttr("rowspan");
                            tr.parent().child(trPos.get() + i).child(1).before(tr.child(1).clone());
                        }
                    }
                }

                if( (!tr.child(1).text().equals("1") || !tr.child(1).text().equals("2") ) && tr.child(1).text().length() != 1){
                    tr.child(1).before("<td>3</td>");
                }

                //System.out.println(tr.child(tds).text()); // Вывод по строчно (td)

                /* TODO: переделать!*/
                String type = null;
                String name = null;

                if(tds == 2) {
                    type = "Disciplines";
                    for(String typelessons : Typelessons) {
                        name = tr.child(tds).text();
                        if(name.contains(typelessons)) {
                            disciplineType.set(typelessons);
                            discipline.set((tr.child(tds).text().substring(typelessons.length())).trim());
                            name = discipline.get();
                            break;
                        }
                    }
                }

                if(tds == 3) { type = "Teachers"; name = tr.child(tds).text(); }
                if(tds == 4) { type = "Auditories"; name = tr.child(tds).text(); }
                if(tds == 2 || tds == 3 || tds == 4) {
                    try {
                        dbHandler.WriteDB(type, name);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

            Schedules.add(new String[] { dayweek.get(), // День недели
                    tr.parent().child(trPos.get()).child(0).text(), // Часы
                    tr.parent().child(trPos.get()).child(1).text(), // Тип недели
                    disciplineType.get(), // Тип дисциплины
                    discipline.get(), // Дисциплина
                    tr.parent().child(trPos.get()).child(3).text(), // Преподаватель
                    tr.parent().child(trPos.get()).child(4).text(), // Аудитория
                    "1", // Подгруппа
                    "1", // Группа
                    "1" // Факультет
            } );

            System.out.println(tr.parent().child(trPos.get()).text()); // Вывод по строчно (tr)
            //System.out.println();
            //if(trPos.get() <= tr.parent().children().size())
                trPos.getAndIncrement();
        });

        System.out.println("\n\tSchedules\t\n");

        /* Try to add*/

        /*  TODO:
         *    Делаем так: заносим в БД таблицы Дисциплины, Преподователей, Аудитории,
         *    затем считываем из этих таблиц всю информацию и заносим их в массив,
         *    после приступаем к Schedules проверяя на совпадение текста и данных в массиве, заменяя на соответсвующие ID из БД */

        List<String[]> disciplines = dbHandler.getTable("Disciplines");
        List<String[]> teachers = dbHandler.getTable("Teachers");
        List<String[]> auditories = dbHandler.getTable("Auditories");

        for(String[] sc : Schedules) {
            String Code_Dayweek = null;
            String Number_Lesson = null;
            String Code_Typelesson = null;
            String Code_Discipline = null;
            String Code_Teacher = null;
            String Code_Auditory = null;

//            System.out.print(sc[0]); // День недели
//            System.out.print(sc[1]); // Часы
//            System.out.print(sc[2]); // Тип Недели
//            System.out.print(sc[3]); // Тип дисциплины
//            System.out.print(sc[4]); // Дисциплина
//            System.out.print(sc[5]); // Преподаватель
//            System.out.print(sc[6]); // Аудитория

            // Pognali epta

            // День недели
            for(int i = 0; i < DayOfWeeks.length; i++){
                if(sc[0].equals(DayOfWeeks[i])) {
                    Code_Dayweek = String.valueOf(i + 1); // Получаем ID дня недели
                    //System.out.println();
                    break;
                }
            }
//            for(String dayofweek : DayOfWeeks) {
//                if(sc[0].equals(dayofweek)) {
//                   // int fdw = dayofweek; // Получаем ID дня недели
//                    System.out.println();
//                    break;
//                }
//            }

            // Время
            for(int i = 0; i < Time.length; i++){
                if(sc[1].equals(Time[i])) {
                    Number_Lesson = String.valueOf(i + 1);
                    //System.out.println();
                    break;
                }
            }
//            for(String time : Time) {
//                if(sc[1].equals(time)) {
//                    Number_Lesson = String.valueOf(time.indexOf(sc[1]) + 1);
//                    break;
//                }
//            }

            //System.out.print(sc[2]); // Тип Недели

            //Типы дисциплин
            for(int i = 0; i < Typelessons.length; i++){
                if(sc[3].equals(Typelessons[i])) {
                    Code_Typelesson = String.valueOf(i + 1);
                    //System.out.println();
                    break;
                }
            }
//            for(String typelessons : Typelessons) {
//                if(sc[3].equals(typelessons)) {
//                    Code_Typelesson = String.valueOf(typelessons.indexOf(sc[3]) + 1);
//                    break;
//                }
//            }

            // Дисциплины
            for (String[] discip : disciplines) {
                if(sc[4].equals(discip[1])) {
                    Code_Discipline = discip[0];
                    break;
                }
            }

            // Преподаватели
            for (String[] teacher : teachers) {
                if(sc[5].equals(teacher[1])) {
                    Code_Teacher = teacher[0];
                    break;
                }
            }

            // Аудитории
            for (String[] auditory : auditories) {
                if(sc[6].equals(auditory[1])) {
                    Code_Auditory = auditory[0];
                    break;
                }
            }

/*            sc[7]; // Подгруппа
            sc[8]; // Группа
            sc[9]; // Факультет*/
            dbHandler.WriteDB("Schedules", Code_Dayweek, Number_Lesson, sc[2], Code_Typelesson, Code_Discipline, Code_Teacher, Code_Auditory, sc[7], sc[8], sc[9]);
        }

        //dbHandler.CreateDB();

        // Получаем все записи и выводим их на консоль
        List<Schedules> products = dbHandler.getAllProducts();
        for (Schedules product : products) {
            System.out.println(product.toString());
        }

        dbHandler.CloseDB();
    }
}