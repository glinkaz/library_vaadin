package com.example.application.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;

public class LoginItem  extends Div {

    public LoginItem() {
        getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("padding", "var(--lumo-space-l)");

        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();

        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(i18n);
        add(loginForm);
        // Prevent the example from stealing focus when browsing the documentation
        loginForm.getElement().setAttribute("no-autofocus", "");
    }
}
