package com.example.application.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;

public class RegisterItem extends FormLayout {
    public RegisterItem(){

        Button buton = new Button("XDD");
        buton.addClickListener(e -> Notification.show("register"));
        add(buton);
    }
}
