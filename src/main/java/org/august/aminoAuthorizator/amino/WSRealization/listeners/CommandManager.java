package org.august.aminoAuthorizator.amino.WSRealization.listeners;

import org.august.AminoApi.dto.response.WSSMessage.MessageInformation;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.Command;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.CommandHandler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager {
    private static final Set<Class<?>> classes = new HashSet<>();
    private static final Map<Class<?>, Object> instances = new HashMap<>();

    static {
        initCommandClasses();
    }

    private static void initCommandClasses() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("org.august"))
                .setScanners(Scanners.TypesAnnotated)
                .filterInputsBy(new FilterBuilder().includePackage("org.august")));

        classes.addAll(reflections.getTypesAnnotatedWith(CommandHandler.class));

        for (Class<?> clazz : classes) {
            try {
                instances.put(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.out.println("Ошибка при создании экземпляра класса: " + clazz.getName());
                e.printStackTrace(System.out);
            }
        }
    }

    public void executeCommand(String command, MessageInformation message) {
        String[] parts = command.split(" ");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        for (Class<?> clazz : classes) {
            executeCommandInClass(clazz, commandName, message, args);
        }
    }

    private void executeCommandInClass(Class<?> clazz, String commandName, MessageInformation message, String[] args) {
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }

            Command commandAnnotation = method.getAnnotation(Command.class);
            String commandValue = commandAnnotation.value().isEmpty() ? method.getName() : commandAnnotation.value();

            if (!commandName.equals(commandValue)) {
                continue;
            }

            invokeCommandMethod(clazz, method, message, args);
        }
    }


    private void invokeCommandMethod(Class<?> clazz, Method method, MessageInformation message, String[] args) {
        try {
            Object obj = instances.get(clazz);
            method.invoke(obj, message, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Ошибка при вызове метода команды: " + method.getName());
            e.printStackTrace(System.out);
        }
    }
}


