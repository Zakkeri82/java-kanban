package controllers;

import enums.Status;
import enums.TypeTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskManager {

    /**
     * Метод получает строку String из Task для записи ее в файл
     *
     * @param task задача которую нужно сереализовать
     * @return строка для записи в файл
     */
    public static String getStringFromTask(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = null;
        String taskDuration = null;
        if (task.getStartTime() != null) {
            formattedDateTime = task.getStartTime().format(formatter);
        }
        if (task.getDuration() != null) {
            taskDuration = String.valueOf(task.getDuration().toMinutes());
        }
        return task.getId() + "," + task.getType() + "," + task.getNameTask()
                + "," + task.getStatus() + "," + task.getDescription() + "," + task.getEpicId()
                + "," + formattedDateTime + "," + taskDuration + "\n";
    }

    /**
     * Получение задачи из строки, десериализация состояния из файла
     *
     * @param str входящая строка полученная из файла
     * @return возвращает задачу
     */
    public static Task getTaskFromString(String str) {
        String[] elementsTask = str.split(",");
        final int id = Integer.parseInt(elementsTask[0]);
        final TypeTask type = TypeTask.valueOf(elementsTask[1]);
        final String name = elementsTask[2];
        final Status status = Status.valueOf(elementsTask[3]);
        final String description = elementsTask[4];
        int epicId = 0;
        if (type.equals(TypeTask.SubTask)) {
            epicId = Integer.parseInt(elementsTask[5]);
        }
        final LocalDateTime startTime = LocalDateTime.parse(elementsTask[6]);
        final Duration duration = Duration.parse(elementsTask[7]);

        if (type.equals(TypeTask.Epic)) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else if (type.equals(TypeTask.SubTask)) {
            Subtask subtask = new Subtask(name, description, startTime, duration);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setEpicId(epicId);
            return subtask;
        }
        Task task = new Task(name, description, startTime, duration);
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    /**
     * Преобразует историю изменения в строку
     *
     * @param manager менеджер истории
     * @return строка id в истории
     */
    public static String historyToString(HistoryManager manager) {
        return "\n" + manager.getHistory().stream()
                .map(Task::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    /**
     * Получает список id из строки файла для десереализации менеджера истории
     *
     * @param value строка из файла
     * @return список id
     */
    public static List<Integer> historyFromString(String value) {
        String[] idHistory = value.split(",");
        return Arrays.stream(idHistory)
                .map(id -> Integer.parseInt(id.trim()))
                .collect(Collectors.toList());
    }
}