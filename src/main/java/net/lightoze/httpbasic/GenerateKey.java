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

import com.google.common.base.Strings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import javax.persistence.EntityManager;

/**
 * Page is responsible of
 *
 * @author Vladimir Kulev
 */
public class GenerateKey extends WebPage {
    private String message;
    private String publicKey;
    private String privateKey;
    private WebMarkupContainer formbox;

    public GenerateKey() {
        add(new Label("message", new PropertyModel<String>(this, "message")).setEscapeModelStrings(false));


        formbox = new WebMarkupContainer("formbox");
        Form form = new KeyForm("form");
        form.add(new TextField<String>("publicKey", new PropertyModel<String>(this, "publicKey")));
        form.add(new TextField<String>("privateKey", new PropertyModel<String>(this, "privateKey")));
        formbox.add(form);
        add(formbox);
    }

    public final class KeyForm extends Form {
        public KeyForm(String id) {
            super(id);
        }

        @Override
        protected void onSubmit() {
            publicKey = Strings.nullToEmpty(publicKey).trim();
            privateKey = Strings.nullToEmpty(privateKey).trim();
            if (publicKey.isEmpty() || privateKey.isEmpty()) {
                message = "Please provide your keys.";
                return;
            }
            UserKey key = new UserKey();
            EntityManager em = Application.emf.createEntityManager();
            try {
                key.setPublicKey(publicKey);
                key.setPrivateKey(privateKey);
                em.persist(key);
            } finally {
                em.close();
            }
            message = String.format("Success. Now go to the <a href='/encrypt.html?%d#%s'>next page</a>.", key.getId(), key.getPublicKey());
            clearInput();
            formbox.setVisible(false);
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
