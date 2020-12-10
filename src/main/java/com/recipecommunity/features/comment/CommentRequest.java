package com.recipecommunity.features.comment;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
/**
 * POJO class that contains payload required to edit or create new comment
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
public class CommentRequest implements Serializable {

    private static final long serialVersionUID = 12345222L;
    @NotBlank
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
