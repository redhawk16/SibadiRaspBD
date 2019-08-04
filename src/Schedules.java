public class Schedules {

    public int id;
    public String dayweek;
    public String time_start; // Время начала занятий
    public String time_end; // Время конца занятий
    public String typeweek; // Тип недели
    public String discipline; // Название дисциплины
    public String typelesson; // Тип занятия
    public String teacher; // ФИО преподавателя
    public String auditory; // Номер аудитории
/*    private String subgroups; // подгруппа
    private String groups; // группа
    private String faculty; // факультет*/

    public Schedules(int id, String dayweek, String time_start, String time_end, String typeweek, String typelesson, String discipline, String teacher, String auditory) {
        this.id = id;
        this.dayweek = dayweek;
        this.time_start = time_start;
        this.time_end = time_end;
        this.typeweek = typeweek;
        this.typelesson = typelesson;
        this.discipline = discipline;
        this.teacher = teacher;
        this.auditory = auditory;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s %s %s",
                this.dayweek, this.time_start, this.time_end, this.typeweek, this.typelesson, this.discipline, this.teacher, this.auditory);
    }
}