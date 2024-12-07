package org.august.aminoAuthorizator.amino.WSRealization.listeners;

import org.august.AminoApi.dto.response.WSSMessage.MessageInformation;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.Pattern;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.PatternHandler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class MessageManager {
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

        classes.addAll(reflections.getTypesAnnotatedWith(PatternHandler.class));

        for (Class<?> clazz : classes) {
            try {
                instances.put(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.out.println("Ошибка при создании экземпляра класса: " + clazz.getName());
                e.printStackTrace(System.out);
            }
        }
    }

    public void executePattern(String message, MessageInformation messageInfo) {
        for (Class<?> clazz : classes) {
            this.executeCommandInClass(clazz, message, messageInfo);
        }
    }

    private void executeCommandInClass(Class<?> clazz, String message, MessageInformation messageInfo) {
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(Pattern.class)) {
                continue;
            }

            Pattern patternAnnotation = method.getAnnotation(Pattern.class);
            String patternValue = patternAnnotation.value();

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternValue);
            Matcher matcher = pattern.matcher(message);

            if (!matcher.matches()) {
                continue;
            }

            invokeCommandMethod(clazz, method, messageInfo);
        }
    }

    private void invokeCommandMethod(Class<?> clazz, Method method, MessageInformation message) {
        try {
            Object obj = instances.get(clazz);
            method.invoke(obj, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Ошибка при вызове метода команды: " + method.getName());
            e.printStackTrace(System.out);
        }
    }
}
