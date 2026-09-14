package org.example.catalog.domain.valueobjects;

import org.example.catalog.domain.enums.StorageType;

public record Image(String name, String path, StorageType storage) {
}
