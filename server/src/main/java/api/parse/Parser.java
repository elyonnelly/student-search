package api.parse;

import api.StudentSearchApp;
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private StudentSearchApp app;

    public Parser(StudentSearchApp app) {
        this.app = app;
    }

    public List<Query> csvParse(File file, List<String> fields, boolean hasHeaders) throws IOException {
        List<Query> queries = new ArrayList<>();
        try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.RFC4180.withDelimiter(';'))) {
            for (CSVRecord record : parser) {
                if (hasHeaders) {
                    hasHeaders = false;
                    continue;
                }
                Query q = new Query();
                if (fields.size() != record.size()) {
                    throw new IOException("Ошибка при разборе файла. Проверьте указанные поля и состояние файла!");
                }
                for (int k = 0; k < fields.size(); k++) {
                    handleNameCeil(record.get(k), fields.get(k), q);
                }
                queries.add(q);
            }
        }
        catch (Exception ex) {
            throw new IOException("Ошибка при разборе файла. Проверьте указанные поля и состояние файла!");
        }
        return queries;
    }

    public List<Query> pdfParse(List<String> lines, List<String> fields) throws IOException {
        List<Query> queries = new ArrayList<>();
        try {
            int i = 0;
            while (i < lines.size()) {
                Query q = new Query();
                for (var field : fields) {
                    handleNameCeil(lines.get(i), field, q);
                    i++;
                }
                queries.add(q);
            }
        } catch (Exception ex) {
            throw new IOException("Ошибка при разборе файла. Проверьте указанные поля и состояние файла!");
        }
        return queries;
    }

    private void updQueryGrade(Query query, Integer grade) {
        query.setAge_from(6 + grade);
        query.setAge_to(8 + grade);
    }

    private boolean findCity(Query q, String value) {
        if (app.getCitiesId().containsKey(value.toLowerCase())) {
            q.setCityId(app.getCitiesId().get(value.toLowerCase()));
            q.setCity(value);
            return true;
        }
        return false;
    }

    private void handleNameCeil(String value, String field, Query query) {
        if (field.equals("Фамилия Имя (Отчество)")) {
            var splitted = value.split(" ");
            query.setQ(splitted[0] + " " + splitted[1]);
        }
        if (field.equals("Фамилия И. (О.)")) {
            var splitted = value.split(" ");
            query.setQ(splitted[0]);
        }
        if (field.equals("Фамилия") || field.equals("Имя")) {
            query.setQ(query.getQ() + " " + value);
        }
        if (field.equals("Класс")) {
            Integer grade = Integer.valueOf(value);
            updQueryGrade(query, grade);
            query.setGrade(grade);
        }
        if (field.equals("Город")) {
            var punct = ", ( ) \"".split(" ");
            for (var p : punct) {
                value = value.replace(p.charAt(0), ' ');
            }

            value = value.replace('ё', 'е');

            var t = value.split("Село ");
            if (t.length > 1) {
                value = t[1];
            }

            t = value.split("село ");
            if (t.length > 1) {
                value = t[1];
            }

            //try 1. всё value уже является городом
            boolean success = findCity(query, value);
            if (success) {
                return;
            }

            //try 2. уберем г. и оставим только одно или два слова после
            t = value.split("г\\.");
            if (t.length > 1) {
                value = t[0] + t[1];
            }
            var splitValue = value.split(" ");

            for (var mbCity : splitValue) {
                success = findCity(query, mbCity);
                if (success) {
                    return;
                }
            }

            //try 3. пробуем по 2 слова
            for (int i = 0; i < splitValue.length - 1; i++) {
                success = findCity(query, splitValue[i] + splitValue[i + 1]);
                if (success) {
                    break;
                }
            }
        }
        if (field.equals("Статус Участника")) {
            value.replace(" ", "");
            //пробуем найти "победитель", "призёр". если ничего не найдено, то это "участник".
            if (value.toLowerCase().contains("победитель")) {
                query.setStatusParticipant(StatusParticipant.WINNER);
            }
            if (value.toLowerCase().contains("призёр") || value.toLowerCase().contains("призер")) {
                query.setStatusParticipant(StatusParticipant.PRIZEWINNER);
            }
            query.setStatusParticipant(StatusParticipant.PARTICIPANT);
        }
        if (field.equals("Школа")) {
            query.setSchool(value);
        }
    }
}
