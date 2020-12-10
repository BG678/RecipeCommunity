package com.recipecommunity.features.recipe;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
/**
 * POJO class that contains payload required to edit or create new recipe
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
public class RecipeRequest implements Serializable {
    private static final long serialVersionUID = 12345333L;

    @NotBlank
    private String title;
    @NotBlank
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
