package com.secret.client.csv;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

public abstract class CsvDataLoader<T> implements Iterator<T> {

    private static final char CSV_SEPARATOR = ';';
    protected final CSVReader reader;
    protected final DataMapper<T> mapper;
    private String[] next;

    public CsvDataLoader(String inputFile) throws IOException {
        this.reader = new CSVReader(new FileReader(inputFile), CSV_SEPARATOR);
        this.mapper = configureMapping();
    }

    protected abstract <T> DataMapper<T> configureMapping() throws IOException;

    @Override
    public boolean hasNext() {
        try {
            next = reader.readNext();
        } catch (IOException e) {
            next = null;
        }
        return next != null;
    }

    @Override
    public T next() {
        try {
            return mapper.processLine(next);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported");
    }

    protected static class DataMapper<T> {
        private Map<Class<?>, PropertyEditor> editorMap = null;
        private final ColumnPositionMappingStrategy<T> mapper;

        public DataMapper(ColumnPositionMappingStrategy<T> mapper) {
            this.mapper = mapper;
        }

        public T processLine(String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
            T bean = mapper.createBean();
            for (int col = 0; col < line.length; col++) {
                PropertyDescriptor prop = mapper.findDescriptor(col);
                if (null != prop) {
                    String value = checkForTrim(line[col], prop);
                    Object obj = convertValue(value, prop);
                    prop.getWriteMethod().invoke(bean, obj);
                }
            }
            return bean;
        }

        private String checkForTrim(String s, PropertyDescriptor prop) {
            return trimmableProperty(prop) ? s.trim() : s;
        }

        private boolean trimmableProperty(PropertyDescriptor prop) {
            return !prop.getPropertyType().getName().contains("String");
        }

        protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
            PropertyEditor editor = getPropertyEditor(prop);
            Object obj = value;
            if (null != editor) {
                editor.setAsText(value);
                obj = editor.getValue();
            }
            return obj;
        }

        protected PropertyEditor getPropertyEditor(PropertyDescriptor desc) throws InstantiationException, IllegalAccessException {
            Class<?> cls = desc.getPropertyEditorClass();
            if (null != cls)
                return (PropertyEditor) cls.newInstance();
            return getPropertyEditorValue(desc.getPropertyType());
        }

        private PropertyEditor getPropertyEditorValue(Class<?> cls) {
            if (editorMap == null) {
                editorMap = new HashMap<Class<?>, PropertyEditor>();
            }

            PropertyEditor editor = editorMap.get(cls);

            if (editor == null) {
                editor = PropertyEditorManager.findEditor(cls);
                addEditorToMap(cls, editor);
            }

            return editor;
        }

        private void addEditorToMap(Class<?> cls, PropertyEditor editor) {
            if (editor != null) {
                editorMap.put(cls, editor);
            }
        }
    }
}
