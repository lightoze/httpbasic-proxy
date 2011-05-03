/*
 * Copyright 2008 Kindleit Technologies. All rights reserved. This file, all
 * proprietary knowledge and algorithms it details are the sole property of
 * Kindleit Technologies unless otherwise specified. The software this file
 * belong with is the confidential and proprietary information of Kindleit
 * Technologies. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Kindleit.
 */


package net.lightoze.httpbasic;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 * Page is responsible of
 *
 * @author Vladimir Kulev
 */
public class GenerateKey extends WebPage {
    private String message;
    private String publicKey;
    private String privateKey;
    private Form form;

    public GenerateKey() {

        add(new Label("message", new PropertyModel<String>(this, "message")));

        form = new Form("form");
        form.add(new TextField<String>("publicKey", new PropertyModel<String>(this, "publicKey")));
        form.add(new TextField<String>("privateKey", new PropertyModel<String>(this, "privateKey")));
        add(form);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (form.isSubmitted()) {
            message = "Keys: " + publicKey + " " + privateKey;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
