package org.demo;

import java.lang.reflect.Field;

public class FieldSetter {
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            System.err.println("❌ Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }
}
