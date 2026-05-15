package org.example.crochetapp.interfaces.recipes;

import org.example.crochetapp.domain.model.Recipe;
import org.example.crochetapp.domain.model.valueobjects.Image;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RecipieController {
    @GetMapping(value = "/recipes/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable int id) {
        Recipe recipe = recipeService.findById(id);
        Image img = recipe.getImage();
        if (img == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img.getContentType()))
                .body(new InputStreamResource(imageStorage.open(img)));
    }
}
