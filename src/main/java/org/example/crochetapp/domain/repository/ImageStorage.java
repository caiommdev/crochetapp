package org.example.crochetapp.domain.repository;

import org.example.crochetapp.domain.model.valueobjects.Image;

import java.io.InputStream;

public interface ImageStorage {
    InputStream open(Image image);
    byte[] readAll(Image image);
    Image save(String name, InputStream content, String contentType);
    void delete(Image image);
}