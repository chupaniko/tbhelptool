package com.chupaniko.dataworker;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class BackupReader implements AutoCloseable {
    private BufferedReader reader;
    private final EntityType entitiesType;
    private boolean hasNext;
    private char[] buffer = new char[1024];
    private StringBuilder restBufferSb = new StringBuilder();
    private boolean entityInfoKeyFound = false;
    private StringBuilder entityInfoKey = new StringBuilder();
    private int squareBracketsCounter = 0;
    private int curlyBracketsCounter = 0;
    private int currentEntityInfoKeyIndex = 0;

    public BackupReader(String backupPath, EntityType entitiesType) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(
                Files.newInputStream(Paths.get(backupPath)), StandardCharsets.UTF_8
        ));
        this.entitiesType = entitiesType;
        this.hasNext = true;
    }

    public boolean hasNextEntity() {
        return hasNext;
    }

    public JSONObject readEntity() throws IOException {
        StringBuilder entityInfoSb = new StringBuilder();

        while (reader.read(buffer) != -1 || hasNext) {
            char[] combinedBuffer = buffer;
            if (restBufferSb.length() != 0) {
                combinedBuffer = new char[restBufferSb.length() + buffer.length];
                int j = 0;
                for (int i = 0; i < restBufferSb.length(); i++, j++) {
                    combinedBuffer[j] = restBufferSb.charAt(i);
                }
                restBufferSb.setLength(0);
                for (int i = 0; i < buffer.length; i++, j++) {
                    combinedBuffer[j] = buffer[i];
                }
            }

            int entityStartIndex = -1;
            int entityEndIndex = -1;
            for (int i = 0; i < combinedBuffer.length; i++) {
                if (!entityInfoKeyFound) {
                    if (combinedBuffer[i] == entitiesType.getEntitiesKey().toCharArray()[currentEntityInfoKeyIndex]) {
                        currentEntityInfoKeyIndex++;
                        entityInfoKey.append(combinedBuffer[i]);
                    } else {
                        currentEntityInfoKeyIndex = 0;
                        entityInfoKey.setLength(0);
                    }
                    if (entitiesType.getEntitiesKey().contentEquals(entityInfoKey)) {
                        entityInfoKeyFound = true;
                    }
                } else {
                    if (combinedBuffer[i] == '[') {
                        squareBracketsCounter++;
                    } else if (combinedBuffer[i] == '{') {
                        curlyBracketsCounter++;
                        if (curlyBracketsCounter == 1) {
                            //начинаем считывать entity
                            entityStartIndex = i;
                        }
                    } else if (combinedBuffer[i] == ']') {
                        squareBracketsCounter--;
                        if (squareBracketsCounter == 0) {
                            //TODO: потенциальное место для бага: если буфер начинается с ']', то метод не вернет entity
                            hasNext = false;
                            break;
                        }
                    } else if (combinedBuffer[i] == '}') {
                        curlyBracketsCounter--;
                        if (curlyBracketsCounter == 0) {
                            entityEndIndex = i;
                            if (i < combinedBuffer.length - 1) {
                                if (combinedBuffer[i + 1] == ']') {
                                    hasNext = false;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            //entity целиком в буфере.
            if (entityStartIndex >= 0 && entityEndIndex >= 0) {
                entityInfoSb.append(Arrays.copyOfRange(
                        combinedBuffer,
                        entityStartIndex,
                        entityEndIndex + 1)
                );
                if (entityEndIndex < combinedBuffer.length - 1) {
                    restBufferSb.append(Arrays.copyOfRange(
                            combinedBuffer,
                            entityEndIndex + 1,
                            combinedBuffer.length
                    ));
                }
                break;
                // последний кусок entity в начале буфера
            } else if (entityStartIndex < 0 && entityEndIndex >= 0) {
                entityInfoSb.append(Arrays.copyOfRange(
                        combinedBuffer,
                        0,
                        entityEndIndex + 1)
                );
                restBufferSb.append(Arrays.copyOfRange(
                        combinedBuffer,
                        entityEndIndex + 1,
                        combinedBuffer.length
                ));
                break;
                // кусок entity в конце буфера
            } else if (entityStartIndex >= 0) {
                entityInfoSb.append(Arrays.copyOfRange(
                        combinedBuffer,
                        entityStartIndex,
                        combinedBuffer.length)
                );
                // кусок entity занимает весь буфер
            } else if (hasNext && entityInfoKeyFound) {
                entityInfoSb.append(combinedBuffer);
            }
        }
        return new JSONObject(entityInfoSb.toString());
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
