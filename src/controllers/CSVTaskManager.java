package controllers;

import enums.Status;
import enums.TypeTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskManager {

    /**
     * Метод получает строку String из Task для записи ее в файл
     *
     * @param task задача которую нужно сереализовать
     * @return строка для ызаписи в файл
     */
    public static String getStringFromTask(Task task) {
        if (task instanceof Epic) {
            return task.getId() + ", " + TypeTask.Epic + ", " + task.getNameTask()
                    + ", " + task.getStatus() + ", " + task.getDescription() + ", " + ((Epic) task).getSubsId() + "\n";
        } else if (task instanceof Subtask) {
            return task.getId() + ", " + TypeTask.SubTask + ", " + task.getNameTask()
                    + ", " + task.getStatus() + ", " + task.getDescription() + ", " + ((Subtask) task).getEpicId() + "\n";
        }
        return task.getId() + ", " + TypeTask.Task + ", " + task.getNameTask()
                + ", " + task.getStatus() + ", " + task.getDescription() + "\n";
    }

    /**
     * Получение задачи из строки, десериализация состояния из файла
     *
     * @param str входящая строка полученная из файла
     * @return возвращает задачу
     */
    public static Task getTaskFromString(String str) {
        String[] elementsTask = str.split(",", 6);
        if (elementsTask[1].trim().equals("Epic")) {
            Epic epic = new Epic(elementsTask[2].trim(), elementsTask[4].trim());
            epic.setId(Integer.parseInt(elementsTask[0].trim()));
            epic.setStatus(getStatusFromString(elementsTask[3].trim()));
            if (!elementsTask[5].trim().equals("[]")) {
                String[] subId = elementsTask[5].replaceAll("\\[|\\]", "").split(",");
                epic.setSubsId(new ArrayList<>(Arrays.stream(subId).map(String::trim).map(Integer::parseInt).collect(Collectors.toList())));
            }
            return epic;
        } else if (elementsTask[1].trim().equals("SubTask")) {
            Subtask subtask = new Subtask(elementsTask[2].trim(), elementsTask[4].trim());
            subtask.setId(Integer.parseInt(elementsTask[0].trim()));
            subtask.setStatus(getStatusFromString(elementsTask[3].trim()));
            subtask.setEpicId(Integer.parseInt(elementsTask[5].trim()));
            return subtask;
        }
        Task task = new Task(elementsTask[2].trim(), elementsTask[4].trim());
        task.setId(Integer.parseInt(elementsTask[0].trim()));
        task.setStatus(getStatusFromString(elementsTask[3].trim()));
        return task;
    }

    private static Status getStatusFromString(String status) {
        if (status.equals("NEW")) {
            return Status.NEW;
        } else if (status.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        }
        return Status.DONE;
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